package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.IViewer
import cz.cvut.fel.plichjan.distmesh.inputs.*
import cz.cvut.fel.plichjan.distmesh.matlab.DistMeshBar
import cz.cvut.fel.plichjan.distmesh.matlab.ITest
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import cz.cvut.fel.plichjan.distmesh.result.FixedMesh
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import org.apache.log4j.Logger
import java.awt.geom.Point2D
import java.io.PrintStream
import java.util.*
import kotlin.math.sqrt

/**
 * 2-D Mesh Generator using Distance Functions.
 * Port of DistMesh (MATLAB) to Kotlin.
 */
class DistMesh2D {
    var dptol = 0.001
    private var viewer: IViewer? = null

    companion object {
        val logger: Logger = Logger.getLogger(DistMesh2D::class.java)

        @JvmStatic
        fun isSomeEdgeCross(p: List<Pnt>, bars: List<DistMeshBar>): Boolean {
            for (i in bars.indices) {
                val bar0 = bars[i]
                val r = p[bar0.b].subtract(p[bar0.a])
                for (j in i + 1 until bars.size) {
                    val bar = bars[j]
                    if (bar.a == bar0.a || bar.a == bar0.b || bar.b == bar0.a || bar.b == bar0.b) {
                        continue
                    }
                    val s = p[bar.b].subtract(p[bar.a])
                    val qp = p[bar.a].subtract(p[bar0.a])
                    val rxs = Pnt.determinant(arrayOf(r, s))
                    if (rxs > Matlab.EPS) {
                        val t = Pnt.determinant(arrayOf(qp, s)) / rxs
                        val u = Pnt.determinant(arrayOf(qp, r)) / rxs
                        if (t in 0.0..1.0 && u in 0.0..1.0) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        @JvmStatic
        fun minPointsDistance(p: List<Pnt>): Double {
            var d = Double.POSITIVE_INFINITY
            for (i in p.indices) {
                val a = p[i]
                for (j in 0 until i) {
                    val distance = a.subtract(p[j]).magnitude()
                    if (distance < d) {
                        d = distance
                    }
                }
            }
            return d
        }

        @JvmStatic
        fun createFtot(bars: List<DistMeshBar>, n: Int, nfix: Int): List<Pnt> {
            val ftot = MutableList(n) { Pnt(0.0, 0.0) }
            for (bar in bars) {
                val fvec = bar.fvec ?: continue
                ftot[bar.a] = ftot[bar.a].add(fvec)
                ftot[bar.b] = ftot[bar.b].add(-1.0, fvec)
            }
            for (i in 0 until nfix) {
                ftot[i] = Pnt(0.0, 0.0)
            }
            return ftot
        }

        @JvmStatic
        fun setupForces(bars: List<DistMeshBar>) {
            for (bar in bars) {
                bar.f = maxOf(bar.l0 - bar.l, 0.0)
                val fl = bar.f / bar.l
                bar.fvec = bar.barvec?.scale(fl)
            }
        }

        @JvmStatic
        fun setupLengths(p: List<Pnt>, bars: List<DistMeshBar>, fh: IEdgeLengthFunction, fscale: Double) {
            var sumL2 = 0.0
            var sumHbars2 = 0.0
            for (bar in bars) {
                val pA = p[bar.a]
                val pB = p[bar.b]

                val barvec = pA.add(-1.0, pB)
                bar.barvec = barvec

                val l = barvec.magnitude()
                bar.l = l
                sumL2 += l * l

                val midp = pA.add(pB).scale(0.5)
                val hbar = fh.call(midp.coord(0), midp.coord(1))
                sumHbars2 += hbar * hbar
                bar.hbar = hbar
            }
            val scaleFactor = sqrt(sumL2 / sumHbars2)
            for (bar in bars) {
                bar.l0 = bar.hbar * fscale * scaleFactor
            }
        }

        @JvmStatic
        fun resetBars(bars: MutableList<DistMeshBar>, t: List<IntArray>) {
            val barsSet = TreeSet<DistMeshBar>()
            for (tr in t) {
                barsSet.add(DistMeshBar(tr[0], tr[1]))
                barsSet.add(DistMeshBar(tr[0], tr[2]))
                barsSet.add(DistMeshBar(tr[1], tr[2]))
            }
            bars.clear()
            bars.addAll(barsSet)
        }

        @JvmStatic
        fun keepInteriorTriangles(p: List<Pnt>, t: MutableList<IntArray>, fd: IDistanceFunction, geps: Double) {
            val pmid = Matlab.computeCentroids(p, t)
            Matlab.filterSelf(t) { i, _ ->
                fd.call(pmid[i].coord(0), pmid[i].coord(1)) < -geps
            }
            if (logger.isDebugEnabled) {
                logger.debug("keepInteriorTriangles done.")
            }
        }

        @JvmStatic
        fun anyLargeMovement(p: List<Pnt>, pold: List<Pnt>, h0: Double, ttol: Double): Boolean {
            if (p.size < pold.size) return true
            for (i in p.indices) {
                if (p[i].subtract(pold[i]).magnitude() / h0 > ttol) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun addFixPoints(p: MutableList<Pnt>, pfix: List<Pnt>?, h0: Double): Int {
            pfix?.let { p.removeAll(it) }
            val pfixSet = pfix?.let { LinkedHashSet(it) } ?: LinkedHashSet<Pnt>()
            val nfix = pfixSet.size
            p.addAll(0, pfixSet)
            if (logger.isDebugEnabled) {
                logger.debug("addFixPoints done. ${p.size}")
            }
            return nfix
        }

        @JvmStatic
        fun rejectionMethod(p: MutableList<Pnt>, fh: IEdgeLengthFunction) {
            val r0 = p.map { item ->
                val v = fh.call(item.coord(0), item.coord(1))
                1.0 / (v * v)
            }
            val maxR0 = if (r0.isEmpty()) 0.0 else Collections.max(r0)
            Matlab.filterSelf(p) { i, _ ->
                Math.random() < r0[i] / maxR0
            }
            if (logger.isDebugEnabled) {
                logger.debug("rejectionMethod done. ${p.size}")
            }
        }

        @JvmStatic
        fun removeOutsidePoints(p: MutableList<Pnt>, fd: IDistanceFunction, geps: Double) {
            Matlab.filterSelf(p) { _, item ->
                fd.call(item.coord(0), item.coord(1)) < geps
            }
            if (logger.isDebugEnabled) {
                logger.debug("removeOutsidePoints done. ${p.size}")
            }
        }

        @JvmStatic
        fun initEquiTriangles(h0: Double, bbox: Array<DoubleArray>): MutableList<Pnt> {
            val x = Matlab.vector(bbox[0][0], h0, bbox[1][0])
            val y = Matlab.vector(bbox[0][1], h0 * Math.sqrt(3.0) / 2.0, bbox[1][1])

            val p = ArrayList<Pnt>(x.size * y.size)
            for (aX in x) {
                for (j in y.indices) {
                    if (j % 2 == 0) {
                        p.add(Pnt(aX, y[j]))
                    } else {
                        p.add(Pnt(aX + h0 / 2.0, y[j]))
                    }
                }
            }
            if (logger.isDebugEnabled) {
                logger.debug("initEquiTriangles done. ${p.size}")
            }
            return p
        }

        @JvmStatic
        fun anyTooClose(bars: List<DistMeshBar>): Boolean {
            return bars.any { it.l0 > 2.0 * it.l }
        }

        @JvmStatic
        fun removeTooClosePoints(p: MutableList<Pnt>, bars: List<DistMeshBar>, nfix: Int) {
            val indices = TreeSet<Int>(Collections.reverseOrder())
            for (bar in bars) {
                if (bar.l0 > 2.0 * bar.l) {
                    if (bar.a >= nfix) indices.add(bar.a)
                    if (bar.b >= nfix) indices.add(bar.b)
                }
            }
            for (i in indices) {
                p.removeAt(i)
            }
        }

        @JvmStatic
        fun inf(p: List<Pnt>): List<Pnt> {
            return Collections.nCopies(p.size, Pnt(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY))
        }

        @JvmStatic
        fun printP(pList: List<Pnt>, ps: PrintStream?) {
            val out = ps ?: System.`out`
            out.println(pList.size)
            for (p in pList) {
                out.format("   % 6.4g   % 6.4g 0.0\n", p.coord(0), p.coord(1))
            }
        }
    }

    fun setViewer(viewer: IViewer) {
        this.viewer = viewer
    }

    fun call(fd: IDistanceFunction, fh: IEdgeLengthFunction, h0: Double, bbox: Array<DoubleArray>, pfix: Array<DoubleArray>): Mesh {
        return call(fd, fh, h0, bbox, Matlab.asPntList(pfix))
    }

    fun call(fd: IDistanceFunction, fh: IEdgeLengthFunction, h0: Double, bbox: Array<DoubleArray>, pfix: List<Pnt>?): Mesh {
        val ttol = 0.1
        val fscale = 1.2
        val deltat = 0.2
        val geps = 0.001 * h0
        val deps = sqrt(Matlab.EPS) * h0
        val densityctrlfreq = 30

        val p = initEquiTriangles(h0, bbox)
        removeOutsidePoints(p, fd, geps)
        rejectionMethod(p, fh)
        val nfix = addFixPoints(p, pfix, geps)
        var n = p.size
        var pold = inf(p)

        val bars = ArrayList<DistMeshBar>()
        var t = mutableListOf<IntArray>()

        outer@ while (true) {
            if (anyLargeMovement(p, pold, h0, ttol)) {
                pold = ArrayList(p)
                t = Matlab.delaunayn2(p, bbox, fd, geps / 100.0).toMutableList()
                keepInteriorTriangles(p, t, fd, geps)
                resetBars(bars, t)
                drawnow(p, t, bbox)
            }

            setupLengths(p, bars, fh, fscale)

            if (p.size % densityctrlfreq == 0 && anyTooClose(bars)) {
                removeTooClosePoints(p, bars, nfix)
                n = p.size
                pold = inf(p)
                continue
            }

            setupForces(bars)
            val ftot = createFtot(bars, n, nfix)

            for (i in p.indices) {
                p[i] = p[i].add(deltat, ftot[i])
            }

            val dList = ArrayList<Double>(p.size)
            for (i in p.indices) {
                val a = p[i]
                val d = fd.call(a.coord(0), a.coord(1))
                dList.add(d)
                if (d > 0 && i >= nfix) {
                    val dgradx = (fd.call(a.coord(0) + deps, a.coord(1)) - d) / deps
                    val dgrady = (fd.call(a.coord(0), a.coord(1) + deps) - d) / deps
                    val dgrad2 = dgradx * dgradx + dgrady * dgrady
                    p[i] = a.add(-1.0, Pnt(d * dgradx / dgrad2, d * dgrady / dgrad2))
                }
            }
            if (isSomeEdgeCross(p, bars)) {
                pold = inf(p)
            }

            drawnow(p, t, bbox)

            for (i in nfix until p.size) {
                val d = dList[i]
                if (d < -geps && deltat * ftot[i].magnitude() / h0 > dptol) {
                    continue@outer
                }
            }
            break
        }

        t = Matlab.delaunayn2(p, bbox, fd, geps / 100.0).toMutableList()
        keepInteriorTriangles(p, t, fd, geps)

        val mesh = FixMesh().call(p, t)
        viewer?.drawMesh(mesh)
        return mesh
    }

    private fun drawnow(p: List<Pnt>, t: List<IntArray>, bbox: Array<DoubleArray>) {
        viewer?.let {
            val pdt = p.map { point -> Point2D.Double(point.coord(0), point.coord(1)) }
            it.setNewPoints(pdt, t, bbox)
        }
    }
}
