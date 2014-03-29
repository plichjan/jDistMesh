package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.matlab.BoundBar;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class BoundEdgesTest {
    @Test
    public void testCallMash() throws Exception {
        // square
        final Mesh mesh = new Mesh();
        mesh.setP(new double[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}});
        mesh.setT(new int[][]{{0, 1, 2}, {1, 3, 2}});
        final List<BoundBar> edges4 = new BoundEdges().call(mesh);

        System.out.println("edges4 = " + edges4);
        Assert.assertEquals(4, edges4.size());

        Assert.assertEquals(new TreeSet<BoundBar>(Arrays.asList(
                new MyBar(1, 0),
                new MyBar(0, 2),
                new MyBar(2, 3),
                new MyBar(3, 1)
        )), new TreeSet<BoundBar>(edges4));
    }

    @Test
    public void testCall() throws Exception {
        final List<BoundBar> edges = new BoundEdges(true).call(
                new double[][]{{0, 0}, {1, 0}, {0, 1}},
                new int[][]{{0, 1, 2}});
        System.out.println("edges = " + edges);
        Assert.assertEquals(3, edges.size());

        Assert.assertEquals(new TreeSet<BoundBar>(Arrays.asList(
                new MyBar(0, 1),
                new MyBar(1, 2),
                new MyBar(2, 0)
        )), new TreeSet<BoundBar>(edges));
    }

    @Test
    public void testCallClockwise() throws Exception {
        final List<BoundBar> edges3 = new BoundEdges().call(
                new double[][]{{0, 0}, {1, 0}, {0, 1}},
                new int[][]{{0, 1, 2}});
        System.out.println("edges3 = " + edges3);
        Assert.assertEquals(3, edges3.size());

        Assert.assertEquals(new TreeSet<BoundBar>(Arrays.asList(
                new MyBar(1, 0),
                new MyBar(2, 1),
                new MyBar(0, 2)
        )), new TreeSet<BoundBar>(edges3));

        // square
        final List<BoundBar> edges4 = new BoundEdges().call(
                new double[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}},
                new int[][]{{0, 1, 2}, {1, 3, 2}});
        System.out.println("edges4 = " + edges4);
        Assert.assertEquals(4, edges4.size());

        Assert.assertEquals(new TreeSet<BoundBar>(Arrays.asList(
                new MyBar(1, 0),
                new MyBar(0, 2),
                new MyBar(2, 3),
                new MyBar(3, 1)
        )), new TreeSet<BoundBar>(edges4));
    }

    @Test
    public void testCallSquareWithHole() throws Exception {
        // square with hole
        final List<BoundBar> edges8 = new BoundEdges().call(
                new double[][]{
                        {0, 0}, {1, 0}, {1, 1}, {0, 1},
                        {1./2, 1./4}, {3./4, 1./2}, {1./2, 3./4}, {1./2, 1./2}, {1./4, 1./2}
                },
                new int[][]{{0, 1, 4}, {0, 4, 7}, {0, 7, 3}, {3, 7, 6}, {3, 6, 2}, {2, 6, 5}, {2, 5, 1}, {1, 5, 4}});
        System.out.println("edges8 = " + edges8);
        Assert.assertEquals(8, edges8.size());

        Assert.assertEquals(new TreeSet<BoundBar>(Arrays.asList(
                new MyBar(1, 0),
                new MyBar(0, 3),
                new MyBar(3, 2),
                new MyBar(2, 1),
                // hole
                new MyBar(7, 4),
                new MyBar(6, 7),
                new MyBar(5, 6),
                new MyBar(4, 5)
        )), new TreeSet<BoundBar>(edges8));
    }

    @Test
    public void testSetupOrientation() throws Exception {
        final BoundBar bar = new BoundBar(0, 1, 2);
        final List<Pnt> pnts = Arrays.asList(new Pnt(0, 0), new Pnt(1, 0), new Pnt(0, 1));

        // clockwise
        new BoundEdges().setupOrientation(pnts, Arrays.asList(bar));
        Assert.assertEquals(1, bar.getA());
        Assert.assertEquals(0, bar.getB());

        // counterclockwise
        new BoundEdges(true).setupOrientation(pnts, Arrays.asList(bar));
        Assert.assertEquals(0, bar.getA());
        Assert.assertEquals(1, bar.getB());
    }

    @Test
    public void testGetBoundaryEdges() throws Exception {
        final List<BoundBar> edges = new BoundEdges().getBoundaryEdges(new int[][]{{0, 1, 2}});

        Assert.assertEquals(3, edges.size());

        Assert.assertEquals(new TreeSet<BoundBar>(Arrays.asList(
                new BoundBar(0, 1),
                new BoundBar(1, 2),
                new BoundBar(2, 0)
        )), new TreeSet<BoundBar>(edges));
    }

    private static class MyBar extends BoundBar {

        public MyBar(int a, int b) {
            super(a, b);
            setA(a);
            setB(b);
        }
    }
}
