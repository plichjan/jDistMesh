package cz.cvut.fel.plichjan.distmesh.matlab

import delaunay.Pnt

/**
 * Edge
 */
class DistMeshBar(a: Int, b: Int) : Bar(a, b) {

    var barvec: Pnt? = null
    var l: Double = 0.0
    var hbar: Double = 0.0
    var l0: Double = 0.0
    var f: Double = 0.0
    var fvec: Pnt? = null
}
