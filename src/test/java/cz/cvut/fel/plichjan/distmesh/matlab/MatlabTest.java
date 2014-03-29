package cz.cvut.fel.plichjan.distmesh.matlab;

import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle;
import delaunay.Pnt;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatlabTest {
    @Test
    public void testVector() throws Exception {
        double[] v100 = Matlab.vector(0, 0.01, 1);
        Assert.assertEquals(101, v100.length);
        Assert.assertEquals(0., v100[0]);
        Assert.assertEquals(1., v100[100]);

        double[] vm100 = Matlab.vector(1, -0.01, 0);
        Assert.assertEquals(101, vm100.length);
        Assert.assertEquals(1., vm100[0]);
        Assert.assertEquals(0., vm100[100]);

        double[] v1000 = Matlab.vector(0, 0.001, 1);
        Assert.assertEquals(1001, v1000.length);
        Assert.assertEquals(0., v1000[0]);
        Assert.assertEquals(1., v1000[1000]);

        double[] v4 = Matlab.vector(0, 0.3, 1);
        org.junit.Assert.assertArrayEquals(new double[]{0, 0.3, 0.6, 0.9}, v4, 0.01);
    }

    @Test
    public void testFilterSelf() throws Exception {
        List<Integer> src = Arrays.asList(0, 1, 2, 3, 4, 5);

        List<Integer> src1 = new ArrayList<Integer>(src);
        Matlab.filterSelf(src1, new ITest<Integer>() {
            @Override
            public boolean call(int i, Integer item) {
                return item > 3;
            }
        });
        Assert.assertEquals(Arrays.asList(4, 5), src1);

        List<Integer> src2 = new ArrayList<Integer>(src);
        Matlab.filterSelf(src2, new ITest<Integer>() {
            @Override
            public boolean call(int i, Integer item) {
                return item > 3 || item < 2;
            }
        });
        Assert.assertEquals(Arrays.asList(0, 1, 4, 5), src2);

        List<Integer> src3 = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 0, 1, 0));
        Matlab.filterSelf(src3, new ITest<Integer>() {
            @Override
            public boolean call(int i, Integer item) {
                return i > 3;
            }
        });
        Assert.assertEquals(Arrays.asList(0,1,0), src3);
    }

    @Test
    public void testFilter() throws Exception {
        List<Integer> src = Arrays.asList(0, 1, 2, 3, 4, 5);

        Assert.assertEquals(Arrays.asList(4, 5), Matlab.filter(src, new ITest<Integer>() {
            @Override
            public boolean call(int i, Integer item) {
                return item > 3;
            }
        }));

        Assert.assertEquals(Arrays.asList(0, 1, 4, 5), Matlab.filter(src, new ITest<Integer>() {
            @Override
            public boolean call(int i, Integer item) {
                return item > 3 || item < 2;
            }
        }));

        Assert.assertEquals(Arrays.asList(0,1,0), Matlab.filter(Arrays.asList(1, 1, 1, 1, 0, 1, 0), new ITest<Integer>() {
            @Override
            public boolean call(int i, Integer item) {
                return i > 3;
            }
        }));
    }

    @Test
    public void testDelaunayn2() throws Exception {
        List<int[]> triangles = Matlab.delaunayn2(Arrays.asList(
                new Pnt(0, 0),
                new Pnt(1, 0),
                new Pnt(1, 1),
                new Pnt(0, 2)
        ), new double[][]{{0, 0}, {2, 2}}, new DRectangle(0, 1, 0, 2), 0.01);

        Assert.assertEquals(2, triangles.size());
    }
}
