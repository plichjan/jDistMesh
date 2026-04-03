import cz.cvut.fel.plichjan.*
import cz.cvut.fel.plichjan.distmesh.*
import cz.cvut.fel.plichjan.distmesh.inputs.*
import cz.cvut.fel.plichjan.distmesh.matlab.*
import cz.cvut.fel.plichjan.distmesh.result.*
import delaunay.Pnt
import java.util.*
import kotlin.math.abs

// Variables for script
val distMesh2D = ScriptContext.distMesh2D
val viewFrame = ScriptContext.viewFrame

val dir = "./"
val zero = doubleArrayOf(0.0, 0.0)

val IS_SET_DISPLACEMENT = 2
val IS_SET_POTENTIAL = 4
val IS_SET_FORCE = 8
val IS_PRINTABLE = 16
val IS_SET_BOUNDARY_FORCE = 32
val WALL_NODE = 64

data class Constrain(
    val df: IDistanceFunction,
    val flags: Int = 0,
    val potential: Double = 0.0,
    val displacement: DoubleArray = doubleArrayOf(0.0, 0.0),
    val bForce: DoubleArray = doubleArrayOf(0.0, 0.0),
    val vForce: DoubleArray = doubleArrayOf(0.0, 0.0)
)

data class TrConstrain(
    val df: IDistanceFunction,
    val matId: Int
)

fun callDf(df: IDistanceFunction, node: DoubleArray): Double = df.call(node[0], node[1])

fun storeMesh(file: String, mesh2: Mesh, h0: Double, list: List<Constrain>, trList: List<TrConstrain>) {
    val nodePrinter = NodePrinter("$file.node")
    nodePrinter.printHeader(mesh2.p!!.size, 2, 7)
    for (i in mesh2.p!!.indices) {
        val node = mesh2.p!![i]
        var flags = 0
        var potential = 0.0
        var displacement = zero
        var bForce = zero
        var vForce = zero

        for (item in list) {
            if (abs(callDf(item.df, node)) < h0) {
                flags = flags or item.flags
                potential = item.potential
                displacement = item.displacement
                bForce = item.bForce
                vForce = item.vForce
            }
        }

        nodePrinter.printNode(i + 1, node, doubleArrayOf(
            potential,
            displacement[0], displacement[1],
            bForce[0], bForce[1],
            vForce[0], vForce[1]
        ), flags)
    }
    nodePrinter.close()

    val pList = mesh2.p!!.map { Pnt(*it) }
    val centroid = Matlab.computeCentroids(pList, mesh2.t!!.toList())
    val elePrinter = ElePrinter("$file.ele")
    elePrinter.printHeader(mesh2.t!!.size, 6)
    for (i in mesh2.t!!.indices) {
        val tr = mesh2.t!![i]
        var matId = 0

        for (item in trList) {
            val cent = centroid[i]
            if (item.df.call(cent.getData()[0], cent.getData()[1]) < 0) {
                matId = item.matId
            }
        }
        elePrinter.printEle(i + 1, intArrayOf(tr[0] + 1, tr[1] + 1, tr[2] + 1, tr[4] + 1, tr[5] + 1, tr[3] + 1), matId)
    }
    elePrinter.close()
}

val forEvery = object : IDistanceFunction {
    override fun call(x: Double, y: Double): Double = -1.0
}

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
var h0_val = Math.min(t, g) / 4.0
val beam = DRectangle0(0.0, L, g, g + t)
val electrode = DRectangle0(L - l_e_val, L, 0.0 - t, 0.0)
val pLeft = DPoly(listOf(Pnt(0.0, g), Pnt(0.0, g + t)))
val pRight = DPoly(listOf(Pnt(L, g), Pnt(L, g + t)))
val pBottom = DPoly(listOf(Pnt(0.0, g), Pnt(L, g)))
val pTop = DPoly(listOf(Pnt(0.0, g + t), Pnt(L, g + t)))

fun fh(x: Double, y: Double): Double {
    return Math.min(
        Math.min(
            h0_val + 0.05 * Math.abs(pLeft.call(x, y)),
            2 * h0_val + 0.05 * Math.abs(pRight.call(x, y))
        ),
        Math.min(
            Math.min(
                L / 100 + 0.05 * Math.abs(pBottom.call(x, y)),
                L / 100 + 0.05 * Math.abs(pTop.call(x, y))
            ),
            4 * h0_val + 0.05 * Math.abs(electrode.call(x, y))
        )
    )
}

val area1 = DUnion(electrode, beam)

println("Test script loaded.")
