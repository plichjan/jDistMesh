package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar;
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BoundNodes {
    public List<Pnt> call(Mesh mesh, List<BoundBar> boundBars) {
        return call(mesh.getP(), boundBars);
    }

    public List<Pnt> call(double[][] p, List<BoundBar> boundBars) {
        final Set<Integer> indexes = new TreeSet<Integer>();
        for (BoundBar bar : boundBars) {
            indexes.add(bar.getA());
            indexes.add(bar.getB());
        }

        final ArrayList<Pnt> pnts = new ArrayList<Pnt>(indexes.size());
        for (Integer i : indexes) {
            pnts.add(new Pnt(p[i]));
        }
        return pnts;
    }
}
