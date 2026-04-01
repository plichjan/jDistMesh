package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import java.util.*

/**
 * Add midpoints to edge
 */
class AddMidpoints {
    fun call(p: Array<DoubleArray>, t: Array<IntArray>): Mesh {
        return call(Matlab.asPntList(p).toMutableList(), t.toMutableList())
    }

    fun call(p: MutableList<Pnt>, t: MutableList<IntArray>): Mesh {
        var size = p.size
        val edges = TreeMap<BoundBar, BoundBar>()
        for (i in t.indices) {
            val tr = t[i]
            val n = tr.size
            // n=3 for triangle, n=4 for quad etc. Initial version assumes triangles.
            // exTr size: for triangle (n=3) it's 3 * 4 / 2 = 6 (3 vertices + 3 midpoints)
            val exTr = IntArray(n * (n + 1) / 2)
            for (j in 0 until n) {
                val a = tr[j]
                val b = tr[(j + 1) % n]
                exTr[j] = a
                var bar = BoundBar(a, b)
                if (edges.containsKey(bar)) {
                    bar = edges[bar]!!
                } else {
                    // new midpoint
                    p.add(p[a].add(p[b]).scale(0.5))
                    bar.c = size++
                    edges[bar] = bar
                }
                exTr[j + n] = bar.c
            }
            t[i] = exTr
        }

        val mesh = Mesh()
        mesh.p = Matlab.toArray(p)
        mesh.t = t.toTypedArray()
        return mesh
    }
}
