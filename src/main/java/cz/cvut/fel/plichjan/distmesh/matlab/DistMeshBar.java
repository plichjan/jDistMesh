package cz.cvut.fel.plichjan.distmesh.matlab;

import delaunay.Pnt;

/**
 * Edge
 */
public class DistMeshBar extends Bar {

    private Pnt barvec;
    private double L;
    private double hbar;
    private double L0;
    private double F;
    private Pnt Fvec;

    //sort(bars,2)
    public DistMeshBar(int a, int b) {
        super(a, b);
    }

    public Pnt getBarvec() {
        return barvec;
    }

    public void setBarvec(Pnt barvec) {
        this.barvec = barvec;
    }

    public double getL() {
        return L;
    }

    public void setL(double l) {
        L = l;
    }

    public double getHbar() {
        return hbar;
    }

    public void setHbar(double hbar) {
        this.hbar = hbar;
    }

    public double getL0() {
        return L0;
    }

    public void setL0(double l0) {
        L0 = l0;
    }

    public double getF() {
        return F;
    }

    public void setF(double f) {
        F = f;
    }

    public Pnt getFvec() {
        return Fvec;
    }

    public void setFvec(Pnt fvec) {
        Fvec = fvec;
    }

}
