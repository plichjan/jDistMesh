import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle0
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.inputs.IEdgeLengthFunction
import delaunay.Pnt
import kotlin.math.abs
import kotlin.math.min

// Square with size function driven by point and line sources
// Reference: test.js (commented section - "Square, with size function point and line sources")
// MATLAB ref: fd=DRectangle0(p,0,1,0,1);
//             fh=min(0.01+0.3*abs(dcircle(p,0,0,0)), 0.025+0.3*abs(dpoly(p,[0.3,0.7;0.7,0.5])), 0.15)

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val d1 = DCircle(0.0, 0.0, 0.0)
val d2 = DPoly(listOf(Pnt(0.3, 0.7), Pnt(0.7, 0.5)))

val fh = IEdgeLengthFunction { x, y ->
    min(
        min(0.01 + 0.3 * abs(d1.call(x, y)),
            0.025 + 0.3 * abs(d2.call(x, y))),
        0.15
    )
}

val fix = arrayListOf(
    Pnt(0.0, 0.0),
    Pnt(1.0, 0.0),
    Pnt(0.0, 1.0),
    Pnt(1.0, 1.0)
)

var mesh2 = distMesh2D.call(
    DRectangle0(0.0, 1.0, 0.0, 1.0),
    fh,
    0.01, arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(1.0, 1.0)),
    fix
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

println("Done: squareSources")
