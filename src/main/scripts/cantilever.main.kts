import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.AddMidpoints
import cz.cvut.fel.plichjan.distmesh.BndProj
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff
import cz.cvut.fel.plichjan.distmesh.inputs.DCircle
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle0
import cz.cvut.fel.plichjan.distmesh.inputs.DUnion
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.script.Constrain
import cz.cvut.fel.plichjan.distmesh.script.IS_PRINTABLE
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_DISPLACEMENT
import cz.cvut.fel.plichjan.distmesh.script.IS_SET_POTENTIAL
import cz.cvut.fel.plichjan.distmesh.script.TrConstrain
import cz.cvut.fel.plichjan.distmesh.script.WALL_NODE
import cz.cvut.fel.plichjan.distmesh.script.storeMesh
import delaunay.Pnt
import kotlin.math.abs
import kotlin.math.min

// Cantilever beam with electrostatic electrode
// Reference: test.js (01_temaDIP_cantilever.pdf Fig. 3 (a)) — active section
// Uses distMesh2D instead of FreeFemMeshReader

val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"
val forEvery = IDistanceFunction { _, _ -> -1.0 }

val L     = 200e-6
val t     = 3e-6
val g     = 2e-6
val w     = 50e-6
val alpha = 1.0
val V     = 16.0 - 0.86

val l_e    = alpha * L
val width  = 2 * L
val height = 2 * g + t

var h0 = min(t, g) / 4.0

val beam     = DRectangle0(0.0,     L,     g,       g + t)
val electrode = DRectangle0(L - l_e, L,    -t,       0.0)
val pLeft    = DPoly(listOf(Pnt(0.0, g), Pnt(0.0, g + t)))
val pRight   = DPoly(listOf(Pnt(L,  g), Pnt(L,  g + t)))
val pBottom  = DPoly(listOf(Pnt(0.0, g), Pnt(L,  g)))
val pTop     = DPoly(listOf(Pnt(0.0, g + t), Pnt(L, g + t)))

fun fh(x: Double, y: Double): Double = min(
    min(
        h0 + 0.05 * abs(pLeft.call(x, y)),
        2 * h0 + 0.05 * abs(pRight.call(x, y))
    ),
    min(
        min(
            L / 100 + 0.05 * abs(pBottom.call(x, y)),
            L / 100 + 0.05 * abs(pTop.call(x, y))
        ),
        4 * h0 + 0.05 * abs(electrode.call(x, y))
    )
)

val area1 = DUnion(electrode, beam)

// Generate beam mesh
distMesh2D.dptol = 0.01
var mesh2 = distMesh2D.call(
    beam,
    ::fh,
    h0, arrayOf(doubleArrayOf(0.0, 0.0), doubleArrayOf(width, height)),
    listOf(Pnt(0.0, g), Pnt(0.0, g + t), Pnt(L, g), Pnt(L, g + t))
)

mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
viewFrame.drawMesh(mesh2)

h0 /= 1000.0
val lbCorner = DCircle(0.0, g, 0.0)

storeMesh("${dir}cantilever", mesh2, h0, listOf(
    Constrain(df = beam,      flags = IS_SET_POTENTIAL or WALL_NODE,   potential = 0.0),
    Constrain(df = electrode, flags = IS_SET_POTENTIAL or WALL_NODE,   potential = V),
    Constrain(df = DPoly(listOf(Pnt(0.0, 0.0), Pnt(width, 0.0))),
              flags = IS_SET_DISPLACEMENT or WALL_NODE, displacement = doubleArrayOf(0.0, 0.0)),
    Constrain(df = DPoly(listOf(Pnt(0.0, g), Pnt(0.0, g + t))),
              flags = IS_SET_DISPLACEMENT or WALL_NODE, displacement = doubleArrayOf(0.0, Double.NaN)),
    Constrain(df = DCircle(L, g, 0.0), flags = IS_PRINTABLE),
    Constrain(df = lbCorner, flags = IS_SET_DISPLACEMENT, displacement = doubleArrayOf(0.0, 0.0))
), listOf(
    TrConstrain(df = forEvery, matId = 10),  // air
    TrConstrain(df = area1,    matId = 19)   // E = 57e9
))

println("Done: cantilever")
