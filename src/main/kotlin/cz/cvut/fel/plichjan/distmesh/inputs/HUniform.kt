package cz.cvut.fel.plichjan.distmesh.inputs

/**
 */
class HUniform : IEdgeLengthFunction {
    override fun call(x: Double, y: Double): Double {
        return 1.0
    }
}
