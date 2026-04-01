package cz.cvut.fel.plichjan.distmesh.inputs

/**
 * Distance function d(p), p is point coordinates
 */
fun interface IDistanceFunction {
    fun call(x: Double, y: Double): Double
}
