package cz.cvut.fel.plichjan.distmesh.result

/**
 * Oriented edge
 */
data class Edge(
    var a: Int,
    var b: Int,
    var c: Int = 0,
    var tr: Int = 0
)
