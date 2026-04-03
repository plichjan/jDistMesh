import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.BndProj
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import cz.cvut.fel.plichjan.distmesh.script.Constrain
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_POTENTIAL
import cz.cvut.fel.plichjan.distmesh.script.TrConstrain
import cz.cvut.fel.plichjan.distmesh.script.storeMesh

// Variables for script
val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"

val forEvery = IDistanceFunction { _, _ -> -1.0 }

// Circle with Hole
val r0 = 1.0
val r1 = 5.0
val v0 = 1.0
val v1 = 0.0
val c0 = DCircle(0.0, 0.0, r0)
val c1 = DCircle(0.0, 0.0, r1)

fun myCircle(i: Int, h0: Double): Mesh {
    distMesh2D.dptol = 0.0001
    val df = DDiff(c1, c0)

    var mesh2 = distMesh2D.call(
        df,
        { x, y -> h0 + 0.3 * c0.call(x, y) },
        h0, arrayOf(doubleArrayOf(-r1, -r1), doubleArrayOf(r1, r1)),
        emptyList()
    )

    mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
    mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, df)
    viewFrame.drawMesh(mesh2)

    val file = "${dir}circleNu0$i"
    storeMesh(file, mesh2, h0 / 4, listOf(
        Constrain(df = c0, flags = IS_SET_POTENTIAL, potential = v0),
        Constrain(df = c1, flags = IS_SET_POTENTIAL, potential = v1)
    ), listOf(
        TrConstrain(df = forEvery, matId = 10)
    ))
    return mesh2
}

println("Starting circle generation...")
myCircle(1, 1.0)
println("Done.")
