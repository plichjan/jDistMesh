import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.BndProj
import cz.cvut.fel.plichjan.distmesh.Split
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import cz.cvut.fel.plichjan.distmesh.script.Constrain
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_POTENTIAL
import cz.cvut.fel.plichjan.distmesh.script.TrConstrain
import cz.cvut.fel.plichjan.distmesh.script.storeMesh

// Circle with Hole — convergence study (h-refinement via Split subdivision)
// Reference: circle.js (commented-out section)
//
// Iterations 1–3: generates a new mesh using distMesh2D with the given h0
// Iterations 4–8: bisects edges of the previous mesh (Split), projects nodes onto the boundary (BndProj)
//                 and inserts midpoints (AddMidpoints) — pure h-refinement without retriangulation

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"
val forEvery = IDistanceFunction { _, _ -> -1.0 }

val r0 = 1.0
val r1 = 5.0
val v0 = 1.0
val v1 = 0.0
val c0 = DCircle(0.0, 0.0, r0)
val c1 = DCircle(0.0, 0.0, r1)
val df = DDiff(c1, c0)

val constrains = listOf(
    Constrain(df = c0, flags = IS_SET_POTENTIAL, potential = v0),
    Constrain(df = c1, flags = IS_SET_POTENTIAL, potential = v1)
)
val trConstrains = listOf(
    TrConstrain(df = forEvery, matId = 10)
)

fun generateCircle(h0: Double): Mesh {
    distMesh2D.dptol = 0.0001
    var mesh2 = distMesh2D.call(
        df,
        { x, y -> h0 + 0.3 * c0.call(x, y) },
        h0, arrayOf(doubleArrayOf(-r1, -r1), doubleArrayOf(r1, r1)),
        emptyList()
    )
    mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
    mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, df)
    return mesh2
}

fun splitCircle(prev: Mesh): Mesh {
    var mesh2 = Split().call(prev.p!!, prev.t!!)
    mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, df)
    mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
    return mesh2
}

var oldMesh: Mesh? = null
var h0 = 1.0

for (i in 1..8) {
    var mesh = if (i <= 3) {
        generateCircle(h0)
    } else {
        splitCircle(oldMesh!!)
    }

    viewFrame.drawMesh(mesh)
    storeMesh("${dir}circleNu0$i", mesh, h0 / 4, constrains, trConstrains)
    println("Step $i done (h0=$h0)")

    h0 /= 2.0
    oldMesh = mesh
}

println("Convergence study complete.")
