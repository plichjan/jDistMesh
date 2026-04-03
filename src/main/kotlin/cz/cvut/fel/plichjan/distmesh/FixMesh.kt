package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import cz.cvut.fel.plichjan.distmesh.result.FixedMesh
import delaunay.Pnt
import java.util.*

/**
 * Remove duplicated/unused nodes and fix element orientation.
 *
 * function [p,t,pix]=fixmesh(p,t,ptol)
 * %FIXMESH  Remove duplicated/unused nodes and fix element orientation.
 * %   [P,T]=FIXMESH(P,T)
 *
 * %   Copyright (C) 2004-2012 Per-Olof Persson. See COPYRIGHT.TXT for details.
 */
class FixMesh(private val ptol: Double = 1024 * Matlab.EPS) {

    fun call(p: Array<DoubleArray>, t: Array<IntArray>): FixedMesh {
        return call(Matlab.asPntList(p), t.toList())
    }

    fun call(p: List<Pnt>, t: List<IntArray>): FixedMesh {
        if (p.isEmpty() || t.isEmpty()) {
            val mesh = FixedMesh()
            mesh.p = Matlab.toArray(p)
            mesh.t = t.toTypedArray()
            mesh.pix = IntArray(p.size) { it }
            return mesh
        }

        // TODO Remove duplicated/unused nodes
        // snap=max(max(p,[],1)-min(p,[],1),[],2)*ptol;
        // [foo,ix,jx]=unique(round(p/snap)*snap,'rows');
        // p=p(ix,:);
        //
        // t=reshape(jx(t),size(t));
        //
        // [pix,ix1,jx1]=unique(t);
        // t=reshape(jx1,size(t));
        // p=p(pix,:);
        // pix=ix(pix);

        // fix element orientation
        // if size(t,2)==size(p,2)+1
        if (t[0].size == p[0].dimension() + 1) {
            fixElementOrientation(p, t)
        }

        val mesh = FixedMesh()
        mesh.p = Matlab.toArray(p)
        mesh.t = t.toTypedArray()
        mesh.pix = intArrayOf()
        return mesh
    }

    // flip=simpvol(p,t)<0;
    // t(flip,[1,2])=t(flip,[2,1]);
    fun fixElementOrientation(p: List<Pnt>, t: List<IntArray>) {
        val simpVol = SimpVol()
        for (tr in t) {
            if (simpVol.oneVolume(p, tr) < 0) {
                val tmp = tr[0]
                tr[0] = tr[1]
                tr[1] = tmp
            }
        }
    }
}
