package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * function d=drectangle(p,x1,x2,y1,y2)
 * d=-min(min(min(-y1+p(:,2),y2-p(:,2)),-x1+p(:,1)),x2-p(:,1));
 */
public class DRectangle implements IDistanceFunction {
    private double x1,x2,y1,y2;

    //function d=drectangle(p,x1,x2,y1,y2)
    public DRectangle(double x1, double x2, double y1, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    @Override
    public double call(double x, double y) {
        // d=-min(min(min(-y1+p(:,2),y2-p(:,2)),-x1+p(:,1)),x2-p(:,1));
        return -Math.min(Math.min(Math.min(-y1 + y, y2 - y), -x1 + x), x2 - x);
    }
}
