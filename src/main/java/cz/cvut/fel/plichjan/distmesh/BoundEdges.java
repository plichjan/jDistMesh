package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar;
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 function e=boundedges(p,t)
 %BOUNDEDGES Find boundary edges from triangular mesh
 %   E=BOUNDEDGES(P,T)

 %   Copyright (C) 2004-2012 Per-Olof Persson. See COPYRIGHT.TXT for details.
 */
public class BoundEdges {
    // direction of edge
    private boolean counterClock = false;

    /**
     * Default orientation is clockwise
     */
    public BoundEdges() {
    }

    /**
     * Set up orientation of edge in result function
     * @param counterClock true if counterclockwise orientation
     */
    public BoundEdges(boolean counterClock) {
        this.counterClock = counterClock;
    }

    public List<BoundBar> call(Mesh mesh) {
        return call(mesh.getP(), mesh.getT());
    }

    public List<BoundBar> call(double[][] p, int[][] t) {
        final List<Pnt> pnts = Matlab.asPntList(p);

        //Form all edges, non-duplicates are boundary edges
        final int n = p.length > 0 ? p[0].length + 1 : 0;
        List<BoundBar> e = getBoundaryEdges(t, n);

        //Orientation
        setupOrientation(pnts, e);

        return e;
    }

    void setupOrientation(List<Pnt> pnts, List<BoundBar> e) {
        //v1=p(e(:,2),:)-p(e(:,1),:);
        //v2=p(node3,:)-p(e(:,1),:);
        //ix=find(v1(:,1).*v2(:,2)-v1(:,2).*v2(:,1)>0);
        //e(ix,[1,2])=e(ix,[2,1]);
        for (BoundBar bar : e) {
            Pnt v1 = pnts.get(bar.getB()).subtract(pnts.get(bar.getA()));
            Pnt v2 = pnts.get(bar.getC()).subtract(pnts.get(bar.getA()));
            // cross > 0 => C is on left side
            double cross = v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0);
            if (cross > 0 && !counterClock || cross < 0 && counterClock) {
                int a = bar.getA();
                bar.setA(bar.getB());
                bar.setB(a);
            }
        }
    }

    List<BoundBar> getBoundaryEdges(int[][] t, int n) {
        //% Form all edges, non-duplicates are boundary edges
        //edges=[t(:,[1,2]);
        //       t(:,[1,3]);
        //       t(:,[2,3])];
        //node3=[t(:,3);t(:,2);t(:,1)];
        //edges=sort(edges,2);
        //[foo,ix,jx]=unique(edges,'rows');
        //vec=histc(jx,1:max(jx));
        //qx=find(vec==1);
        //e=edges(ix(qx),:);
        //node3=node3(ix(qx));

        Set<BoundBar> edges = new TreeSet<BoundBar>();
        Set<BoundBar> innerEdges = new TreeSet<BoundBar>();
        for (int i = 0; i < t.length; i++) {
            int[] tr = t[i];
            for (int j = 0; j < n; j++) {
                int a = tr[j];
                int b = tr[(j + 1) % n];
                int c = tr[(j + 2) % n];
                final BoundBar bar = createBar(a, b, c, i);
                bar.setIx(j);
                if (edges.contains(bar)) {
                    innerEdges.add(bar);
                } else {
                    edges.add(bar);
                }
            }
        }
        edges.removeAll(innerEdges);
        return new ArrayList<BoundBar>(edges);
    }

    private BoundBar createBar(int a, int b, int c, int trIndex) {
        return new BoundBar(a, b, c, trIndex);
    }


}
