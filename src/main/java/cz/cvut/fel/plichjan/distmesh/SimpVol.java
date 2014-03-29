package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import delaunay.Pnt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simplex volume.
 *
 function v=simpvol(p,t)
 %SIMPVOL Simplex volume.
 %   V=SIMPVOL(P,T)

 %   Copyright (C) 2004-2012 Per-Olof Persson. See COPYRIGHT.TXT for details.
 */
public class SimpVol {
    public Double[] call(double[][] p, int[][] t) {
        return call(Matlab.asPntList(p), Arrays.asList(t)).toArray(new Double[t.length]);
    }

    public List<Double> call(List<Pnt> p, List<int[]> t) {
        final List<Double> volumes = new ArrayList<Double>(t.size());
        for (int[] tr : t) {
            volumes.add(oneVolume(p, tr));
        }
        return volumes;
    }

    public double oneVolume(List<Pnt> p, int[] tr) {
        return Pnt.content(Matlab.filter(p, tr).toArray(new Pnt[tr.length]));
    }
}
