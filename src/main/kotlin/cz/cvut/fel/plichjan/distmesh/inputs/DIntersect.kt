package cz.cvut.fel.plichjan.distmesh.inputs

import kotlin.math.max

/**
 * function d=dintersect(d1,d2), d=max(d1,d2);
 */
data class DIntersect(val d1: IDistanceFunction, val d2: IDistanceFunction) : IDistanceFunction {
    override fun call(x: Double, y: Double): Double {
        return max(d1.call(x, y), d2.call(x, y))
    }
}
