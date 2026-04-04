import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.script.Box
import cz.cvut.fel.plichjan.distmesh.script.herodes
import delaunay.Pnt

val distMesh2D = ScriptContext.distMesh2D

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes07...")
herodes("herodes07", box,
    DPoly(listOf(Pnt(50.0, 50.0), Pnt(150.0, 50.0), Pnt(150.0, 70.0), Pnt(70.0, 70.0), Pnt(70.0, 150.0), Pnt(50.0, 150.0))),
    DPoly(listOf(Pnt(130.0, 130.0), Pnt(230.0, 130.0), Pnt(230.0, 150.0), Pnt(130.0, 150.0))),
    listOf(Pnt(50.0, 50.0), Pnt(150.0, 50.0), Pnt(150.0, 70.0), Pnt(70.0, 70.0), Pnt(70.0, 150.0), Pnt(50.0, 150.0),
           Pnt(130.0, 130.0), Pnt(230.0, 130.0), Pnt(230.0, 150.0), Pnt(130.0, 150.0))
)
println("Done.")
