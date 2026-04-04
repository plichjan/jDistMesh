import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.script.Box
import cz.cvut.fel.plichjan.distmesh.script.herodes
import delaunay.Pnt

val distMesh2D = ScriptContext.distMesh2D

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes02...")
herodes("herodes02", box,
    DPoly(listOf(Pnt(39.0, 91.0), Pnt(87.0, 178.0), Pnt(105.0, 167.0), Pnt(66.0, 99.0), Pnt(134.0, 58.0), Pnt(123.0, 40.0))),
    DPoly(listOf(Pnt(197.0, 25.0), Pnt(211.0, 104.0), Pnt(134.0, 119.0), Pnt(138.0, 138.0), Pnt(234.0, 121.0), Pnt(216.0, 22.0))),
    listOf(Pnt(39.0, 91.0), Pnt(87.0, 178.0), Pnt(105.0, 167.0), Pnt(66.0, 99.0), Pnt(134.0, 58.0), Pnt(123.0, 40.0),
           Pnt(197.0, 25.0), Pnt(211.0, 104.0), Pnt(134.0, 119.0), Pnt(138.0, 138.0), Pnt(234.0, 121.0), Pnt(216.0, 22.0))
)
println("Done.")
