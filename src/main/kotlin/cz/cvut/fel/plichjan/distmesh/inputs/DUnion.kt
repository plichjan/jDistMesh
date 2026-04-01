package cz.cvut.fel.plichjan.distmesh.inputs

import kotlin.math.min

/**
 * function d=dunion(d1,d2), d=min(d1,d2);
 */
data class DUnion(val d1: IDistanceFunction, val d2: IDistanceFunction) : IDistanceFunction {
    override fun call(x: Double, y: Double): Double {
        return min(d1.call(x, y), d2.call(x, y))
    }
}
