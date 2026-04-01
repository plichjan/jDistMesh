package cz.cvut.fel.plichjan.distmesh.inputs

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

/**
 * function d=ddiff(d1,d2), d=max(d1,-d2);
 */
data class DDiff(val d1: IDistanceFunction, val d2: IDistanceFunction) : IDistanceFunction {
    override fun call(x: Double, y: Double): Double {
        val dd1 = d1.call(x, y)
        val dd2 = d2.call(x, y)
        // Original logic from Java:
        // Math.signum(1. + Math.signum(dd1) + Math.signum(-dd2)) * Math.min(Math.abs(dd1), Math.abs(dd2))
        return (1.0 + dd1.sign + (-dd2).sign).sign * min(abs(dd1), abs(dd2))
    }
}
