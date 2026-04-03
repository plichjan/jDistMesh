package cz.cvut.fel.plichjan

import cz.cvut.fel.plichjan.distmesh.DistMesh2D

/**
 * Static context for Kotlin scripts to provide better IDE support and type safety.
 */
object ScriptContext {
    lateinit var distMesh2D: DistMesh2D
    lateinit var viewFrame: IViewer
}
