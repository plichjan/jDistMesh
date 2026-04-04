import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.script.Box
import cz.cvut.fel.plichjan.distmesh.script.herodes

val distMesh2D = ScriptContext.distMesh2D

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes04...")
herodes("herodes04", box,
    DCircle(205.0, 125.0, 50.0),
    DCircle(75.0,  75.0,  50.0)
)
println("Done.")
