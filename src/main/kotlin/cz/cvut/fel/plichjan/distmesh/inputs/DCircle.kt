package cz.cvut.fel.plichjan.distmesh.inputs

import kotlin.math.sqrt

/**
 * d=sqrt((p(:,1)-xc).^2+(p(:,2)-yc).^2)-r;
 */
data class DCircle(val xc: Double, val yc: Double, val r: Double) : IDistanceFunction {
    override fun call(x: Double, y: Double): Double {
        return sqrt((x - xc) * (x - xc) + (y - yc) * (y - yc)) - r
    }
}
