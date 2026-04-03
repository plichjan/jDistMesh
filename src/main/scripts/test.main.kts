import cz.cvut.fel.plichjan.ScriptContext
import cz.cvut.fel.plichjan.distmesh.inputs.DPoly
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle0
import cz.cvut.fel.plichjan.distmesh.inputs.DUnion
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import delaunay.Pnt
import kotlin.math.abs
import kotlin.math.min

// Variables for script
val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"

val forEvery = IDistanceFunction { _, _ -> -1.0 }

// 01_temaDIP_cantilever.pdf Fig. 3 (a)
val L = 200e-6
val t = 3e-6
val g = 2e-6
val w = 50e-6
val alpha = 1.0
val V = 16.0 - 0.86

// w, t are width and thickness of the beam, alpha is ratio of electrode length (l_e) over beam length (L)
val l_e_val = alpha * L
val width_val = 2 * L
val height_val = 2 * g + t

//two electrodes and space between
var h0_val = min(t, g) / 4.0
val beam = DRectangle0(0.0, L, g, g + t)
val electrode = DRectangle0(L - l_e_val, L, 0.0 - t, 0.0)
val pLeft = DPoly(listOf(Pnt(0.0, g), Pnt(0.0, g + t)))
val pRight = DPoly(listOf(Pnt(L, g), Pnt(L, g + t)))
val pBottom = DPoly(listOf(Pnt(0.0, g), Pnt(L, g)))
val pTop = DPoly(listOf(Pnt(0.0, g + t), Pnt(L, g + t)))

fun fh(x: Double, y: Double): Double {
    return min(
        min(
            h0_val + 0.05 * abs(pLeft.call(x, y)),
            2 * h0_val + 0.05 * abs(pRight.call(x, y))
        ),
        min(
            min(
                L / 100 + 0.05 * abs(pBottom.call(x, y)),
                L / 100 + 0.05 * abs(pTop.call(x, y))
            ),
            4 * h0_val + 0.05 * abs(electrode.call(x, y))
        )
    )
}

val area1 = DUnion(electrode, beam)

println("Test script loaded.")
