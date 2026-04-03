package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab
import delaunay.Pnt
import java.util.*

/**
 * Simplex volume.
 *
 * function v=simpvol(p,t)
 * %SIMPVOL Simplex volume.
 * %   V=SIMPVOL(P,T)
 *
 * %   Copyright (C) 2004-2012 Per-Olof Persson. See COPYRIGHT.TXT for details.
 */
class SimpVol {
    fun call(p: Array<DoubleArray>, t: Array<IntArray>): Array<Double> {
        return call(Matlab.asPntList(p), t.toList()).toTypedArray()
    }

    fun call(p: List<Pnt>, t: List<IntArray>): List<Double> {
        val volumes = ArrayList<Double>(t.size)
        for (tr in t) {
            volumes.add(oneVolume(p, tr))
        }
        return volumes
    }

    fun oneVolume(p: List<Pnt>, tr: IntArray): Double {
        return Pnt.content(Matlab.filter(p, tr).toTypedArray())
    }
}
