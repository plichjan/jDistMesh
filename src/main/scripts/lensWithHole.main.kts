import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DIntersect
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.inputs.IEdgeLengthFunction
import delaunay.Pnt
import kotlin.math.sqrt

// Lens shape with a small circular hole ("čočka s dírou")
// Reference: test.js (commented section - "cocka s dirou")

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

// Lens = intersection of two circles: DCircle(-1,0,1) and DCircle(0,0,0.5)
val lensOuter = DCircle(-1.0, 0.0, 1.0)
val lensBound = DCircle(0.0,  0.0, 0.5)
val hole      = DCircle(-0.1, 0.15, 0.05)

// The two intersection points of the two circles (used as size refinement targets)
val dh1 = DCircle(-0.125,  sqrt(15.0) / 8.0, 0.0)
val dh2 = DCircle(-0.125, -sqrt(15.0) / 8.0, 0.0)

val pfix = arrayListOf(
    Pnt(-0.125,  sqrt(15.0) / 8.0),
    Pnt(-0.125, -sqrt(15.0) / 8.0)
)

val fd = DDiff(DIntersect(lensOuter, lensBound), hole)

val fh = IEdgeLengthFunction { x, y ->
    minOf(
        0.01 + 0.3 * dh1.call(x, y),
        0.01 + 0.3 * dh2.call(x, y),
        0.01 + 0.3 * hole.call(x, y)
    )
}

var mesh2 = distMesh2D.call(
    fd,
    fh,
    0.01, arrayOf(doubleArrayOf(-0.5, -0.5), doubleArrayOf(0.0, 0.5)),
    pfix
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

println("Done: lensWithHole")
