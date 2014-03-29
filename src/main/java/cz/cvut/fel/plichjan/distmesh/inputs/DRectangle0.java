package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * function d=drectangle0(p,x1,x2,y1,y2)
 */
public class DRectangle0 implements IDistanceFunction {
    private double x1,x2,y1,y2;

    //function d=drectangle0(p,x1,x2,y1,y2)
    public DRectangle0(double x1, double x2, double y1, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    @Override
    public double call(double x, double y) {
        double d1=y1-y;
        double d2=-y2+y;
        double d3=x1-x;
        double d4=-x2+x;
        
        double d5=Math.sqrt(d1*d1+d3*d3);
        double d6=Math.sqrt(d1*d1+d4*d4);
        double d7=Math.sqrt(d2*d2+d3*d3);
        double d8=Math.sqrt(d2*d2+d4*d4);
        
        double d=-Math.min(Math.min(Math.min(-d1,-d2),-d3),-d4);

        d = d1 > 0 & d3 > 0 ? d5
                : d1 > 0 & d4 > 0 ? d6
                : d2 > 0 & d3 > 0 ? d7
                : d2 > 0 & d4 > 0 ? d8
                : d;

        return d;
    }
}
