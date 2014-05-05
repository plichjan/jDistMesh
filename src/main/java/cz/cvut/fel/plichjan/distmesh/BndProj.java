package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.inputs.IDistanceFunction;
import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar;
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import delaunay.Pnt;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 function p=bndproj(p,t,fd,varargin)
 %BNDPROJ Project boundary points to true boundary
 %   P=BNDPROJ(P,T,FD,FDARGS)

 %   Copyright (C) 2004-2012 Per-Olof Persson. See COPYRIGHT.TXT for details.
 */
public class BndProj {
    public double[][] call(List<Pnt> p, List<int[]> t, IDistanceFunction fd) {
        return call(Matlab.toArray(p), t.toArray(new int[t.size()][]), fd);
    }

    public double[][] call(double[][] p, int[][] t, IDistanceFunction fd) {
        //deps=sqrt(eps)*max(max(p)-min(p));
        double deps = getDEps(p);

        //e=boundedges(p,t);
        final List<BoundBar> bars = new BoundEdges().call(p, t);
        //e=unique(e(:));
        final int dimT0 = p.length > 0 ? p[0].length + 1 : 0;
        final int dimT = t.length > 0 ? t[0].length : 0;
        Set<Integer> eSet = new TreeSet<Integer>();
        for (BoundBar bar : bars) {
            eSet.add(bar.getA());
            eSet.add(bar.getB());
            if (dimT > dimT0) {
                eSet.add(t[bar.getTr()][bar.getIx() + dimT0]);
            }
        }

        //d=feval(fd,p(e,:),varargin{:});
        //dgradx=(feval(fd,[p(e,1)+deps,p(e,2)],varargin{:})-d)/deps;
        //dgrady=(feval(fd,[p(e,1),p(e,2)+deps],varargin{:})-d)/deps;
        //dgrad2=dgradx.^2+dgrady.^2;
        //dgrad2(dgrad2==0)=1;
        //p(e,:)=p(e,:)-[d.*dgradx./dgrad2,d.*dgrady./dgrad2];
        for (Integer i : eSet) {
            Pnt a = new Pnt(p[i]);
            double d = fd.call(a.get(0), a.get(1));
            double dgradx = (fd.call(a.get(0) + deps, a.get(1)) - d ) / deps;
            double dgrady = (fd.call(a.get(0), a.get(1) + deps) - d ) / deps;
            double dgrad2=dgradx * dgradx + dgrady * dgrady;
            if (dgrad2 < Matlab.EPS) {
                dgrad2 = 1;
            }
            p[i] = a.add(-1, new Pnt(d * dgradx / dgrad2, d * dgrady / dgrad2)).getData();
        }
        return p;
    }

    private double getDEps(double[][] p) {
        double maxD = Double.NEGATIVE_INFINITY;
        double[] min = new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        double[] max = new double[] {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        for (double[] data : p) {
            for (int i = 0; i < data.length; i++) {
                double v = data[i];
                if (min[i] > v) {
                    min[i] = v;
                }
                if (max[i] < v) {
                    max[i] = v;
                }
            }
        }
        for (int i = 0; i < min.length; i++) {
            if (maxD < max[i] - min[i]) {
                maxD = max[i] - min[i];
            }
        }
        return Math.sqrt(Matlab.EPS) * maxD;
    }
/*
deps=sqrt(eps)*max(max(p)-min(p));

if size(p,2)==2
  e=boundedges(p,t);
  e=unique(e(:));

  d=feval(fd,p(e,:),varargin{:});
  dgradx=(feval(fd,[p(e,1)+deps,p(e,2)],varargin{:})-d)/deps;
  dgrady=(feval(fd,[p(e,1),p(e,2)+deps],varargin{:})-d)/deps;
  dgrad2=dgradx.^2+dgrady.^2;
  dgrad2(dgrad2==0)=1;
  p(e,:)=p(e,:)-[d.*dgradx./dgrad2,d.*dgrady./dgrad2];
elseif size(p,2)==3
  if size(t,2)==3
    tri=t;
  else
    tri=surftri(p,t);
  end
  tri=unique(tri(:));

  d=feval(fd,p(tri,:),varargin{:});
  dgradx=(feval(fd,[p(tri,1)+deps,p(tri,2),p(tri,3)],varargin{:})-d)/deps;
  dgrady=(feval(fd,[p(tri,1),p(tri,2)+deps,p(tri,3)],varargin{:})-d)/deps;
  dgradz=(feval(fd,[p(tri,1),p(tri,2),p(tri,3)+deps],varargin{:})-d)/deps;
  dgrad2=dgradx.^2+dgrady.^2+dgradz.^2;
  dgrad2(dgrad2==0)=1;
  p(tri,:)=p(tri,:)-[d.*dgradx./dgrad2,d.*dgrady./dgrad2,d.*dgradz./dgrad2];
end
*/
}
