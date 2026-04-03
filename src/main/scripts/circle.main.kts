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

val IS_SET_POTENTIAL = 4

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

// Circle with Hole
val r0 = 1.0
val r1 = 5.0
val v0 = 1.0
val v1 = 0.0
val c0 = DCircle(0.0, 0.0, r0)
val c1 = DCircle(0.0, 0.0, r1)

val forEvery = IDistanceFunction { _, _ -> -1.0 }

fun myCircle(i: Int, h0: Double): Mesh {
    distMesh2D.dptol = 0.0001
    val df = DDiff(c1, c0)

    var mesh2 = distMesh2D.call(
        df,
        { x, y -> h0 + 0.3 * c0.call(x, y) },
        h0, arrayOf(doubleArrayOf(-r1, -r1), doubleArrayOf(r1, r1)),
        emptyList()
    )

    mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
    mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, df)
    viewFrame?.drawMesh(mesh2)

    val file = "${dir}circleNu0$i"
    storeMesh(file, mesh2, h0 / 4, listOf(
        Constrain(df = c0, flags = IS_SET_POTENTIAL, potential = v0),
        Constrain(df = c1, flags = IS_SET_POTENTIAL, potential = v1)
    ), listOf(
        TrConstrain(df = forEvery, matId = 10)
    ))
    return mesh2
}

println("Starting circle generation...")
myCircle(1, 1.0)
println("Done.")
