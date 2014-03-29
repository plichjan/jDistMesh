package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * Scaled edge length function h(x,y)
 */
public interface IEdgeLengthFunction {
    double call(double x, double y);
}
