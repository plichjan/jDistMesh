package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle
import cz.cvut.fel.plichjan.distmesh.inputs.HUniform
import cz.cvut.fel.plichjan.distmesh.matlab.DistMeshBar
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import delaunay.Pnt
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class DistMesh2DTest {

    @Test
    @Throws(Exception::class)
    fun testCall() {
        val mesh = DistMesh2D().call(
            DCircle(0.0, 0.0, 1.0),
            HUniform(),
            0.5, arrayOf(doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, 1.0)),
            ArrayList()
        )
        println(mesh.toTsin())
    }

    @Test
    @Throws(Exception::class)
    fun testCreateFtot() {
        val fScale = 1.2

        val bars = listOf(DistMeshBar(0, 1), DistMeshBar(0, 2), DistMeshBar(0, 3))
        val p = listOf(Pnt(0.0, 0.0), Pnt(0.0, 1.0), Pnt(0.0, 2.0), Pnt(0.0, 3.0))
        DistMesh2D.setupLengths(p, bars, HUniform(), fScale)
        DistMesh2D.setupForces(bars)
        val ftot = DistMesh2D.createFtot(bars, p.size, 0)

        assertEquals(4, ftot.size)
        val expectFtot = listOf(Pnt(0.0, -2.1846), Pnt(0.0, 1.5923), Pnt(0.0, 0.5923), Pnt(0.0, 0.0))
        for (i in expectFtot.indices) {
            val expect = expectFtot[i]
            assertArrayEquals("i = $i", expect.getData(), ftot[i].getData(), 0.0001)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSetupForces() {
        val fScale = 1.2

        val bars = listOf(DistMeshBar(0, 1), DistMeshBar(0, 2), DistMeshBar(0, 3))
        DistMesh2D.setupLengths(
            listOf(Pnt(0.0, 0.0), Pnt(0.0, 1.0), Pnt(0.0, 2.0), Pnt(0.0, 3.0)),
            bars, HUniform(), fScale
        )
        DistMesh2D.setupForces(bars)

        assertEquals(1.5923, bars[0].f, 0.0001)
        assertEquals(0.5923, bars[1].f, 0.0001)
        assertEquals(0.0000, bars[2].f, 0.0001)
        assertArrayEquals(Pnt(0.0, -1.5923).getData(), bars[0].fvec!!.getData(), 0.0001)
        assertArrayEquals(Pnt(0.0, -0.5923).getData(), bars[1].fvec!!.getData(), 0.0001)
        assertArrayEquals(Pnt(0.0, -0.0000).getData(), bars[2].fvec!!.getData(), 0.0001)
    }

    @Test
    @Throws(Exception::class)
    fun testSetupLengths() {
        val fScale = 1.2

        var bars = listOf(DistMeshBar(0, 1))
        DistMesh2D.setupLengths(listOf(Pnt(0.0, 0.0), Pnt(0.0, 1.0)), bars, HUniform(), fScale)

        val bar = bars[0]
        assertArrayEquals(Pnt(0.0, -1.0).getData(), bar.barvec!!.getData(), Matlab.EPS)
        assertEquals(1.0, bar.l, Matlab.EPS)
        assertEquals(1.0, bar.hbar, Matlab.EPS)
        assertEquals(1.2, bar.l0, Matlab.EPS)

        bars = listOf(DistMeshBar(0, 1), DistMeshBar(0, 2), DistMeshBar(0, 3))
        DistMesh2D.setupLengths(
            listOf(Pnt(0.0, 0.0), Pnt(0.0, 1.0), Pnt(0.0, 2.0), Pnt(0.0, 3.0)),
            bars, HUniform(), fScale
        )

        assertArrayEquals(Pnt(0.0, -1.0).getData(), bars[0].barvec!!.getData(), Matlab.EPS)
        assertArrayEquals(Pnt(0.0, -2.0).getData(), bars[1].barvec!!.getData(), Matlab.EPS)
        assertArrayEquals(Pnt(0.0, -3.0).getData(), bars[2].barvec!!.getData(), Matlab.EPS)
        assertEquals(1.0, bars[0].l, Matlab.EPS)
        assertEquals(2.0, bars[1].l, Matlab.EPS)
        assertEquals(3.0, bars[2].l, Matlab.EPS)
        assertEquals(1.0, bars[0].hbar, Matlab.EPS)
        assertEquals(1.0, bars[1].hbar, Matlab.EPS)
        assertEquals(1.0, bars[2].hbar, Matlab.EPS)
        assertEquals(2.5923, bars[0].l0, 0.0001)
        assertEquals(2.5923, bars[1].l0, 0.0001)
        assertEquals(2.5923, bars[2].l0, 0.0001)
    }

    @Test
    @Throws(Exception::class)
    fun testResetBars() {
        val t = ArrayList<IntArray>()
        t.add(intArrayOf(0, 1, 2))
        t.add(intArrayOf(0, 2, 3))

        val bars = ArrayList<DistMeshBar>()
        DistMesh2D.resetBars(bars, t)

        assertEquals(
            listOf(
                DistMeshBar(0, 1),
                DistMeshBar(0, 2),
                DistMeshBar(0, 3),
                DistMeshBar(1, 2),
                DistMeshBar(2, 3)
            ), bars
        )
    }

    @Test
    @Throws(Exception::class)
    fun testKeepInteriorTriangles() {
        val fd = DDiff(DRectangle(0.0, 2.0, 0.0, 2.0), DRectangle(0.0, 1.0, 0.0, 1.0))
        val t = ArrayList<IntArray>()

        t.add(intArrayOf(0, 1, 2))
        DistMesh2D.keepInteriorTriangles(listOf(Pnt(0.0, 0.0), Pnt(1.0, 0.0), Pnt(0.0, 1.0)), t, fd, Matlab.EPS)
        assertTrue(t.isEmpty())

        t.clear()
        t.add(intArrayOf(0, 1, 2))
        DistMesh2D.keepInteriorTriangles(listOf(Pnt(1.0, 0.0), Pnt(2.0, 0.0), Pnt(1.0, 1.0)), t, fd, Matlab.EPS)
        assertFalse(t.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun testAnyLargeMovement() {
        assertFalse(
            DistMesh2D.anyLargeMovement(
                listOf(Pnt(0.0, 0.0)),
                listOf(Pnt(0.01, 0.0)),
                1.0, 0.1
            )
        )

        assertTrue(
            DistMesh2D.anyLargeMovement(
                listOf(Pnt(0.0, 0.0)),
                listOf(Pnt(0.2, 0.0)),
                1.0, 0.1
            )
        )

        assertTrue(
            DistMesh2D.anyLargeMovement(
                listOf(Pnt(0.0, 0.0)),
                listOf(Pnt(0.01, 0.0), Pnt(1.0, 1.0)),
                1.0, 0.1
            )
        )

        assertTrue(
            DistMesh2D.anyLargeMovement(
                listOf(Pnt(0.0, 0.0), Pnt(1.0, 2.0)),
                listOf(Pnt(0.01, 0.0), Pnt(1.0, 1.0)),
                1.0, 0.1
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun testAddFixPoints() {
        val h0 = 1e-6
        val pold = listOf(Pnt(0.0, 0.0), Pnt(0.5, 0.5), Pnt(0.0, 1.0))

        val p = ArrayList<Pnt>(pold)
        // no fix points
        var nfix = DistMesh2D.addFixPoints(p, null, h0)
        assertEquals(pold, p)
        assertEquals(0, nfix)

        p.clear()
        p.addAll(pold)
        val pfix = listOf(Pnt(0.0, 1.0), Pnt(0.0, 1.0))
        // two same fix points
        nfix = DistMesh2D.addFixPoints(p, pfix, h0)
        val pnew = listOf(Pnt(0.0, 1.0), Pnt(0.0, 0.0), Pnt(0.5, 0.5))
        assertEquals(pnew, p)
        assertEquals(1, nfix)

        p.clear()
        p.addAll(pold)
        val pfixNew = listOf(Pnt(2.0, 0.0))
        // new fix point
        nfix = DistMesh2D.addFixPoints(p, pfixNew, h0)
        val pnewFix = ArrayList(pfixNew)
        pnewFix.addAll(pold)
        assertEquals(pnewFix, p)
        assertEquals(1, nfix)
    }

    @Test
    @Throws(Exception::class)
    fun testRejectionMethod() {
        val pold = listOf(Pnt(0.0, 0.0), Pnt(0.5, 0.5), Pnt(0.0, 1.0))
        val p = ArrayList(pold)
        DistMesh2D.rejectionMethod(p, HUniform())
        assertEquals("HUniform keeps all points.", pold, p)
    }

    @Test
    @Throws(Exception::class)
    fun testRemoveOutsidePoints() {
        val fd = DCircle(0.0, 0.0, 1.0)
        val insidePoints = listOf(Pnt(0.0, 0.0), Pnt(0.5, 0.5), Pnt(0.0, 1.0))
        val outsidePoints = listOf(Pnt(1.0, 1.0), Pnt(0.5, 1.0), Pnt(2.0, 1.0))

        val p = ArrayList(insidePoints)
        p.addAll(outsidePoints)

        DistMesh2D.removeOutsidePoints(p, fd, Matlab.EPS)
        assertEquals(insidePoints, p)
    }

    @Test
    @Throws(Exception::class)
    fun testInitEquiTriangles() {
        var p = DistMesh2D.initEquiTriangles(1.0, arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 5.0)))
        assertEquals("Size by Matlab", 24, p.size)
        // points
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.0, 0.0), p[0].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.5, 0.8660), p[1].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.0, 1.7321), p[2].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.5, 2.5981), p[3].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.0, 3.4641), p[4].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.5, 4.3301), p[5].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(1.0, 0.0), p[6].getData(), 0.0001)
        // last
        assertArrayEquals("Content by Matlab", doubleArrayOf(3.5, 4.3301), p[23].getData(), 0.0001)

        p = DistMesh2D.initEquiTriangles(1.0, arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(3.0, 4.0)))
        assertEquals("Size by Matlab", 20, p.size)
        // points
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.0, 0.0), p[0].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.5, 0.8660), p[1].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.0, 1.7321), p[2].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.5, 2.5981), p[3].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(0.0, 3.4641), p[4].getData(), 0.0001)
        assertArrayEquals("Content by Matlab", doubleArrayOf(1.0, 0.0), p[5].getData(), 0.0001)
    }

    @Test
    @Throws(Exception::class)
    fun testAnyTooClose() {
        val bars = ArrayList<DistMeshBar>()
        var bar = DistMeshBar(0, 1)
        bar.l0 = 3.0
        bar.l = 1.0
        bars.add(bar)

        assertTrue("One short bar.", DistMesh2D.anyTooClose(bars))

        bar = DistMeshBar(0, 2)
        bar.l0 = 3.0
        bar.l = 2.0
        bars.add(bar)
        bar = DistMeshBar(1, 2)
        bar.l0 = 3.0
        bar.l = 2.0
        bars.add(bar)

        assertTrue("One short bar.", DistMesh2D.anyTooClose(bars))
        bars.removeAt(0)
        assertFalse("No short bar.", DistMesh2D.anyTooClose(bars))
    }

    @Test
    @Throws(Exception::class)
    fun testRemoveTooClosePoints() {
        val bars = ArrayList<DistMeshBar>()
        val bar = DistMeshBar(0, 1)
        bar.l0 = 3.0
        bar.l = 1.0
        bars.add(bar)

        var p = anyPoints(2)
        DistMesh2D.removeTooClosePoints(p, bars, 0)
        assertTrue("Any fix point.", p.isEmpty())

        p = anyPoints(2)
        DistMesh2D.removeTooClosePoints(p, bars, 1)
        assertEquals("One fix point.", 1, p.size)

        p = anyPoints(2)
        DistMesh2D.removeTooClosePoints(p, bars, 2)
        assertEquals("All fix point.", 2, p.size)

        val bar2 = DistMeshBar(0, 2)
        bar2.l0 = 3.0
        bar2.l = 2.0
        bars.add(bar2)
        val bar3 = DistMeshBar(1, 2)
        bar3.l0 = 3.0
        bar3.l = 2.0
        bars.add(bar3)

        p = anyPoints(3)
        DistMesh2D.removeTooClosePoints(p, bars, 0)
        assertEquals("Any fix point.", 1, p.size)

        p = anyPoints(3)
        DistMesh2D.removeTooClosePoints(p, bars, 1)
        assertEquals("One fix point.", 2, p.size)

        p = anyPoints(3)
        DistMesh2D.removeTooClosePoints(p, bars, 3)
        assertEquals("All fix point.", 3, p.size)
    }

    private fun anyPoints(n: Int): MutableList<Pnt> {
        val p = ArrayList<Pnt>(n)
        for (i in 0 until n) {
            p.add(Pnt(i.toDouble(), (i % 2).toDouble()))
        }
        return p
    }
}
