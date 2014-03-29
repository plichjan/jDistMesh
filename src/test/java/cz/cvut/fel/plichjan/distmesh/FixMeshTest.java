package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.FixedMesh;
import delaunay.Pnt;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FixMeshTest {
    @Test
    public void testCallPrimitives() throws Exception {
        // triangle
        final FixedMesh call = new FixMesh().call(
                new double[][]{{0, 0}, {1, 0}, {0, 1}},
                new int[][]{{0, 1, 2}, {0, 2, 1}}
        );
        Assert.assertArrayEquals("Compare triangle i = " + 0, new int[]{0, 1, 2}, call.getT()[0]);
        Assert.assertArrayEquals("Compare triangle i = " + 1, new int[]{2, 0, 1}, call.getT()[1]);
    }

    @Test
    public void testCall() throws Exception {
        final List<Pnt> p = Matlab.asPntList(new double[][]{{0, 0}, {1, 0}, {0, 1}});
        final List<int[]> t = Arrays.asList(new int[][]{{0, 1, 2}, {0, 2, 1}});

        final FixedMesh call = new FixMesh().call(p, t);
        Assert.assertArrayEquals("Compare triangle i = " + 0, new int[]{0, 1, 2}, call.getT()[0]);
        Assert.assertArrayEquals("Compare triangle i = " + 1, new int[]{2, 0, 1}, call.getT()[1]);
    }

    @Test
    public void testFixElementOrientation() throws Exception {
        final List<Pnt> p = Matlab.asPntList(new double[][]{{0, 0}, {1, 0}, {0, 1}});
        final List<int[]> t = Arrays.asList(new int[][]{{0, 1, 2}, {0, 2, 1}});

        new FixMesh().fixElementOrientation(p, t);
        Assert.assertArrayEquals("Compare triangle i = " + 0, new int[]{0, 1, 2}, t.get(0));
        Assert.assertArrayEquals("Compare triangle i = " + 1, new int[]{2, 0, 1}, t.get(1));
    }
}
