package cz.cvut.fel.plichjan.distmesh.inputs

import org.junit.Assert.assertEquals
import org.junit.Test

class DCircleTest {
    @Test
    @Throws(Exception::class)
    fun testCall() {
        val circle = DCircle(10.0, 20.0, 5.0)

        assertEquals(-5.0, circle.call(10.0, 20.0), 1e-9)
        assertEquals(0.0, circle.call(10.0, 25.0), 1e-9)
        assertEquals(0.0, circle.call(15.0, 20.0), 1e-9)
        assertEquals(5.0, circle.call(20.0, 20.0), 1e-9)
    }
}
