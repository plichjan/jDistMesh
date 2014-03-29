package cz.cvut.fel.plichjan.distmesh.inputs;

/**
 * Distance function d(p), p is point coordinates
 *
 */
public interface IDistanceFunction {
    double call(double x, double y);
}
