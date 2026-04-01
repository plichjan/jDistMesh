package cz.cvut.fel.plichjan.distmesh.inputs

import kotlin.math.min
import kotlin.math.sqrt

/**
 * function d=drectangle0(p,x1,x2,y1,y2)
 */
data class DRectangle0(val x1: Double, val x2: Double, val y1: Double, val y2: Double) : IDistanceFunction {
    override fun call(x: Double, y: Double): Double {
        val d1 = y1 - y
        val d2 = -y2 + y
        val d3 = x1 - x
        val d4 = -x2 + x

        val d5 = sqrt(d1 * d1 + d3 * d3)
        val d6 = sqrt(d1 * d1 + d4 * d4)
        val d7 = sqrt(d2 * d2 + d3 * d3)
        val d8 = sqrt(d2 * d2 + d4 * d4)

        var d = -min(min(min(-d1, -d2), -d3), -d4)

        d = if (d1 > 0 && d3 > 0) d5
        else if (d1 > 0 && d4 > 0) d6
        else if (d2 > 0 && d3 > 0) d7
        else if (d2 > 0 && d4 > 0) d8
        else d

        return d
    }
}
