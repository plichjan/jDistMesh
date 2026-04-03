import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.inputs.IEdgeLengthFunction
import kotlin.math.sqrt

// Ellipse [a, b]
// Reference: test.js (commented section)

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val a = 1.0
val b = 0.25
val h0 = 0.05

val fd = IDistanceFunction { x, y -> sqrt(x * x / a / a + y * y / b / b) - 1.0 }
val fh = IEdgeLengthFunction { x, y -> h0 + 0.1 * -fd.call(x, y) }

var mesh2 = distMesh2D.call(
    fd,
    fh,
    h0, arrayOf(doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, 1.0)),
    emptyList()
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

println("Done: ellipse")
