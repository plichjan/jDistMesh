package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import java.util.TreeSet

/**
 * Find boundary edges from triangular mesh
 */
class BoundEdges(private val counterClock: Boolean = false) {

    fun call(mesh: Mesh): List<BoundBar> {
        val p = mesh.p ?: return emptyList()
        val t = mesh.t ?: return emptyList()
        return call(p, t)
    }

    fun call(p: Array<DoubleArray>, t: Array<IntArray>): List<BoundBar> {
        val pnts = Matlab.asPntList(p)

        // Form all edges, non-duplicates are boundary edges
        val n = if (p.isNotEmpty()) p[0].size + 1 else 0
        val e = getBoundaryEdges(t, n)

        // Orientation
        setupOrientation(pnts, e)

        return e
    }

    private fun setupOrientation(pnts: List<Pnt>, e: List<BoundBar>) {
        for (bar in e) {
            val v1 = pnts[bar.b].subtract(pnts[bar.a])
            val v2 = pnts[bar.c].subtract(pnts[bar.a])
            // cross > 0 => C is on left side
            val cross = v1.coord(0) * v2.coord(1) - v1.coord(1) * v2.coord(0)
            if ((cross > 0 && !counterClock) || (cross < 0 && counterClock)) {
                val a = bar.a
                bar.a = bar.b
                bar.b = a
            }
        }
    }

    private fun getBoundaryEdges(t: Array<IntArray>, n: Int): List<BoundBar> {
        val edges = TreeSet<BoundBar>()
        val innerEdges = TreeSet<BoundBar>()
        for (i in t.indices) {
            val tr = t[i]
            for (j in 0 until n) {
                val a = tr[j]
                val b = tr[(j + 1) % n]
                val c = tr[(j + 2) % n]
                val bar = createBar(a, b, c, i)
                bar.ix = j
                if (!edges.add(bar)) {
                    innerEdges.add(bar)
                }
            }
        }
        edges.removeAll(innerEdges)
        return edges.toList()
    }

    private fun createBar(a: Int, b: Int, c: Int, trIndex: Int): BoundBar {
        return BoundBar(a, b, c, trIndex)
    }
}
