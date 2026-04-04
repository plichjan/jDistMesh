import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.script.Box
import cz.cvut.fel.plichjan.distmesh.script.herodes
import delaunay.Pnt

val distMesh2D = ScriptContext.distMesh2D

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes06...")
herodes("herodes06", box,
    DPoly(listOf(Pnt(30.0, 30.0), Pnt(130.0, 30.0), Pnt(130.0, 50.0), Pnt(30.0, 50.0))),
    DCircle(200.0, 120.0, 50.0),
    listOf(Pnt(30.0, 30.0), Pnt(130.0, 30.0), Pnt(130.0, 50.0), Pnt(30.0, 50.0))
)
println("Done.")
