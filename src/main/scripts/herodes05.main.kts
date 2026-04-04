import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.script.Box
import cz.cvut.fel.plichjan.distmesh.script.herodes
import delaunay.Pnt

val distMesh2D = ScriptContext.distMesh2D

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes05...")
herodes("herodes05", box,
    DPoly(listOf(Pnt(45.0, 175.0), Pnt(30.0, 160.0), Pnt(95.0, 85.0), Pnt(110.0, 95.0))),
    DPoly(listOf(Pnt(197.0, 25.0), Pnt(211.0, 104.0), Pnt(134.0, 119.0), Pnt(138.0, 138.0), Pnt(234.0, 121.0), Pnt(216.0, 22.0))),
    listOf(Pnt(45.0, 175.0), Pnt(30.0, 160.0), Pnt(95.0, 85.0), Pnt(110.0, 95.0),
           Pnt(197.0, 25.0), Pnt(211.0, 104.0), Pnt(134.0, 119.0), Pnt(138.0, 138.0), Pnt(234.0, 121.0), Pnt(216.0, 22.0))
)
println("Done.")
