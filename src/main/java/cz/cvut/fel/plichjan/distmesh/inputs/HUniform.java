package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 */
public class HUniform implements IEdgeLengthFunction {
    @Override
    public double call(double x, double y) {
        return 1.;
    }
}
