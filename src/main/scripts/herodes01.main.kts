import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.script.Box
import cz.cvut.fel.plichjan.distmesh.script.herodes
import delaunay.Pnt

val distMesh2D = ScriptContext.distMesh2D

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes01...")
herodes("herodes01", box,
    DPoly(listOf(Pnt(150.0, 90.0), Pnt(250.0, 90.0), Pnt(250.0, 110.0), Pnt(150.0, 110.0))),
    DPoly(listOf(Pnt(78.0, 100.0), Pnt(138.0, 160.0), Pnt(124.0, 174.0), Pnt(50.0, 100.0), Pnt(124.0, 26.0), Pnt(138.0, 40.0))),
    listOf(Pnt(150.0, 90.0), Pnt(250.0, 90.0), Pnt(250.0, 110.0), Pnt(150.0, 110.0),
           Pnt(78.0, 100.0), Pnt(138.0, 160.0), Pnt(124.0, 174.0), Pnt(50.0, 100.0), Pnt(124.0, 26.0), Pnt(138.0, 40.0))
)
println("Done.")
