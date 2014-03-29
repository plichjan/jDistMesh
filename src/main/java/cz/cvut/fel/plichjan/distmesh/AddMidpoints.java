package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar;
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;

import java.util.*;

/**
 * Add midpoints to edge
 */
public class AddMidpoints {
    public Mesh call(double[][] p, int[][] t) {
        return call(Matlab.asPntList(p), Arrays.asList(t));
    }

    public Mesh call(List<Pnt> p, List<int[]> t) {
        int size = p.size();
        Map<BoundBar, BoundBar> edges = new TreeMap<BoundBar, BoundBar>();
        for (int i = 0; i < t.size(); i++) {
            int[] tr = t.get(i);
            final int n = tr.length;
            final int[] exTr = new int[n * (n + 1) / 2];
            for (int j = 0; j < n; j++) {
                int a = tr[j];
                int b = tr[(j + 1) % n];
                exTr[j] = a;
                BoundBar bar = new BoundBar(a, b);
                if (edges.containsKey(bar)) {
                    bar = edges.get(bar);
                } else {
                    // new midpoint
                    p.add(p.get(a).add(p.get(b)).scale(0.5));
                    bar.setC(size++);
                    edges.put(bar, bar);
                }
                exTr[j + n] = bar.getC();
            }
            t.set(i, exTr);
        }


        final Mesh mesh = new Mesh();
        mesh.setP(Matlab.toArray(p));
        mesh.setT(t.toArray(new int[t.size()][]));
        return mesh;
    }

}
