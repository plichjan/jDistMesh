import cz.cvut.fel.plichjan.*
import cz.cvut.fel.plichjan.distmesh.*
import cz.cvut.fel.plichjan.distmesh.inputs.*
import cz.cvut.fel.plichjan.distmesh.matlab.*
import cz.cvut.fel.plichjan.distmesh.result.*
import delaunay.Pnt
import java.util.*
import kotlin.math.abs

// Variables for script
// val distMesh2D = DistMesh2D() // Removed: provided by ViewerFrame
val distMesh2D: DistMesh2D = distMesh2D_from_viewer

val viewFrame: IViewer? = null 

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

val forEvery = object : IDistanceFunction {
    override fun call(x: Double, y: Double): Double = -1.0
}

data class Box(val width: Double, val height: Double, val dividing: Double)

fun herodes(file: String, box: Box, electrode1: IDistanceFunction, electrode2: IDistanceFunction, pfixInput: List<Pnt>? = null) {
    val zoom = 5.0
    val width = box.width * zoom
    val height = box.height * zoom
    val x0 = box.width * (1.0 - zoom) / 2.0
    val y0 = box.height * (1.0 - zoom) / 2.0
    
    val pfix = ArrayList<Pnt>()
    pfix.add(Pnt(x0, y0))
    pfix.add(Pnt(x0, height + y0))
    pfix.add(Pnt(width + x0, y0))
    pfix.add(Pnt(width + x0, height + y0))
    pfixInput?.let { pfix.addAll(it) }
    
    val h0 = 10.0 / box.dividing
    val dBox = DRectangle0(x0, x0 + width, y0, y0 + height)
    val fd = DDiff(dBox, DUnion(electrode1, electrode2))

    val fh = object : IEdgeLengthFunction {
        override fun call(x: Double, y: Double): Double {
            return Math.min(
                h0 + 0.3 * Math.abs(electrode1.call(x, y)),
                h0 + 0.3 * Math.abs(electrode2.call(x, y))
            )
        }
    }
    
    var mesh2 = distMesh2D.call(
        fd,
        fh,
        h0, arrayOf(doubleArrayOf(x0, y0), doubleArrayOf(x0 + width, y0 + height)),
        pfix
    )
    
    mesh2.p = BndProj().call(mesh2.p!!, mesh2.t!!, fd)
    mesh2 = AddMidpoints().call(mesh2.p!!, mesh2.t!!)
    viewFrame?.drawMesh(mesh2)

    storeMesh(dir + file, mesh2, h0 / 1000.0, listOf(
        Constrain(df = electrode1, flags = IS_SET_POTENTIAL, potential = -1.0),
        Constrain(df = electrode2, flags = IS_SET_POTENTIAL, potential = 1.0)
    ), listOf(
        TrConstrain(df = forEvery, matId = 10)
    ))
}

val box = Box(width = 280.0, height = 200.0, dividing = 2.0)
distMesh2D.dptol = 0.01

println("Running herodes configuration...")
herodes("herodes02", box,
    DPoly(listOf(Pnt(39.0, 91.0), Pnt(87.0, 178.0), Pnt(105.0, 167.0), Pnt(66.0, 99.0), Pnt(134.0, 58.0), Pnt(123.0, 40.0))),
    DPoly(listOf(Pnt(197.0, 25.0), Pnt(211.0, 104.0), Pnt(134.0, 119.0), Pnt(138.0, 138.0), Pnt(234.0, 121.0), Pnt(216.0, 22.0))),
    listOf(Pnt(39.0, 91.0), Pnt(87.0, 178.0), Pnt(105.0, 167.0), Pnt(66.0, 99.0), Pnt(134.0, 58.0), Pnt(123.0, 40.0), Pnt(197.0, 25.0), Pnt(211.0, 104.0), Pnt(134.0, 119.0), Pnt(138.0, 138.0), Pnt(234.0, 121.0), Pnt(216.0, 22.0))
)
println("Done.")
