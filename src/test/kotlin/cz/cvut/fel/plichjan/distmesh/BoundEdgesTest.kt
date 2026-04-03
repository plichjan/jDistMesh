package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class BoundEdgesTest {
    @Test
    @Throws(Exception::class)
    fun testCallMash() {
        // square
        val mesh = Mesh()
        mesh.p = arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0, 1.0))
        mesh.t = arrayOf(intArrayOf(0, 1, 2), intArrayOf(1, 3, 2))
        val edges4 = BoundEdges().call(mesh)

        println("edges4 = $edges4")
        assertEquals(4, edges4.size)

        assertEquals(
            TreeSet(
                listOf(
                    MyBar(1, 0),
                    MyBar(0, 2),
                    MyBar(2, 3),
                    MyBar(3, 1)
                )
            ), TreeSet(edges4)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testCall() {
        val edges = BoundEdges(true).call(
            arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0)),
            arrayOf(intArrayOf(0, 1, 2))
        )
        println("edges = $edges")
        assertEquals(3, edges.size)

        assertEquals(
            TreeSet(
                listOf(
                    MyBar(0, 1),
                    MyBar(1, 2),
                    MyBar(2, 0)
                )
            ), TreeSet(edges)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testCallClockwise() {
        val edges3 = BoundEdges().call(
            arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0)),
            arrayOf(intArrayOf(0, 1, 2))
        )
        println("edges3 = $edges3")
        assertEquals(3, edges3.size)

        assertEquals(
            TreeSet(
                listOf(
                    MyBar(1, 0),
                    MyBar(2, 1),
                    MyBar(0, 2)
                )
            ), TreeSet(edges3)
        )

        // square
        val edges4 = BoundEdges().call(
            arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0), doubleArrayOf(1.0, 1.0)),
            arrayOf(intArrayOf(0, 1, 2), intArrayOf(1, 3, 2))
        )
        println("edges4 = $edges4")
        assertEquals(4, edges4.size)

        assertEquals(
            TreeSet(
                listOf(
                    MyBar(1, 0),
                    MyBar(0, 2),
                    MyBar(2, 3),
                    MyBar(3, 1)
                )
            ), TreeSet(edges4)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testCallSquareWithHole() {
        // square with hole
        val edges8 = BoundEdges().call(
            arrayOf(
                doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(1.0, 1.0), doubleArrayOf(0.0, 1.0),
                doubleArrayOf(1.0 / 2, 1.0 / 4), doubleArrayOf(3.0 / 4, 1.0 / 2), doubleArrayOf(1.0 / 2, 3.0 / 4),
                doubleArrayOf(1.0 / 2, 1.0 / 2), doubleArrayOf(1.0 / 4, 1.0 / 2)
            ),
            arrayOf(
                intArrayOf(0, 1, 4), intArrayOf(0, 4, 7), intArrayOf(0, 7, 3), intArrayOf(3, 7, 6),
                intArrayOf(3, 6, 2), intArrayOf(2, 6, 5), intArrayOf(2, 5, 1), intArrayOf(1, 5, 4)
            )
        )
        println("edges8 = $edges8")
        assertEquals(8, edges8.size)

        assertEquals(
            TreeSet(
                listOf(
                    MyBar(1, 0),
                    MyBar(0, 3),
                    MyBar(3, 2),
                    MyBar(2, 1),
                    // hole
                    MyBar(7, 4),
                    MyBar(6, 7),
                    MyBar(5, 6),
                    MyBar(4, 5)
                )
            ), TreeSet(edges8)
        )
    }

    @Test
    @Throws(Exception::class)
    fun testSetupOrientation() {
        val bar = BoundBar(0, 1, 2)
        val pnts = listOf(Pnt(0.0, 0.0), Pnt(1.0, 0.0), Pnt(0.0, 1.0))

        // clockwise
        BoundEdges().setupOrientation(pnts, listOf(bar))
        assertEquals(1, bar.a)
        assertEquals(0, bar.b)

        // counterclockwise
        BoundEdges(true).setupOrientation(pnts, listOf(bar))
        assertEquals(0, bar.a)
        assertEquals(1, bar.b)
    }

    @Test
    @Throws(Exception::class)
    fun testGetBoundaryEdges() {
        val edges = BoundEdges().getBoundaryEdges(arrayOf(intArrayOf(0, 1, 2)), 3)

        assertEquals(3, edges.size)

        assertEquals(
            TreeSet(
                listOf(
                    BoundBar(0, 1),
                    BoundBar(1, 2),
                    BoundBar(2, 0)
                )
            ), TreeSet(edges)
        )
    }

    private class MyBar(a: Int, b: Int) : BoundBar(a, b) {
        init {
            this.a = a
            this.b = b
        }
    }
}
