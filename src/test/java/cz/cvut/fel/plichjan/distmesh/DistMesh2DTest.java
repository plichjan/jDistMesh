package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.inputs.DCircle;
import cz.cvut.fel.plichjan.distmesh.inputs.DDiff;
import cz.cvut.fel.plichjan.distmesh.inputs.DRectangle;
import cz.cvut.fel.plichjan.distmesh.inputs.HUniform;
import cz.cvut.fel.plichjan.distmesh.matlab.DistMeshBar;
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class DistMesh2DTest {

    @Test
    public void testCall() throws Exception {

        Mesh mesh = new DistMesh2D().call(
                new DCircle(0, 0, 1),
                new HUniform(),
                0.5, new double[][]{{-1, -1}, {1, 1}},
                new ArrayList<Pnt>()
        );

        System.out.println(mesh.toTsin());
    }
/*
          Ftot=full(sparse(bars(:,[1,1,2,2]),ones(size(F))*[1,2,1,2],[Fvec,-Fvec],N,2));
          Ftot(1:size(pfix,1),:)=0;                          % Force = 0 at fixed points
    public static List<Vector> createFtot(List<Bar> bars, int n, int nfix) {
*/
    @Test
    public void testCreateFtot() throws Exception {
        final double Fscale=1.2;

        List<DistMeshBar> bars;
        bars = Arrays.asList(new DistMeshBar(0, 1), new DistMeshBar(0, 2), new DistMeshBar(0, 3));
        //fh = @huniform; p =[0,0;0,1;0,2;0,3]; bars=[1,2;1,3;1,4]; Fscale=1.2;
        List<Pnt> p = Arrays.asList(new Pnt(0, 0), new Pnt(0, 1), new Pnt(0, 2), new Pnt(0, 3));
        DistMesh2D.setupLengths(p, bars, new HUniform(), Fscale);
        DistMesh2D.setupForces(bars);
        //N=4
        List<Pnt> ftot = DistMesh2D.createFtot(bars, p.size(), 0);

        Assert.assertEquals(4, ftot.size());
        List<Pnt> expectFtot = Arrays.asList(new Pnt(0, -2.1846), new Pnt(0, 1.5923), new Pnt(0, 0.5923), new Pnt(0, 0));
        for (int i = 0; i < expectFtot.size(); i++) {
            Pnt expect =  expectFtot.get(i);
            Assert.assertArrayEquals("i = " + i, expect.getData(), ftot.get(i).getData(), 0.0001);
        }
    }

/*
          F=max(L0-L,0);                                     % Bar forces (scalars)
          Fvec=F./L*[1,1].*barvec;                           % Bar forces (x,y components)
    public static void setupForces(List<Bar> bars) {
*/
    @Test
    public void testSetupForces() throws Exception {
        final double Fscale=1.2;

        List<DistMeshBar> bars;
        bars = Arrays.asList(new DistMeshBar(0, 1), new DistMeshBar(0, 2), new DistMeshBar(0, 3));
        //fh = @huniform; p =[0,0;0,1;0,2;0,3]; bars=[1,2;1,3;1,4]; Fscale=1.2;
        DistMesh2D.setupLengths(Arrays.asList(new Pnt(0, 0), new Pnt(0, 1), new Pnt(0, 2), new Pnt(0, 3)),
                bars, new HUniform(), Fscale);
        DistMesh2D.setupForces(bars);

        Assert.assertEquals(1.5923, bars.get(0).getF(), 0.0001);
        Assert.assertEquals(0.5923, bars.get(1).getF(), 0.0001);
        Assert.assertEquals(0.0000, bars.get(2).getF(), 0.0001);
        Assert.assertArrayEquals(new Pnt(0, -1.5923).getData(), bars.get(0).getFvec().getData(), 0.0001);
        Assert.assertArrayEquals(new Pnt(0, -0.5923).getData(), bars.get(1).getFvec().getData(), 0.0001);
        Assert.assertArrayEquals(new Pnt(0, -0.0000).getData(), bars.get(2).getFvec().getData(), 0.0001);
    }

/*
          barvec=p(bars(:,1),:)-p(bars(:,2),:);              % List of bar vectors
          L=sqrt(sum(barvec.^2,2));                          % L = Bar lengths
          hbars=feval(fh,(p(bars(:,1),:)+p(bars(:,2),:))/2);
          L0=hbars*Fscale*sqrt(sum(L.^2)/sum(hbars.^2));     % L0 = Desired lengths
    public static void setupLengths(List<Pnt> p, List<Bar> bars, IEdgeLengthFunction fh, double fscale) {
*/
    @Test
    public void testSetupLengths() throws Exception {
        final double Fscale=1.2;

        List<DistMeshBar> bars;
        bars = Arrays.asList(new DistMeshBar(0, 1));
        //fh = @huniform; p =[0,0;0,1]; bars=[1,2]; Fscale=1.2;
        DistMesh2D.setupLengths(Arrays.asList(new Pnt(0, 0), new Pnt(0, 1)),
                bars, new HUniform(), Fscale);

        DistMeshBar bar = bars.get(0);
        org.junit.Assert.assertArrayEquals(new Pnt(0, -1).getData(), bar.getBarvec().getData(), Matlab.EPS);
        Assert.assertEquals(1., bar.getL(), Matlab.EPS);
        Assert.assertEquals(1., bar.getHbar(), Matlab.EPS);
        Assert.assertEquals(1.2, bar.getL0(), Matlab.EPS);

        bars = Arrays.asList(new DistMeshBar(0, 1), new DistMeshBar(0, 2), new DistMeshBar(0, 3));
        //fh = @huniform; p =[0,0;0,1;0,2;0,3]; bars=[1,2;1,3;1,4]; Fscale=1.2;
        DistMesh2D.setupLengths(Arrays.asList(new Pnt(0, 0), new Pnt(0, 1), new Pnt(0, 2), new Pnt(0, 3)),
                bars, new HUniform(), Fscale);

        org.junit.Assert.assertArrayEquals(new Pnt(0, -1).getData(), bars.get(0).getBarvec().getData(), Matlab.EPS);
        org.junit.Assert.assertArrayEquals(new Pnt(0, -2).getData(), bars.get(1).getBarvec().getData(), Matlab.EPS);
        org.junit.Assert.assertArrayEquals(new Pnt(0, -3).getData(), bars.get(2).getBarvec().getData(), Matlab.EPS);
        Assert.assertEquals(1., bars.get(0).getL(), Matlab.EPS);
        Assert.assertEquals(2., bars.get(1).getL(), Matlab.EPS);
        Assert.assertEquals(3., bars.get(2).getL(), Matlab.EPS);
        Assert.assertEquals(1., bars.get(0).getHbar(), Matlab.EPS);
        Assert.assertEquals(1., bars.get(1).getHbar(), Matlab.EPS);
        Assert.assertEquals(1., bars.get(2).getHbar(), Matlab.EPS);
        Assert.assertEquals(2.5923, bars.get(0).getL0(), 0.0001);
        Assert.assertEquals(2.5923, bars.get(1).getL0(), 0.0001);
        Assert.assertEquals(2.5923, bars.get(2).getL0(), 0.0001);

    }

/*
//            bars=[t(:,[1,2]);t(:,[1,3]);t(:,[2,3])];         % Interior bars duplicated
//            bars=unique(sort(bars,2),'rows');                % Bars as node pairs
    private void resetBars(List<Bar> bars, List<int[]> t) {
*/
    @Test
    public void testResetBars() throws Exception {
        List<int[]> t;

        t = new ArrayList<int[]>();
        t.add(new int[]{0, 1, 2});
        t.add(new int[]{0, 2, 3});

        List<DistMeshBar> bars = new ArrayList<DistMeshBar>();
        DistMesh2D.resetBars(bars, t);

        Assert.assertEquals(Arrays.asList(new DistMeshBar(0, 1), new DistMeshBar(0, 2), new DistMeshBar(0, 3), new DistMeshBar(1, 2), new DistMeshBar(2, 3)),
                bars);
    }

/*
//            pmid=(p(t(:,1),:)+p(t(:,2),:)+p(t(:,3),:))/3;    % Compute centroids
//            t=t(feval(fd,pmid,varargin{:})<-geps,:);         % Keep interior triangles
    public static void keepInteriorTriangles(List<Pnt> p, List<int[]> t, final IDistanceFunction fd, final double geps) {
*/
    @Test
    public void testKeepInteriorTriangles() throws Exception {
        DDiff fd = new DDiff(new DRectangle(0, 2, 0, 2), new DRectangle(0, 1, 0, 1));
        List<int[]> t;

        t = new ArrayList<int[]>();
        t.add(new int[]{0, 1, 2});
        DistMesh2D.keepInteriorTriangles(Arrays.asList(new Pnt(0, 0), new Pnt(1, 0), new Pnt(0, 1)),
                t, fd, Matlab.EPS);
        Assert.assertTrue(t.isEmpty());

        t = new ArrayList<int[]>();
        t.add(new int[]{0, 1, 2});
        DistMesh2D.keepInteriorTriangles(Arrays.asList(new Pnt(1, 0), new Pnt(2, 0), new Pnt(1, 1)),
                t, fd, Matlab.EPS);
        Assert.assertFalse(t.isEmpty());
    }

/*
//          if max(sqrt(sum((p-pold).^2,2))/h0)>ttol           % Any large movement?
    public static boolean anyLargeMovement(List<Pnt> p, List<Pnt> pold, double h0, double ttol) {
*/
    @Test
    public void testAnyLargeMovement() throws Exception {
        Assert.assertFalse(DistMesh2D.anyLargeMovement(
                Arrays.asList(new Pnt(0, 0)),
                Arrays.asList(new Pnt(0.01, 0)),
                1, 0.1));

        Assert.assertTrue(DistMesh2D.anyLargeMovement(
                Arrays.asList(new Pnt(0, 0)),
                Arrays.asList(new Pnt(0.2, 0)),
                1, 0.1));

        Assert.assertTrue(DistMesh2D.anyLargeMovement(
                Arrays.asList(new Pnt(0, 0)),
                Arrays.asList(new Pnt(0.01, 0), new Pnt(1, 1)),
                1, 0.1));

        Assert.assertTrue(DistMesh2D.anyLargeMovement(
                Arrays.asList(new Pnt(0, 0), new Pnt(1, 2)),
                Arrays.asList(new Pnt(0.01, 0), new Pnt(1, 1)),
                1, 0.1));

    }

/*
//        if ~isempty(pfix), p=setdiff(p,pfix,'rows'); end     % Remove duplicated nodes
//        pfix=unique(pfix,'rows'); nfix=size(pfix,1);
//        p=[pfix; p];                                         % Prepend fix points
    public static int addFixPoints(List<Pnt> p, List<Pnt> pfix) {
*/
    @Test
    public void testAddFixPoints() throws Exception {
        final double h0 = 1e-6;
        List<Pnt> pold = Arrays.asList(new Pnt(0, 0), new Pnt(0.5, 0.5), new Pnt(0, 1));

        List<Pnt> p;
        List<Pnt> pfix;
        int nfix;

        p = new ArrayList<Pnt>(pold);
        // no fix points
        nfix = DistMesh2D.addFixPoints(p, null, h0);
        Assert.assertEquals(pold, p);
        Assert.assertEquals(0, nfix);

        p = new ArrayList<Pnt>(pold);
        pfix = Arrays.asList(new Pnt(0, 1), new Pnt(0, 1));
        // two same fix points
        nfix = DistMesh2D.addFixPoints(p, pfix, h0);
        List<Pnt> pnew = Arrays.asList(new Pnt(0, 1), new Pnt(0, 0), new Pnt(0.5, 0.5));
        Assert.assertEquals(pnew, p);
        Assert.assertEquals(1, nfix);

        p = new ArrayList<Pnt>(pold);
        pfix = Arrays.asList(new Pnt(2, 0));
        // new fix point
        nfix = DistMesh2D.addFixPoints(p, pfix, h0);
        pnew = new ArrayList<Pnt>(pfix);
        pnew.addAll(pold);
        Assert.assertEquals(pnew, p);
        Assert.assertEquals(1, nfix);
    }

/*

//        r0=1./feval(fh,p,varargin{:}).^2;                    % Probability to keep point
//        p=p(rand(size(p,1),1)<r0./max(r0),:);                % Rejection method
    public static void rejectionMethod(List<Pnt> p, IEdgeLengthFunction fh) {

*/
    @Test
    public void testRejectionMethod() throws Exception {
        List<Pnt> pold = Arrays.asList(new Pnt(0, 0), new Pnt(0.5, 0.5), new Pnt(0, 1));

        List<Pnt> p= new ArrayList<Pnt>(pold);

        DistMesh2D.rejectionMethod(p, new HUniform());

        Assert.assertEquals("HUniform keeps all points.", pold, p);
    }

/*
//        p=p(feval(fd,p,varargin{:})<geps,:);                 % Keep only d<0 points
    public static void removeOutsidePoints(List<Pnt> p, final IDistanceFunction fd, final double geps) {
*/
    @Test
    public void testRemoveOutsidePoints() throws Exception {
        DCircle fd = new DCircle(0, 0, 1);

        List<Pnt> insidePoints = Arrays.asList(new Pnt(0, 0), new Pnt(0.5, 0.5), new Pnt(0, 1));
        List<Pnt> outsidePoints = Arrays.asList(new Pnt(1, 1), new Pnt(0.5, 1), new Pnt(2, 1));

        List<Pnt> p= new ArrayList<Pnt>(insidePoints);
        p.addAll(outsidePoints);

        DistMesh2D.removeOutsidePoints(p, fd, Matlab.EPS);
        Assert.assertEquals(insidePoints, p);
    }

/*
        [x,y]=meshgrid(bbox(1,1):h0:bbox(2,1),bbox(1,2):h0*sqrt(3)/2:bbox(2,2));
        x(2:2:end,:)=x(2:2:end,:)+h0/2;                      % Shift even rows
        p=[x(:),y(:)];                                       % List of node coordinates
//    public static List<Pnt> initEquiTriangles(double h0, double[][] bbox) {
*/
    @Test
    public void testInitEquiTriangles() throws Exception {
        List<Pnt> p = DistMesh2D.initEquiTriangles(1, new double[][]{{0, 0}, {3, 5}});
        Assert.assertEquals("Size by Matlab", 24, p.size());
        // points
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0, 0}, p.get(0).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0.5, 0.8660}, p.get(1).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0, 1.7321}, p.get(2).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0.5, 2.5981}, p.get(3).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0, 3.4641}, p.get(4).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0.5, 4.3301}, p.get(5).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {1, 0}, p.get(6).getData(), 0.0001);
        // last
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[]{3.5, 4.3301}, p.get(23).getData(), 0.0001);

        p = DistMesh2D.initEquiTriangles(1, new double[][]{{0, 0}, {3, 4}});
        Assert.assertEquals("Size by Matlab", 20, p.size());
        // points
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0, 0}, p.get(0).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0.5, 0.8660}, p.get(1).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0, 1.7321}, p.get(2).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0.5, 2.5981}, p.get(3).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {0, 3.4641}, p.get(4).getData(), 0.0001);
        org.junit.Assert.assertArrayEquals("Content by Matlab", new double[] {1, 0}, p.get(5).getData(), 0.0001);
        // last
    }

