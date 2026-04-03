package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import java.util.*

/**
 * Split every edge to two in midpoint.
 */
class Split {
    fun call(p: List<Pnt>, t: List<IntArray>): Mesh {
        return call(Matlab.toArray(p), t.toTypedArray())
    }

    fun call(p: Array<DoubleArray>, t: Array<IntArray>): Mesh {
        val tt = ArrayList<IntArray>(t.size * 4)
        for (tr in t) {
            tt.add(getSubTr(tr, 0, 3, 5))
            tt.add(getSubTr(tr, 1, 4, 3))
            tt.add(getSubTr(tr, 2, 5, 4))
            tt.add(getSubTr(tr, 3, 4, 5))
        }

        val mesh = Mesh()
        mesh.p = p
        mesh.t = tt.toTypedArray()
        return mesh
    }

    private fun getSubTr(tr: IntArray, i0: Int, i1: Int, i2: Int): IntArray {
        return intArrayOf(tr[i0], tr[i1], tr[i2])
    }
}
