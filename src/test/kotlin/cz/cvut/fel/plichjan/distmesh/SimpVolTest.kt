package cz.cvut.fel.plichjan.distmesh

import delaunay.Pnt
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SimpVolTest {
    @Test
    @Throws(Exception::class)
    fun testCallPrimitives() {
        // triangle
        val volumes = SimpVol().call(
            arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0)),
            arrayOf(intArrayOf(0, 1, 2), intArrayOf(0, 2, 1))
        )
        assertArrayEquals(doubleArrayOf(0.5, -0.5), volumes.toDoubleArray(), 1e-6)
    }

    @Test
    @Throws(Exception::class)
    fun testCall() {
        // triangle
        val volumes = SimpVol().call(
            listOf(Pnt(0.0, 0.0), Pnt(1.0, 0.0), Pnt(0.0, 1.0)),
            listOf(intArrayOf(0, 1, 2), intArrayOf(0, 2, 1))
        )
        assertArrayEquals(doubleArrayOf(0.5, -0.5), volumes.toDoubleArray(), 1e-6)
    }

    @Test
    @Throws(Exception::class)
    fun testOneVolume() {
        // triangle
        assertEquals(
            0.5, SimpVol().oneVolume(
                listOf(Pnt(0.0, 0.0), Pnt(1.0, 0.0), Pnt(0.0, 1.0)),
                intArrayOf(0, 1, 2)
            ), 1e-6
        )

        // tetrahedron
        assertEquals(
            0.5 / 3, SimpVol().oneVolume(
                listOf(Pnt(0.0, 0.0, 0.0), Pnt(1.0, 0.0, 0.0), Pnt(0.0, 1.0, 0.0), Pnt(0.0, 0.0, -1.0)),
                intArrayOf(0, 1, 2, 3)
            ), 1e-6
        )
        assertEquals(
            0.5 / 3, SimpVol().oneVolume(
                listOf(Pnt(0.0, 0.0, 0.0), Pnt(0.0, 1.0, 0.0), Pnt(1.0, 0.0, 0.0), Pnt(0.0, 0.0, 1.0)),
                intArrayOf(0, 1, 2, 3)
            ), 1e-6
        )
    }
}
