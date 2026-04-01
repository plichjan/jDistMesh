package cz.cvut.fel.plichjan.distmesh.matlab

import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import delaunay.Pnt
import delaunay.Triangle
import delaunay.Triangulation
import org.apache.log4j.Logger
import java.util.*
import kotlin.math.nextUp

/**
 */
object Matlab {
    val logger: Logger = Logger.getLogger(Matlab::class.java)

    /** returns the distance from 1.0 to the next largest double-precision number */
    val EPS: Double = 1.0.nextUp() - 1.0

    @JvmStatic
    fun vector(first: Double, step: Double, last: Double): DoubleArray {
        val size = ((last - first) / step).toInt() + 1
        return DoubleArray(size) { i -> i * step + first }
    }

    @JvmStatic
    fun vector(i0: Int, iN: Int): IntArray {
        val length = iN - i0
        return IntArray(length) { i -> i + i0 }
    }

    @JvmStatic
    fun <E> filter(src: List<E>, test: ITest<E>): List<E> {
        val dst = ArrayList<E>(src.size)
        for (i in src.indices) {
            if (test.call(i, src[i])) {
                dst.add(src[i])
            }
        }
        return dst
    }

    @JvmStatic
    fun <E> filterSelf(src: MutableList<E>, test: ITest<E>) {
        var i = 0
        val iterator = src.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (!test.call(i, next)) {
                iterator.remove()
            }
            i++
        }
    }

    @JvmStatic
    fun <E> filter(src: List<E>, ix: IntArray): List<E> {
        val dst = ArrayList<E>(ix.size)
        for (i in ix) {
            dst.add(src[i])
        }
        return dst
    }

    @JvmStatic
    fun delaunayn2(p: List<Pnt>, bbox: Array<DoubleArray>, fd: IDistanceFunction, geps: Double): List<IntArray> {
        val lb = Pnt(*bbox[0])
        val tr = Pnt(*bbox[1])
        val wh = tr.subtract(lb)
        val tri = Triangle(lb.subtract(wh), lb.add(3.0, Pnt(wh.coord(0), 0.0)), lb.add(3.0, Pnt(0.0, wh.coord(1))))
        val triangulation = Triangulation(tri)
        for (i in p.indices) {
            val point = p[i]
            point.index = i
            if (fd.call(point.coord(0), point.coord(1)) < geps) {
                triangulation.delaunayPlace(point)
            }
        }

        val ints = ArrayList<IntArray>(triangulation.size)
        for (triangle in triangulation) {
            // Check if the triangle is disjoint from the initial large triangle
            var overlapsInitial = false
            for (v in tri) {
                if (triangle.contains(v)) {
                    overlapsInitial = true
                    break
                }
            }
            if (!overlapsInitial) {
                val p1 = triangle[0]
                val p2 = triangle[1]
                val p3 = triangle[2]
                ints.add(intArrayOf(p1.index, p2.index, p3.index))
            }
        }
        if (logger.isDebugEnabled) {
            logger.debug("initEquiTriangles done. ${p.size} tr: ${ints.size}")
        }
        return ints
    }

    @JvmStatic
    fun asPntList(p: Array<DoubleArray>): List<Pnt> {
        return p.map { Pnt(*it) }
    }

    @JvmStatic
    fun toArray(p: List<Pnt>): Array<DoubleArray> {
        return Array(p.size) { i -> p[i].getData() }
    }

    @JvmStatic
    fun computeCentroids(p: List<Pnt>, t: List<IntArray>): List<Pnt> {
        val pmid = ArrayList<Pnt>(t.size)
        for (tr in t) {
            var sum = Pnt(*DoubleArray(p[0].dimension()))
            for (i in tr) {
                sum = sum.add(p[i])
            }
            pmid.add(sum.scale(1.0 / tr.size))
        }
        return pmid
    }

    @JvmStatic
    fun computeCentroids(p: Array<DoubleArray>, t: Array<IntArray>): Array<DoubleArray> {
        return toArray(computeCentroids(asPntList(p), t.toList()))
    }
}
