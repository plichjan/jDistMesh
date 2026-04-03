import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle0
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.script.Constrain
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_BOUNDARY_FORCE
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_DISPLACEMENT
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_POTENTIAL
import cz.cvut.fel.plichjan.distmesh.script.TrConstrain
import cz.cvut.fel.plichjan.distmesh.script.storeMesh
import delaunay.Pnt

// Square with circular Hole
// Reference: test.js (commented section - Square with Hole, with storeMesh)

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"
val forEvery = IDistanceFunction { _, _ -> -1.0 }

val pfix = arrayListOf(
    Pnt(-1.0, -1.0),
    Pnt(-1.0,  1.0),
    Pnt( 1.0, -1.0),
    Pnt( 1.0,  1.0)
)

val dc = DCircle(0.0, 0.0, 0.5)
val rc = DRectangle0(-1.0, 1.0, -1.0, 1.0)
val h0 = 0.05

var mesh2 = distMesh2D.call(
    DDiff(rc, dc),
    { x, y -> 0.05 + 0.3 * dc.call(x, y) },
    h0, arrayOf(doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, 1.0)),
    pfix
)

val lbCorner = DCircle(-1.0, -1.0, 0.0)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

storeMesh("${dir}squareWithHole", mesh2, h0, listOf(
    Constrain(df = dc,       flags = IS_SET_POTENTIAL,      potential = 10.0),
    Constrain(df = rc,       flags = IS_SET_POTENTIAL,      potential = -10.0),
    Constrain(df = DPoly(listOf(Pnt(-1.0, -1.0), Pnt(-1.0, 1.0))),
              flags = IS_SET_DISPLACEMENT, displacement = doubleArrayOf(0.0, Double.NaN)),
    Constrain(df = lbCorner, flags = IS_SET_DISPLACEMENT,   displacement = doubleArrayOf(0.0, 0.0)),
    Constrain(df = DPoly(listOf(Pnt(-1.0, 1.0), Pnt(1.0, 1.0))),
              flags = IS_SET_BOUNDARY_FORCE, bForce = doubleArrayOf(0.0, 200e7))
), listOf(
    TrConstrain(df = forEvery, matId = 20)
))

println("Done: squareWithHole")
