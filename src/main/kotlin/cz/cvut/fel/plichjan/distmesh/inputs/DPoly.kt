package cz.cvut.fel.plichjan.distmesh.inputs

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import delaunay.Pnt
import java.awt.geom.Path2D

/**
 */
class DPoly(val points: List<Pnt>) : IDistanceFunction {
    private val path: Path2D.Double

    constructor(points: Array<DoubleArray>) : this(Matlab.asPntList(points))

    init {
        val n = points.size
        path = Path2D.Double(Path2D.WIND_NON_ZERO, n + 1)
        if (n > 0) {
            path.moveTo(points[n - 1].coord(0), points[n - 1].coord(1))
            for (point in points) {
                path.lineTo(point.coord(0), point.coord(1))
            }
        }
    }

    override fun call(x: Double, y: Double): Double {
        var minD = Double.POSITIVE_INFINITY

        val p = Pnt(x, y)
        if (points.isEmpty()) return minD
        
        var v = points.last()
        for (w in points) {
            val d = minimumDistance(v, w, p)
            if (d < minD) {
                minD = d
            }
            v = w
        }

        return if (path.contains(x, y)) -minD else minD
    }

    // from http://stackoverflow.com/a/1501725/3123022
    private fun minimumDistance(v: Pnt, w: Pnt, p: Pnt): Double {
        val vw = v.subtract(w)
        val l2 = vw.dot(vw) // |v - w|^2
        if (l2 == 0.0) {
            return p.subtract(v).magnitude() // v == w case
        }

        val pw = p.subtract(v)
        val wv = w.subtract(v)
        val t = pw.dot(wv) / l2
        return when {
            t < 0.0 -> p.subtract(v).magnitude()
            t > 1.0 -> p.subtract(w).magnitude()
            else -> {
                val projection = v.add(t, w.subtract(v))
                p.subtract(projection).magnitude()
            }
        }
    }
}
