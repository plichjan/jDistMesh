import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.HUniform
import delaunay.Pnt
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Hexagon shapes
// Reference: test.js (commented sections - "pdf: Fig. 4 simple hexagon" and "pdf: Fig. 4" donut hexagon)

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val n = 6
val r1 = 1.0
val r2 = 0.5 // for donut variant

// --- Simple hexagon ---
val pfixHex = arrayListOf<Pnt>()
val p1 = arrayListOf<Pnt>()
for (i in 0 until n) {
    val phi = (i / 6.0 + 1.0 / 24.0) * 2 * PI
    val x = cos(phi)
    val y = sin(phi)
    pfixHex.add(Pnt(r1 * x, r1 * y))
    p1.add(Pnt(r1 * x, r1 * y))
}

var mesh2 = distMesh2D.call(
    DPoly(p1),
    HUniform(),
    0.1, arrayOf(doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, 1.0)),
    pfixHex
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

println("Done: hexagon")
