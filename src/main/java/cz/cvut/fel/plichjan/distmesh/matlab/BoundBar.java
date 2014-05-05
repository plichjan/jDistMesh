package cz.cvut.fel.plichjan.distmesh.matlab;

/**
 */
public class BoundBar extends Bar {
    private int c;
    private int tr;
    private int ix;

    public BoundBar(int a, int b) {
        super(a, b);
    }

    public BoundBar(int a, int b, int c) {
        super(a, b);
        this.c = c;
    }

    public BoundBar(int a, int b, int c, int tr) {
        super(a, b);
        this.c = c;
        this.tr = tr;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getTr() {
        return tr;
    }

    public void setTr(int tr) {
        this.tr = tr;
    }

    public int getIx() {
        return ix;
    }

    public void setIx(int ix) {
        this.ix = ix;
    }
}
