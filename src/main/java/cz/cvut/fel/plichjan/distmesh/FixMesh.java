package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.FixedMesh;
import delaunay.Pnt;

import java.util.Arrays;
import java.util.List;

/**
 * Remove duplicated/unused nodes and fix element orientation.
 *
 function [p,t,pix]=fixmesh(p,t,ptol)
 %FIXMESH  Remove duplicated/unused nodes and fix element orientation.
 %   [P,T]=FIXMESH(P,T)

 %   Copyright (C) 2004-2012 Per-Olof Persson. See COPYRIGHT.TXT for details.

 */
public class FixMesh {
        //if nargin<3, ptol=1024*eps; end
    private double ptol = 1024 * Matlab.EPS;

    public FixMesh() {
    }

    public FixMesh(double ptol) {
        this.ptol = ptol;
    }

    public FixedMesh call(double[][] p, int[][] t) {
        return call(Matlab.asPntList(p), Arrays.asList(t));
    }

    public FixedMesh call(List<Pnt> p, List<int[]> t) {
        //if nargin>=2 & (isempty(p) | isempty(t)), pix=1:size(p,1); return; end
        if (p.size() == 0 || t.size() == 0) {
            final FixedMesh mesh = new FixedMesh();
            mesh.setP(Matlab.toArray(p));
            mesh.setT(t.toArray(new int[t.size()][]));
            mesh.setPix(Matlab.vector(0, p.size()));
            return mesh;
        }

        // TODO Remove duplicated/unused nodes
        //snap=max(max(p,[],1)-min(p,[],1),[],2)*ptol;
        //[foo,ix,jx]=unique(round(p/snap)*snap,'rows');
        //p=p(ix,:);
        //
        //t=reshape(jx(t),size(t));
        //
        //[pix,ix1,jx1]=unique(t);
        //t=reshape(jx1,size(t));
        //p=p(pix,:);
        //pix=ix(pix);

        // fix element orientation
        //if size(t,2)==size(p,2)+1
        if (t.get(0).length == p.get(0).dimension() + 1) {
            fixElementOrientation(p, t);
        }

        final FixedMesh mesh = new FixedMesh();
        mesh.setP(Matlab.toArray(p));
        mesh.setT(t.toArray(new int[t.size()][]));
        mesh.setPix(new int[0]);
        return mesh;
    }

    //flip=simpvol(p,t)<0;
    //t(flip,[1,2])=t(flip,[2,1]);
    void fixElementOrientation(List<Pnt> p, List<int[]> t) {
        final SimpVol simpVol = new SimpVol();
        for (int[] tr : t) {
            if (simpVol.oneVolume(p, tr) < 0) {
                int tmp = tr[0];
                tr[0] = tr[1];
                tr[1] = tmp;
            }
        }
    }
}
