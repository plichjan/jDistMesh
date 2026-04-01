package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction
import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import delaunay.Pnt
import java.util.TreeSet
import kotlin.math.sqrt

/**
 * Project boundary points to true boundary
 */
class BndProj {
    fun call(p: List<Pnt>, t: List<IntArray>, fd: IDistanceFunction): Array<DoubleArray> {
        return call(Matlab.toArray(p), t.toTypedArray(), fd)
    }

    fun call(p: Array<DoubleArray>, t: Array<IntArray>, fd: IDistanceFunction): Array<DoubleArray> {
        val deps = getDEps(p)

        val bars = BoundEdges().call(p, t)
        
        val dimT0 = if (p.isNotEmpty()) p[0].size + 1 else 0
        val dimT = if (t.isNotEmpty()) t[0].size else 0
        val eSet = TreeSet<Int>()
        for (bar in bars) {
            eSet.add(bar.a)
            eSet.add(bar.b)
            if (dimT > dimT0) {
                eSet.add(t[bar.tr][bar.ix + dimT0])
            }
        }

        for (i in eSet) {
            val a = Pnt(*p[i])
            val d = fd.call(a.coord(0), a.coord(1))
            val dgradx = (fd.call(a.coord(0) + deps, a.coord(1)) - d) / deps
            val dgrady = (fd.call(a.coord(0), a.coord(1) + deps) - d) / deps
            var dgrad2 = dgradx * dgradx + dgrady * dgrady
            if (dgrad2 < Matlab.EPS) {
                dgrad2 = 1.0
            }
            p[i] = a.add(-1.0, Pnt(d * dgradx / dgrad2, d * dgrady / dgrad2)).getData()
        }
        return p
    }

    private fun getDEps(p: Array<DoubleArray>): Double {
        if (p.isEmpty()) return 0.0
        val dim = p[0].size
        val min = DoubleArray(dim) { Double.POSITIVE_INFINITY }
        val max = DoubleArray(dim) { Double.NEGATIVE_INFINITY }
        
        for (data in p) {
            for (i in data.indices) {
                if (min[i] > data[i]) min[i] = data[i]
                if (max[i] < data[i]) max[i] = data[i]
            }
        }
        
        var maxD = Double.NEGATIVE_INFINITY
        for (i in min.indices) {
            val diff = max[i] - min[i]
            if (maxD < diff) maxD = diff
        }
        return sqrt(Matlab.EPS) * maxD
    }
}
