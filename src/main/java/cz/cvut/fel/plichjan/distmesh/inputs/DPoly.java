package cz.cvut.fel.plichjan.distmesh.inputs;

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import delaunay.Pnt;

import java.awt.geom.Path2D;
import java.util.List;

/**
 */
public class DPoly implements IDistanceFunction {
    private List<Pnt> points;
    private Path2D.Double path;

    public DPoly(double[][] points) {
        this(Matlab.asPntList(points));
    }

    public DPoly(List<Pnt> points) {
        this.points = points;
        final int n = points.size();
        path = new Path2D.Double(Path2D.WIND_NON_ZERO, n + 1);
        path.moveTo(points.get(n - 1).get(0), points.get(n - 1).get(1));
        for (Pnt point : points) {
            path.lineTo(point.get(0), point.get(1));
        }
    }

    @Override
    public double call(double x, double y) {
        double minD = Double.POSITIVE_INFINITY;

        Pnt p = new Pnt(x, y);
        Pnt v = points.get(points.size() - 1);
        for (Pnt w : points) {
            double d = minimumDistance(v, w, p);
            if (d < minD) {
                minD = d;
            }
            v = w;
        }

        return path.contains(x, y) ? - minD : minD;
    }

    // from http://stackoverflow.com/a/1501725/3123022
    private double minimumDistance(Pnt v, Pnt w, Pnt p) {
        final Pnt vw = minus(v, w);
        final double l2 = vw.dot(vw); // |v - w|^2
        if (l2 == 0.) {
            return  p.subtract(v).magnitude(); // // v == w case
        }

        double t = minus(p, v).dot(minus(w, v)) / l2;
        if (t < 0.) {
            return p.subtract(v).magnitude();
        } else if (t > 1.) {
            return p.subtract(w).magnitude();
        } else {
            Pnt projection = v.add(t, minus(w, v));
            return p.subtract(projection).magnitude();
        }
    }

    private static Pnt minus(Pnt a, Pnt b) {
        return a.subtract(b);
    }
}
