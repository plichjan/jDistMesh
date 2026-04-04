@file:Suppress("unused")

package cz.cvut.fel.plichjan.distmesh.script

import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.BndProj
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle0
import cz.cvut.fel.plichjan.distmesh.inputs.DUnion
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.inputs.IEdgeLengthFunction
import delaunay.Pnt
import kotlin.math.abs
import kotlin.math.min

data class Box(val width: Double, val height: Double, val dividing: Double)

fun herodes(
    file: String,
    box: Box,
    electrode1: IDistanceFunction,
    electrode2: IDistanceFunction,
    pfixInput: List<Pnt>? = null,
    dir: String = "./"
) {
    val distMesh2D = ScriptContext.distMesh2D
    val viewFrame = ScriptContext.viewFrame
    val forEvery = IDistanceFunction { _, _ -> -1.0 }

    val zoom = 5.0
    val width = box.width * zoom
    val height = box.height * zoom
    val x0 = box.width * (1.0 - zoom) / 2.0
    val y0 = box.height * (1.0 - zoom) / 2.0

    val pfix = ArrayList<Pnt>()
    pfix.add(Pnt(x0,          y0))
    pfix.add(Pnt(x0,          height + y0))
    pfix.add(Pnt(width + x0,  y0))
    pfix.add(Pnt(width + x0,  height + y0))
    pfixInput?.let { pfix.addAll(it) }

    val h0 = 10.0 / box.dividing
    val dBox = DRectangle0(x0, x0 + width, y0, y0 + height)
    val fd = DDiff(dBox, DUnion(electrode1, electrode2))

    val fh = IEdgeLengthFunction { x, y ->
        min(
            h0 + 0.3 * abs(electrode1.call(x, y)),
            h0 + 0.3 * abs(electrode2.call(x, y))
        )
    }

    var mesh2 = distMesh2D.call(
        fd,
        fh,
        h0, arrayOf(doubleArrayOf(x0, y0), doubleArrayOf(x0 + width, y0 + height)),
        pfix
    )

    mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, fd)
    mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
    viewFrame.drawMesh(mesh2)

    storeMesh(dir + file, mesh2, h0 / 1000.0, listOf(
        Constrain(df = electrode1, flags = IS_SET_POTENTIAL, potential = -1.0),
        Constrain(df = electrode2, flags = IS_SET_POTENTIAL, potential =  1.0)
    ), listOf(
        TrConstrain(df = forEvery, matId = 10)
    ))
}
