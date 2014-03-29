package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * function d=dunion(d1,d2), d=min(d1,d2);
 */
public class DUnion implements IDistanceFunction {
    private IDistanceFunction d1, d2;

    //function d=dunion(d1,d2)
    public DUnion(IDistanceFunction d1, IDistanceFunction d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public double call(double x, double y) {
        // d=min(d1,d2);
        return Math.min(d1.call(x, y), d2.call(x, y));
    }
}
