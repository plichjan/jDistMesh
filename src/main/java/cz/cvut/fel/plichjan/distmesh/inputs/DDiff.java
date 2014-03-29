package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * function d=ddiff(d1,d2), d=max(d1,-d2);
 */
public class DDiff implements IDistanceFunction {
    private IDistanceFunction d1, d2;

    //function d=ddiff(d1,d2)
    public DDiff(IDistanceFunction d1, IDistanceFunction d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public double call(double x, double y) {
        // d=max(d1,-d2);
        final double dd1 = d1.call(x, y);
        final double dd2 = d2.call(x, y);
        return Math.signum(1. + Math.signum(dd1) + Math.signum(-dd2)) * Math.min(Math.abs(dd1), Math.abs(dd2));
    }
}