/*
// any(L0>2*L)
    private boolean anyTooClose(List<Bar> bars) {
*/
    @Test
    public void testAnyTooClose() throws Exception {
        List<DistMeshBar> bars = new ArrayList<DistMeshBar>();
        DistMeshBar bar;
        bar = new DistMeshBar(0, 1);
        bar.setL0(3);
        bar.setL(1);
        bars.add(bar);

        Assert.assertTrue("One short bar.", DistMesh2D.anyTooClose(bars));

        // 3 bars, first is short
        bar = new DistMeshBar(0, 2);
        bar.setL0(3);
        bar.setL(2);
        bars.add(bar);
        bar = new DistMeshBar(1, 2);
        bar.setL0(3);
        bar.setL(2);
        bars.add(bar);

        Assert.assertTrue("One short bar.", DistMesh2D.anyTooClose(bars));
        bars.remove(0);
        Assert.assertFalse("No short bar.", DistMesh2D.anyTooClose(bars));
    }

/*
//              p(setdiff(reshape(bars(L0>2*L,:),[],1),1:nfix),:)=[];
    private void removeTooClosePoints(List<Pnt> p, List<Bar> bars, int nfix) {
*/
    @Test
    public void testRemoveTooClosePoints() throws Exception {
        List<DistMeshBar> bars = new ArrayList<DistMeshBar>();
        DistMeshBar bar;
        bar = new DistMeshBar(0, 1);
        bar.setL0(3);
        bar.setL(1);
        bars.add(bar);

        List<Pnt> p;
        p = anyPoints(2);
        DistMesh2D.removeTooClosePoints(p, bars, 0);
        Assert.assertTrue("Any fix point.", p.isEmpty());

        p = anyPoints(2);
        DistMesh2D.removeTooClosePoints(p, bars, 1);
        Assert.assertEquals("One fix point.", 1, p.size());

        p = anyPoints(2);
        DistMesh2D.removeTooClosePoints(p, bars, 2);
        Assert.assertEquals("All fix point.", 2, p.size());

        // 3 bars, first is short
        bar = new DistMeshBar(0, 2);
        bar.setL0(3);
        bar.setL(2);
        bars.add(bar);
        bar = new DistMeshBar(1, 2);
        bar.setL0(3);
        bar.setL(2);
        bars.add(bar);

        p = anyPoints(3);
        DistMesh2D.removeTooClosePoints(p, bars, 0);
        Assert.assertEquals("Any fix point.", 1, p.size());

        p = anyPoints(3);
        DistMesh2D.removeTooClosePoints(p, bars, 1);
        Assert.assertEquals("One fix point.", 2, p.size());

        p = anyPoints(3);
        DistMesh2D.removeTooClosePoints(p, bars, 3);
        Assert.assertEquals("All fix point.", 3, p.size());
    }

    private List<Pnt> anyPoints(int n) {
        ArrayList<Pnt> p = new ArrayList<Pnt>(n);
        for (int i = 0; i < n; i++) {
            p.add(new Pnt(i, i % 2));
        }
        return p;
    }

}
