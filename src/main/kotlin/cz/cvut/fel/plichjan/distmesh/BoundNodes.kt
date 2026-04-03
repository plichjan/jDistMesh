package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar
import cz.cvut.fel.plichjan.distmesh.result.Mesh
import delaunay.Pnt
import java.util.*

class BoundNodes {
    fun call(mesh: Mesh, boundBars: List<BoundBar>): List<Pnt> {
        return call(mesh.p!!, boundBars)
    }

    fun call(p: Array<DoubleArray>, boundBars: List<BoundBar>): List<Pnt> {
        val indexes = TreeSet<Int>()
        for (bar in boundBars) {
            indexes.add(bar.a)
            indexes.add(bar.b)
        }

        val pnts = ArrayList<Pnt>(indexes.size)
        for (i in indexes) {
            pnts.add(Pnt(*p[i]))
        }
        return pnts
    }
}
