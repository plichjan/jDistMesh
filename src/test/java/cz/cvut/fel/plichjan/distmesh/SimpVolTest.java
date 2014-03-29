package cz.cvut.fel.plichjan.distmesh;

import com.google.common.primitives.Doubles;
import delaunay.Pnt;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


public class SimpVolTest {
    @Test
    public void testCallPrimitives() throws Exception {
        // triangle
        final Double[] volumes = new SimpVol().call(
                new double[][]{{0, 0}, {1, 0}, {0, 1}},
                new int[][]{{0, 1, 2}, {0, 2, 1}}
        );
        Assert.assertArrayEquals(new double[]{0.5 - 0.5},
                Doubles.toArray(Arrays.asList(volumes)), 1e-6);
    }

    @Test
    public void testCall() throws Exception {
        // triangle
        final List<Double> volumes = new SimpVol().call(
                Arrays.asList(new Pnt(0, 0), new Pnt(1, 0), new Pnt(0, 1)),
                Arrays.asList(new int[]{0, 1, 2}, new int[]{0, 2, 1})
        );
        Assert.assertArrayEquals(new double[]{0.5 - 0.5},
                Doubles.toArray(volumes), 1e-6);
    }

    @Test
    public void testOneVolume() throws Exception {
        // triangle
        Assert.assertEquals(0.5, new SimpVol().oneVolume(
                Arrays.asList(new Pnt(0, 0), new Pnt(1, 0), new Pnt(0, 1)),
                new int[]{0, 1, 2}
        ), 1e-6);

        // tetrahedron
         Assert.assertEquals(0.5 / 3, new SimpVol().oneVolume(
                Arrays.asList(new Pnt(0, 0, 0), new Pnt(1, 0, 0), new Pnt(0, 1, 0), new Pnt(0, 0, -1)),
                new int[]{0, 1, 2, 3}
        ), 1e-6);
         Assert.assertEquals(0.5 / 3, new SimpVol().oneVolume(
                Arrays.asList(new Pnt(0, 0, 0), new Pnt(0, 1, 0), new Pnt(1, 0, 0), new Pnt(0, 0, 1)),
                new int[]{0, 1, 2, 3}
        ), 1e-6);
    }
}
