package cz.cvut.fel.plichjan;

import cz.cvut.fel.plichjan.distmesh.result.Mesh;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jan.Plichta
 * Date: 24.1.14
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public interface IViewer {
    void setNewPoints(List<Point2D> points, List<int[]> t, double[][] bbox);

    void drawMesh(Mesh mesh);
}
