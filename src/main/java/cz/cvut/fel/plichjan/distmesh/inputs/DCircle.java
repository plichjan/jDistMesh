package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * d=sqrt((p(:,1)-xc).^2+(p(:,2)-yc).^2)-r;
 */
public class DCircle implements IDistanceFunction {
    private double xc,yc,r;

    //function d=dcircle(p,xc,yc,r)
    public DCircle(double xc, double yc, double r) {
        this.xc = xc;
        this.yc = yc;
        this.r = r;
    }

    @Override
    public double call(double x, double y) {
        // d=sqrt((p(:,1)-xc).^2+(p(:,2)-yc).^2)-r;
        return Math.sqrt((x - xc) * (x - xc) + (y - yc) * (y - yc)) - r;
    }
}
