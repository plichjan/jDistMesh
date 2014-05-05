package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;

import java.util.ArrayList;
import java.util.List;

/**
 * Split every edge to two in midpoint.
 */
public class Split {
    public Mesh call(List<Pnt> p, List<int[]> t) {
        return call(Matlab.toArray(p), t.toArray(new int[t.size()][]));
    }

    public Mesh call(double[][] p, int[][] t) {
        List<int[]> tt = new ArrayList<int[]>(t.length * 4);
        for (int[] tr : t) {
            tt.add(getSubTr(tr, 0, 3, 5));
            tt.add(getSubTr(tr, 1, 4, 3));
            tt.add(getSubTr(tr, 2, 5, 4));
            tt.add(getSubTr(tr, 3, 4, 5));
        }

        final Mesh mesh = new Mesh();
        mesh.setP(p);
        mesh.setT(tt.toArray(new int[tt.size()][]));
        return mesh;
    }

    private int[] getSubTr(int[] tr, int i0, int i1, int i2) {
        return new int[]{tr[i0], tr[i1], tr[i2], };
    }

}
