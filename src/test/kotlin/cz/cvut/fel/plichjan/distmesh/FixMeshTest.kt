package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import delaunay.Pnt
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.util.*

class FixMeshTest {
    @Test
    @Throws(Exception::class)
    fun testCallPrimitives() {
        // triangle
        val call = FixMesh().call(
            arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0)),
            arrayOf(intArrayOf(0, 1, 2), intArrayOf(0, 2, 1))
        )
        assertArrayEquals("Compare triangle i = 0", intArrayOf(0, 1, 2), call.t!![0])
        assertArrayEquals("Compare triangle i = 1", intArrayOf(2, 0, 1), call.t!![1])
    }

    @Test
    @Throws(Exception::class)
    fun testCall() {
        val p = Matlab.asPntList(arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0)))
        val t = listOf(intArrayOf(0, 1, 2), intArrayOf(0, 2, 1))

        val call = FixMesh().call(p, t)
        assertArrayEquals("Compare triangle i = 0", intArrayOf(0, 1, 2), call.t!![0])
        assertArrayEquals("Compare triangle i = 1", intArrayOf(2, 0, 1), call.t!![1])
    }

    @Test
    @Throws(Exception::class)
    fun testFixElementOrientation() {
        val p = Matlab.asPntList(arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0)))
        val t = listOf(intArrayOf(0, 1, 2), intArrayOf(0, 2, 1))

        FixMesh().fixElementOrientation(p, t)
        assertArrayEquals("Compare triangle i = 0", intArrayOf(0, 1, 2), t[0])
        assertArrayEquals("Compare triangle i = 1", intArrayOf(2, 0, 1), t[1])
    }
}
