@file:Suppress("unused")

package cz.cvut.fel.plichjan.distmesh.script

import cz.cvut.fel.plichjan.distmesh.ElePrinter
import cz.cvut.fel.plichjan.distmesh.NodePrinter
import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import kotlin.math.abs

// Node flag constants
const val IS_SET_DISPLACEMENT = 2
const val IS_SET_POTENTIAL = 4
const val IS_SET_FORCE = 8
const val IS_PRINTABLE = 16
const val IS_SET_BOUNDARY_FORCE = 32
const val WALL_NODE = 64

data class Constrain(
    val df: IDistanceFunction,
    val flags: Int = 0,
    val potential: Double = 0.0,
    val displacement: DoubleArray = doubleArrayOf(0.0, 0.0),
    val bForce: DoubleArray = doubleArrayOf(0.0, 0.0),
    val vForce: DoubleArray = doubleArrayOf(0.0, 0.0)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Constrain

        if (flags != other.flags) return false
        if (potential != other.potential) return false
        if (df != other.df) return false
        if (!displacement.contentEquals(other.displacement)) return false
        if (!bForce.contentEquals(other.bForce)) return false
        if (!vForce.contentEquals(other.vForce)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flags
        result = 31 * result + potential.hashCode()
        result = 31 * result + df.hashCode()
        result = 31 * result + displacement.contentHashCode()
        result = 31 * result + bForce.contentHashCode()
        result = 31 * result + vForce.contentHashCode()
        return result
    }
}

data class TrConstrain(
    val df: IDistanceFunction,
    val matId: Int
)

fun callDf(df: IDistanceFunction, node: DoubleArray): Double = df.call(node[0], node[1])

fun storeMesh(file: String, mesh2: Mesh, h0: Double, list: List<Constrain>, trList: List<TrConstrain>) {
    val zero = doubleArrayOf(0.0, 0.0)

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
