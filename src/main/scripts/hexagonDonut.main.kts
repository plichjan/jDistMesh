import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.HUniform
import delaunay.Pnt
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Donut hexagon (outer hexagon minus inner rotated hexagon)
// Reference: test.js (commented section - "pdf: Fig. 4")

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val n = 6
val r1 = 1.0  // outer radius
val r2 = 0.5  // inner radius

val pfix = arrayListOf<Pnt>()
val outer = arrayListOf<Pnt>()
val inner = arrayListOf<Pnt>()

for (i in 0 until n) {
    // outer vertex
    var phi = i / 6.0 * 2 * PI
    pfix.add(Pnt(r1 * cos(phi), r1 * sin(phi)))
    outer.add(Pnt(r1 * cos(phi), r1 * sin(phi)))

    // inner vertex (rotated half a sector)
    phi += 1.0 / 12.0 * 2 * PI
    pfix.add(Pnt(r2 * cos(phi), r2 * sin(phi)))
    inner.add(Pnt(r2 * cos(phi), r2 * sin(phi)))
}

var mesh2 = distMesh2D.call(
    DDiff(DPoly(outer), DPoly(inner)),
    HUniform(),
    0.1, arrayOf(doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, 1.0)),
    pfix
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

println("Done: hexagonDonut")
