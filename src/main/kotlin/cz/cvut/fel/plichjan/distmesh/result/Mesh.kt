package cz.cvut.fel.plichjan.distmesh.result

/**
 * The node positions p. This N-by-M array contains the M coordinates for each of the N nodes.
 *
 * The triangle indices t. The row associated with each triangle has M+1 integer entries to specify node numbers in that triangle.
 */
open class Mesh {
    var p: Array<DoubleArray>? = null
    var t: Array<IntArray>? = null

    fun toTsin(): String {
        val p = this.p ?: return "null"
        val sb = StringBuilder()
        sb.append(p.size).append("\n")
        for (point in p) {
            sb.append(point[0]).append(" ")
            sb.append(point[1]).append(" ")
            sb.append(0.0).append("\n")
        }
        return sb.toString()
    }

    override fun toString(): String {
        return "Mesh{\np=${p?.contentDeepToString()}, \nt=${t?.contentDeepToString()}}"
    }
}
