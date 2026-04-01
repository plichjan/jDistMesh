package cz.cvut.fel.plichjan.distmesh.inputs

import kotlin.math.min

/**
 * function d=drectangle(p,x1,x2,y1,y2)
 * d=-min(min(min(-y1+p(:,2),y2-p(:,2)),-x1+p(:,1)),x2-p(:,1));
 */
data class DRectangle(val x1: Double, val x2: Double, val y1: Double, val y2: Double) : IDistanceFunction {
    override fun call(x: Double, y: Double): Double {
        return -min(min(min(-y1 + y, y2 - y), -x1 + x), x2 - x)
    }
}
