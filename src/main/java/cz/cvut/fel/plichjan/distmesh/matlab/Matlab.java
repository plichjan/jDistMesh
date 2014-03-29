package cz.cvut.fel.plichjan.distmesh.matlab;

import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction;
import delaunay.Pnt;
import delaunay.Triangle;
import delaunay.Triangulation;
import org.apache.log4j.Logger;

import java.util.*;

/**
 */
public class Matlab {
    public static final Logger logger = Logger.getLogger(Matlab.class);

    /** returns the distance from 1.0 to the next largest double-precision number */
    public static final double EPS = Math.nextUp(1.) - 1.;

    public static double[] vector(double first, double step, double last) {
        double[] vector = new double[(int) ((last - first) / step) + 1];
        for (int i = 0; i < vector.length; i++) {
            vector[i] = i * step + first;
        }
        return vector;
    }

    public static int[] vector(int i0, int iN) {
        final int length = iN - i0;
        final int[] ints = new int[length];
        for (int i = 0; i < length; i++) {
            ints[i] = i + i0;
        }
        return ints;
    }

    public static <E> List<E> filter(final List<E> src, ITest<E> test) {
        List<E> dst = new ArrayList<E>(src.size());
        for (int i = 0; i < src.size(); i++) {
            if (test.call(i, src.get(i))) {
                dst.add(src.get(i));
            }
        }
        return dst;
    }

    public static <E> void filterSelf(final List<E> src, ITest<E> test) {
        int i = 0;
        for (Iterator<E> iterator = src.iterator(); iterator.hasNext(); ) {
            E next = iterator.next();
            if (!test.call(i, next)) {
                iterator.remove();
            }
            i++;
        }
    }

    public static <E> List<E> filter(List<E> src, int[] ix) {
        List<E> dst = new ArrayList<E>(ix.length);
        for (int i : ix) {
            dst.add(src.get(i));
        }
        return dst;
    }

    public static List<int[]> delaunayn2(List<Pnt> p, double[][] bbox, final IDistanceFunction fd, final double geps) {
        final Pnt lb = new Pnt(bbox[0]);
        final Pnt tr = new Pnt(bbox[1]);
        final Pnt wh = tr.subtract(lb);
        Triangle tri = new Triangle(lb.subtract(wh), lb.add(new Pnt(wh.coord(0) * 3, 0)), lb.add(new Pnt(0, wh.coord(1) * 3)));
        Triangulation triangulation = new Triangulation(tri);
        for (int i = 0; i < p.size(); i++) {
            Pnt point = p.get(i);
            point.setIndex(i);
            if (fd.call(point.get(0), point.get(1)) < geps) {
                triangulation.delaunayPlace(point);
            }
        }

        ArrayList<int[]> ints = new ArrayList<int[]>(triangulation.size());
        for (Triangle triangle : triangulation) {
            if (Collections.disjoint(tri, triangle)) {
                Pnt p1 = triangle.get(0);
                Pnt p2 = triangle.get(1);
                Pnt p3 = triangle.get(2);
                ints.add(new int[] {p1.getIndex(), p2.getIndex(), p3.getIndex()});
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("initEquiTriangles done. " + p.size() + " tr: " + ints.size());
        }
        return ints;
    }

    public static List<Pnt> asPntList(double[][] p) {
        final List<Pnt> list = new ArrayList<Pnt>(p.length);
        for (double[] point : p) {
            list.add(new Pnt(point));
        }
        return list;
    }

    public static double[][] toArray(List<Pnt> p) {
        final double[][] arr = new double[p.size()][];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = p.get(i).getData();
        }
        return arr;
    }

    public static List<Pnt> computeCentroids(List<Pnt> p, List<int[]> t) {
        final List<Pnt> pmid = new ArrayList<Pnt>(t.size());
        for (int[] tr : t) {
            Pnt sum = new Pnt(new double[p.get(0).dimension()]);
            for (int i : tr) {
                sum = sum.add(p.get(i));
            }
            pmid.add(sum.scale(1. / tr.length));
        }
        return pmid;
    }

    public static double[][] computeCentroids(double[][] p, int[][] t) {
        return toArray(computeCentroids(asPntList(p), Arrays.asList(t)));
    }
}

