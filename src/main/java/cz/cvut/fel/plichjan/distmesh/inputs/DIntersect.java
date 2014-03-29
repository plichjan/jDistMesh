package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * function d=dintersect(d1,d2), d=max(d1,d2);
 */
public class DIntersect implements IDistanceFunction {
    private IDistanceFunction d1, d2;

    //function d=dintersect(d1,d2)
    public DIntersect(IDistanceFunction d1, IDistanceFunction d2) {
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public double call(double x, double y) {
        // d=max(d1,d2);
        return Math.max(d1.call(x, y), d2.call(x, y));
    }
}
