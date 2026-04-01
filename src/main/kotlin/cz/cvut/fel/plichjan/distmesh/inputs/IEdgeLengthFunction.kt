package cz.cvut.fel.plichjan.distmesh.inputs

/**
 * Scaled edge length function h(x,y)
 */
fun interface IEdgeLengthFunction {
    fun call(x: Double, y: Double): Double
}
