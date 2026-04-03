package cz.cvut.fel.plichjan.distmesh.matlab

import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle
import delaunay.Pnt
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class MatlabTest {
    @Test
    @Throws(Exception::class)
    fun testVector() {
        val v100 = Matlab.vector(0.0, 0.01, 1.0)
        assertEquals(101, v100.size)
        assertEquals(0.0, v100[0], 1e-9)
        assertEquals(1.0, v100[100], 1e-9)

        val vm100 = Matlab.vector(1.0, -0.01, 0.0)
        assertEquals(101, vm100.size)
        assertEquals(1.0, vm100[0], 1e-9)
        assertEquals(0.0, vm100[100], 1e-9)

        val v1000 = Matlab.vector(0.0, 0.001, 1.0)
        assertEquals(1001, v1000.size)
        assertEquals(0.0, v1000[0], 1e-9)
        assertEquals(1.0, v1000[1000], 1e-9)

        val v4 = Matlab.vector(0.0, 0.3, 1.0)
        assertArrayEquals(doubleArrayOf(0.0, 0.3, 0.6, 0.9), v4, 0.01)
    }

    @Test
    @Throws(Exception::class)
    fun testFilterSelf() {
        val src = listOf(0, 1, 2, 3, 4, 5)

        val src1 = ArrayList(src)
        Matlab.filterSelf(src1) { _, item -> item > 3 }
        assertEquals(listOf(4, 5), src1)

        val src2 = ArrayList(src)
        Matlab.filterSelf(src2) { _, item -> item > 3 || item < 2 }
        assertEquals(listOf(0, 1, 4, 5), src2)

        val src3 = ArrayList(listOf(1, 1, 1, 1, 0, 1, 0))
        Matlab.filterSelf(src3) { i, _ -> i > 3 }
        assertEquals(listOf(0, 1, 0), src3)
    }

    @Test
    @Throws(Exception::class)
    fun testFilter() {
        val src = listOf(0, 1, 2, 3, 4, 5)

        assertEquals(listOf(4, 5), Matlab.filter(src) { _, item -> item > 3 })

        assertEquals(listOf(0, 1, 4, 5), Matlab.filter(src) { _, item -> item > 3 || item < 2 })

        assertEquals(listOf(0, 1, 0), Matlab.filter(listOf(1, 1, 1, 1, 0, 1, 0)) { i, _ -> i > 3 })
    }

    @Test
    @Throws(Exception::class)
    fun testDelaunayn2() {
        val triangles = Matlab.delaunayn2(
            listOf(
                Pnt(0.0, 0.0),
                Pnt(1.0, 0.0),
                Pnt(1.0, 1.0),
                Pnt(0.0, 2.0)
            ), arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(2.0, 2.0)), DRectangle(0.0, 1.0, 0.0, 2.0), 0.01
        )

        assertEquals(2, triangles.size)
    }
}
