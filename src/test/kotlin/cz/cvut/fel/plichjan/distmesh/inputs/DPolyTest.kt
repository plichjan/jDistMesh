package cz.cvut.fel.plichjan.distmesh.inputs

import delaunay.Pnt
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class DPolyTest {
    @Test
    @Throws(Exception::class)
    fun testCallArr() {
        val dPoly = DPoly(arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 1.0)))
        assertEquals(1.0, dPoly.call(2.0, 1.0), 0.0001)
        assertEquals(1.0, dPoly.call(0.0, -1.0), 0.0001)
        assertEquals(sqrt(2.0), dPoly.call(-1.0, -1.0), 0.0001)
        assertEquals(sqrt(2.0) / 2.0, dPoly.call(1.0, 0.0), 0.0001)
        assertEquals(sqrt(2.0) / 2.0, dPoly.call(0.0, 1.0), 0.0001)
    }

    @Test
    @Throws(Exception::class)
    fun testCall() {
        val dPoly = DPoly(listOf(Pnt(0.0, 0.0), Pnt(1.0, 1.0)))
        assertEquals(1.0, dPoly.call(2.0, 1.0), 0.0001)
        assertEquals(1.0, dPoly.call(0.0, -1.0), 0.0001)
        assertEquals(sqrt(2.0), dPoly.call(-1.0, -1.0), 0.0001)
        assertEquals(sqrt(2.0) / 2.0, dPoly.call(1.0, 0.0), 0.0001)
        assertEquals(sqrt(2.0) / 2.0, dPoly.call(0.0, 1.0), 0.0001)
    }

    @Test
    @Throws(Exception::class)
    fun testCall0() {
        val dPoly = DPoly(listOf(Pnt(0.0, 0.0), Pnt(1.0, 0.0)))
        assertEquals(1.0, dPoly.call(-1.0, 0.0), 0.0001)
        assertEquals(1.0, dPoly.call(2.0, 0.0), 0.0001)
        assertEquals(1.0, dPoly.call(0.0, 1.0), 0.0001)
        assertEquals(1.0, dPoly.call(0.0, -1.0), 0.0001)
        assertEquals(1.0, dPoly.call(1.0, 1.0), 0.0001)
        assertEquals(1.0, dPoly.call(1.0, -1.0), 0.0001)
        assertEquals(1.0, dPoly.call(0.5, 1.0), 0.0001)
        assertEquals(1.0, dPoly.call(0.5, -1.0), 0.0001)
    }
}
