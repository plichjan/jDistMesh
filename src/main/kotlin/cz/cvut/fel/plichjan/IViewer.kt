package cz.cvut.fel.plichjan

import cz.cvut.fel.plichjan.distmesh.result.Mesh
import java.awt.geom.Point2D

interface IViewer {
    fun setNewPoints(points: List<Point2D>, t: List<IntArray>, bbox: Array<DoubleArray>)

    fun drawMesh(mesh: Mesh)
}
