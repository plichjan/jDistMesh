import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.HUniform
import delaunay.Pnt
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Star shape (alternating outer/inner polygon vertices)
// Reference: test.js (commented section - "star")

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val n = 6
val r1 = 1.0
val r2 = 0.5

val pfix = arrayListOf<Pnt>()
val p1 = arrayListOf<Pnt>()

for (i in 0 until n) {
    // outer vertex
    var phi = i / 6.0 * 2 * PI
    var x = cos(phi); var y = sin(phi)
    pfix.add(Pnt(r1 * x, r1 * y))
    p1.add(Pnt(r1 * x, r1 * y))

    // inner vertex (offset by half a sector)
    phi += 1.0 / 12.0 * 2 * PI
    x = cos(phi); y = sin(phi)
    pfix.add(Pnt(r2 * x, r2 * y))
    p1.add(Pnt(r2 * x, r2 * y))
}

var mesh2 = distMesh2D.call(
    DPoly(p1),
    HUniform(),
    0.2, arrayOf(doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, 1.0)),
    pfix
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

println("Done: star")
