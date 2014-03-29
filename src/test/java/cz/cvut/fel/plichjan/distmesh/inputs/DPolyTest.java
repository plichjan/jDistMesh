package cz.cvut.fel.plichjan.distmesh.inputs;

import delaunay.Pnt;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 */
public class DPolyTest {
    @Test
    public void testCallArr() throws Exception {
        final DPoly dPoly = new DPoly(new double[][]{{0, 0}, {1, 1}});
        Assert.assertEquals(1, dPoly.call(2, 1), 0.0001);
        Assert.assertEquals(1, dPoly.call(0, -1), 0.0001);
        Assert.assertEquals(Math.sqrt(2), dPoly.call(-1, -1), 0.0001);
        Assert.assertEquals(Math.sqrt(2) / 2., dPoly.call(1, 0), 0.0001);
        Assert.assertEquals(Math.sqrt(2) / 2., dPoly.call(0, 1), 0.0001);
    }

    @Test
    public void testCall() throws Exception {
        final DPoly dPoly = new DPoly(Arrays.asList(new Pnt(0, 0), new Pnt(1, 1)));
        Assert.assertEquals(1, dPoly.call(2, 1), 0.0001);
        Assert.assertEquals(1, dPoly.call(0, -1), 0.0001);
        Assert.assertEquals(Math.sqrt(2), dPoly.call(-1, -1), 0.0001);
        Assert.assertEquals(Math.sqrt(2) / 2., dPoly.call(1, 0), 0.0001);
        Assert.assertEquals(Math.sqrt(2) / 2., dPoly.call(0, 1), 0.0001);
    }

    @Test
    public void testCall0() throws Exception {
        final DPoly dPoly = new DPoly(Arrays.asList(new Pnt(0, 0), new Pnt(1, 0)));
        Assert.assertEquals(1, dPoly.call(-1, 0), 0.0001);
        Assert.assertEquals(1, dPoly.call(2, 0), 0.0001);
        Assert.assertEquals(1, dPoly.call(0, 1), 0.0001);
        Assert.assertEquals(1, dPoly.call(0, -1), 0.0001);
        Assert.assertEquals(1, dPoly.call(1, 1), 0.0001);
        Assert.assertEquals(1, dPoly.call(1, -1), 0.0001);
        Assert.assertEquals(1, dPoly.call(0.5, 1), 0.0001);
        Assert.assertEquals(1, dPoly.call(0.5, -1), 0.0001);
    }
}
