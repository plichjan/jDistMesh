import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.BndProj
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle0
import cz.cvut.fel.plichjan.distmesh.inputs.HUniform
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.script.Constrain
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_BOUNDARY_FORCE
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_DISPLACEMENT
import cz.cvut.fel.plichjan.distmesh.script.TrConstrain
import cz.cvut.fel.plichjan.distmesh.script.storeMesh
import delaunay.Pnt

// Beam plane stress
// Reference: test.js (commented section)

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"
val forEvery = IDistanceFunction { _, _ -> -1.0 }

val w = 10.0
val h = 0.1
var h0 = h / 2.0

val pfix = arrayListOf(
    Pnt(0.0, 0.0),
    Pnt(w,   0.0),
    Pnt(w,   h),
    Pnt(0.0, h)
)

val rc = DRectangle0(0.0, w, 0.0, h)

var mesh2 = distMesh2D.call(
    rc,
    HUniform(),
    h0, arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(w + h0, h + h0)),
    pfix
)

val v0Poly = DPoly(listOf(Pnt(0.0, h), Pnt(w, h)))
val c0 = DCircle(0.0, 0.0, 0.0)

mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, rc)
mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

h0 /= 10.0

storeMesh("${dir}beam", mesh2, h0, listOf(
    Constrain(df = DPoly(listOf(Pnt(0.0, 0.0), Pnt(0.0, h))), flags = IS_SET_DISPLACEMENT, displacement = doubleArrayOf(0.0, Double.NaN)),
    Constrain(df = c0,    flags = IS_SET_DISPLACEMENT,    displacement = doubleArrayOf(0.0, 0.0)),
    Constrain(df = v0Poly, flags = IS_SET_BOUNDARY_FORCE, bForce = doubleArrayOf(0.0, 16.0 / 12.0 * 1e-3 / 1e4))
), listOf(
    TrConstrain(df = forEvery, matId = 100)
))

println("Done: beam")
