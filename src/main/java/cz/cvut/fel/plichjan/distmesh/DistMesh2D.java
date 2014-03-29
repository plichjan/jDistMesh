package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.IViewer;
import cz.cvut.fel.plichjan.distmesh.inputs.*;
import cz.cvut.fel.plichjan.distmesh.matlab.DistMeshBar;
import cz.cvut.fel.plichjan.distmesh.matlab.ITest;
import cz.cvut.fel.plichjan.distmesh.matlab.Matlab;
import cz.cvut.fel.plichjan.distmesh.result.FixedMesh;
import cz.cvut.fel.plichjan.distmesh.result.Mesh;
import delaunay.Pnt;
import org.apache.log4j.Logger;

import java.awt.geom.Point2D;
import java.io.PrintStream;
import java.util.*;

/**
 //        function [p,t]=distmesh2d(fd,fh,h0,bbox,pfix,varargin)
 //        %DISTMESH2D 2-D Mesh Generator using Distance Functions.
 //        %   [P,T]=DISTMESH2D(FD,FH,H0,BBOX,PFIX,FPARAMS)
 //        %
 //        %      P:         Node positions (Nx2)
 //        %      T:         Triangle indices (NTx3)
 //        %      FD:        Distance function d(x,y)
 //        %      FH:        Scaled edge length function h(x,y)
 //        %      H0:        Initial edge length
 //        %      BBOX:      Bounding box [xmin,ymin; xmax,ymax]
 //        %      PFIX:      Fixed node positions (NFIXx2)
 //        %      FPARAMS:   Additional parameters passed to FD and FH
 //        %
 */
public class DistMesh2D {
    public static final Logger logger = Logger.getLogger(DistMesh2D.class);

    private IViewer viewer;
    private double dptol = .001;

    public void setDptol(double dptol) {
        this.dptol = dptol;
    }

    public double getDptol() {
        return dptol;
    }

    public void setViewer(IViewer viewer) {
        this.viewer = viewer;
    }

    public Mesh call(final IDistanceFunction fd, final IEdgeLengthFunction fh, final double h0, final double[][] bbox, double[][] pfix) {
        return call(fd, fh, h0, bbox, Matlab.asPntList(pfix));
    }

    public Mesh call(final IDistanceFunction fd, final IEdgeLengthFunction fh, final double h0, final double[][] bbox, List<Pnt> pfix) {
//        dptol=.001; ttol=.1; Fscale=1.2; deltat=.2; geps=.001*h0; deps=sqrt(eps)*h0;
        final double ttol=.1;
        final double Fscale=1.2;
        final double deltat=.2;
        final double geps=.001*h0;
        final double deps=Math.sqrt(Matlab.EPS)*h0;

//        densityctrlfreq=30;
        final int densityctrlfreq=30;
//
//        % 1. Create initial distribution in bounding box (equilateral triangles)
        final List<Pnt> p = initEquiTriangles(h0, bbox);
//
//        % 2. Remove points outside the region, apply the rejection method
        removeOutsidePoints(p, fd, geps);
        rejectionMethod(p, fh);
        final int nfix = addFixPoints(p, pfix, geps);
//        N=size(p,1);                                         % Number of points N
        int N = p.size();

//
//        count=0;
        int count = 0;
//        pold=inf;                                            % For first iteration
        List<Pnt> pold = inf(p);

        final List<DistMeshBar> bars = new ArrayList<DistMeshBar>();
        List<int[]> t = new ArrayList<int[]>();

//        clf,view(2),axis equal,axis off
//        while 1
        outer: while (true) {
//          count=count+1;
            count++;

//          % 3. Retriangulation by the Delaunay algorithm
//          if max(sqrt(sum((p-pold).^2,2))/h0)>ttol           % Any large movement?

            if (anyLargeMovement(p, pold, h0, ttol)) {
//            pold=p;                                          % Save current positions
                pold = new ArrayList<Pnt>(p);
                t = Matlab.delaunayn2(p, bbox, fd, geps / 100);                //% List of triangles
                keepInteriorTriangles(p, t, fd, geps);

//            % 4. Describe each bar by a unique pair of nodes
                resetBars(bars, t);

//            % 5. Graphical output of the current mesh
//            cla,patch('vertices',p,'faces',t,'edgecol','k','facecol',[.8,.9,1]);
//            drawnow
                drawnow(p, t, bbox);
            }
//          end
//
//          % 6. Move mesh points based on bar lengths L and forces F
            setupLengths(p, bars, fh, Fscale);

//
//          % Density control - remove points that are too close
//          if mod(count,densityctrlfreq)==0 & any(L0>2*L)
            if (count % densityctrlfreq == 0 && anyTooClose(bars)) {
//              p(setdiff(reshape(bars(L0>2*L,:),[],1),1:nfix),:)=[];
                removeTooClosePoints(p, bars, nfix);

//              N=size(p,1); pold=inf;
                N = p.size();
                pold = inf(p);
//              continue;
                continue;
            }
//          end
//
            setupForces(bars);
            List<Pnt> Ftot = createFtot(bars, N, nfix);

//          p=p+deltat*Ftot;                                   % Update node positions
            for (int i = 0; i < p.size(); i++) {
                p.set(i, p.get(i).add(deltat, Ftot.get(i)));
            }
//
//          % 7. Bring outside points back to the boundary
//          d=feval(fd,p,varargin{:}); ix=d>0;                 % Find points outside (d>0)
//          dgradx=(feval(fd,[p(ix,1)+deps,p(ix,2)],varargin{:})-d(ix))/deps; % Numerical gradient
//          dgrady=(feval(fd,[p(ix,1),p(ix,2)+deps],varargin{:})-d(ix))/deps; %
//          dgrad2=dgradx.^2+dgrady.^2;
//          p(ix,:)=p(ix,:)-[d(ix).*dgradx./dgrad2,d(ix).*dgrady./dgrad2];    % Project
            List<Double> dList = new ArrayList<Double>(p.size());
            for (int i = 0; i < p.size(); i++) {
                Pnt a = p.get(i);
                double d = fd.call(a.get(0), a.get(1));
                dList.add(d);
                if (d > 0 && i >= nfix) {
                    double dgradx = (fd.call(a.get(0) + deps, a.get(1)) - d ) / deps;
                    double dgrady = (fd.call(a.get(0), a.get(1) + deps) - d ) / deps;
                    double dgrad2=dgradx * dgradx + dgrady * dgrady;
                    p.set(i, a.add(-1, new Pnt(d * dgradx / dgrad2, d * dgrady / dgrad2)));
                }
            }
            if (isSomeEdgeCross(p, bars)) {
                pold = inf(p);
            }

            drawnow(p, t, bbox);
//
//          % 8. Termination criterion: All interior nodes move less than dptol (scaled)
//          if max(sqrt(sum(deltat*Ftot(d<-geps,:).^2,2))/h0)<dptol, break; end
            for (int i = nfix; i < p.size(); i++) {
                double d = dList.get(i);
                if (d < -geps && deltat * Ftot.get(i).magnitude() / h0 > dptol) {
                    continue outer;
                }
            }
            break;
        }
//        end

        t = Matlab.delaunayn2(p, bbox, fd, geps / 100);                //% List of triangles
        keepInteriorTriangles(p, t, fd, geps);

//
//        % Clean up and plot final mesh
//        [p,t]=fixmesh(p,t);
//        simpplot(p,t)
        final FixedMesh mesh = new FixMesh().call(p, t);
        viewer.drawMesh(mesh);
        return mesh;
    }

    public static boolean isSomeEdgeCross(List<Pnt> p, List<DistMeshBar> bars) {
        for (int i = 0; i < bars.size(); i++) {
            DistMeshBar bar0 = bars.get(i);
            Pnt r = p.get(bar0.getB()).subtract(p.get(bar0.getA()));
            for (int j = i + 1; j < bars.size(); j++) {
                DistMeshBar bar = bars.get(j);
                if (bar.getA() == bar0.getA() || bar.getA() == bar0.getB() || bar.getB() == bar0.getA() || bar.getB() == bar0.getB()) {
                    continue;
                }
                Pnt s = p.get(bar.getB()).subtract(p.get(bar.getA()));
                Pnt qp = p.get(bar.getA()).subtract(p.get(bar0.getA()));
                final double rxs = Pnt.determinant(new Pnt[]{r, s});
                if (rxs > Matlab.EPS) {
                    double t = Pnt.determinant(new Pnt[]{qp, s}) / rxs;
                    double u = Pnt.determinant(new Pnt[]{qp, r}) / rxs;
                    if (t >= 0 && t <= 1 && u >= 0 && u <= 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double minPointsDistance(List<Pnt> p) {
        double d = Double.POSITIVE_INFINITY;
        for (int i = 0; i < p.size(); i++) {
            final Pnt a = p.get(i);
            for (int j = 0; j < i; j++) {
                final double distance = a.subtract(p.get(j)).magnitude();
                if (distance < d) {
                    d = distance;
                }
            }
        }
        return d;
    }

    public static void removeTooClosePoints2(List<Pnt> p, int nfix, double eps) {
        for (int i = p.size() - 1; i >= nfix; i--) {
            final Pnt a = p.get(i);
            for (int j = 0; j < i; j++) {
                if (a.subtract(p.get(j)).magnitude() < eps) {
                    p.remove(i);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    break;
                }
            }
        }
    }

    private void drawnow(List<Pnt> p, List<int[]> t, double[][] bbox) {
        if (viewer != null) {
            List<Point2D> pdt = new ArrayList<Point2D>(p.size());
            for (Pnt point : p) {
                pdt.add(new Point2D.Double(point.get(0), point.get(1)));
            }
            viewer.setNewPoints(pdt, t, bbox);
/*
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
*/
        }
    }

    public static List<Pnt> createFtot(List<DistMeshBar> bars, int n, int nfix) {
//          Ftot=full(sparse(bars(:,[1,1,2,2]),ones(size(F))*[1,2,1,2],[Fvec,-Fvec],N,2));
        List<Pnt> Ftot = new ArrayList<Pnt>(n);
        for (int i = 0; i < n; i++) {
            Ftot.add(new Pnt(0, 0));
        }
        for (DistMeshBar bar : bars) {
            Ftot.set(bar.getA(), Ftot.get(bar.getA()).add(bar.getFvec()));
            Ftot.set(bar.getB(), Ftot.get(bar.getB()).add(-1, bar.getFvec()));
        }
//          Ftot(1:size(pfix,1),:)=0;                          % Force = 0 at fixed points
        for (int i = 0; i < nfix; i++) {
            Ftot.set(i, new Pnt(0, 0));
        }
        return Ftot;
    }

    public static void setupForces(List<DistMeshBar> bars) {
//          F=max(L0-L,0);                                     % Bar forces (scalars)
//          Fvec=F./L*[1,1].*barvec;                           % Bar forces (x,y components)
        for (DistMeshBar bar : bars) {
            bar.setF(Math.max(bar.getL0() - bar.getL(), 0));
//            bar.setF(bar.getL0() - bar.getL());
            double fl = bar.getF() / bar.getL();
            bar.setFvec(bar.getBarvec().scale(fl));
        }
    }

    public static void setupLengths(List<Pnt> p, List<DistMeshBar> bars, IEdgeLengthFunction fh, double fscale) {
//          barvec=p(bars(:,1),:)-p(bars(:,2),:);              % List of bar vectors
//          L=sqrt(sum(barvec.^2,2));                          % L = Bar lengths
//          hbars=feval(fh,(p(bars(:,1),:)+p(bars(:,2),:))/2,varargin{:});
        double sumL2 = 0;
        double sumHbars2 = 0;
        for (DistMeshBar bar : bars) {
            Pnt pA = p.get(bar.getA());
            Pnt pB = p.get(bar.getB());

            Pnt barvec = pA.add(-1, pB);
            bar.setBarvec(barvec);

            double l = barvec.magnitude();
            bar.setL(l);
            sumL2 += l * l;

            // midpoint
            Pnt midp = pA.add(pB).scale(0.5);
            double hbar = fh.call(midp.get(0), midp.get(1));
            sumHbars2 += hbar * hbar;
            bar.setHbar(hbar);
        }
//          L0=hbars*Fscale*sqrt(sum(L.^2)/sum(hbars.^2));     % L0 = Desired lengths
        for (DistMeshBar bar : bars) {
            bar.setL0(bar.getHbar() * fscale * Math.sqrt(sumL2 / sumHbars2));
        }
    }

    public static void resetBars(List<DistMeshBar> bars, List<int[]> t) {
//            bars=[t(:,[1,2]);t(:,[1,3]);t(:,[2,3])];         % Interior bars duplicated
        Set<DistMeshBar> barsSet = new TreeSet<DistMeshBar>();
        for (int[] tr : t) {
            //sort(bars,2)
            barsSet.add(new DistMeshBar(tr[0], tr[1]));
            barsSet.add(new DistMeshBar(tr[0], tr[2]));
            barsSet.add(new DistMeshBar(tr[1], tr[2]));
        }
//            bars=unique(sort(bars,2),'rows');                % Bars as node pairs
        bars.clear();
        bars.addAll(barsSet);
    }

    public static void keepInteriorTriangles(List<Pnt> p, List<int[]> t, final IDistanceFunction fd, final double geps) {
//            pmid=(p(t(:,1),:)+p(t(:,2),:)+p(t(:,3),:))/3;    % Compute centroids
        final List<Pnt> pmid = Matlab.computeCentroids(p, t);
//            t=t(feval(fd,pmid,varargin{:})<-geps,:);         % Keep interior triangles
        Matlab.filterSelf(t, new ITest<int[]>() {
            @Override
            public boolean call(int i, int[] item) {
                return fd.call(pmid.get(i).get(0), pmid.get(i).get(1)) < -geps;
            }
        });
        if (logger.isDebugEnabled()) {
            logger.debug("keepInteriorTriangles done.");
        }
    }

    public static boolean anyLargeMovement(List<Pnt> p, List<Pnt> pold, double h0, double ttol) {
//          if max(sqrt(sum((p-pold).^2,2))/h0)>ttol           % Any large movement?
        if (p.size() < pold.size()) {
            return true;
        }

        for (int i = 0; i < p.size(); i++) {
            if (p.get(i).subtract(pold.get(i)).magnitude() / h0 > ttol) {
                return true;
            }
        }
        return false;
    }

    public static int addFixPoints(List<Pnt> p, List<Pnt> pfix, double h0) {
//        if ~isempty(pfix), p=setdiff(p,pfix,'rows'); end     % Remove duplicated nodes
        if (pfix != null) {
            p.removeAll(pfix);
/*
            for (Iterator<Pnt> iterator = p.iterator(); iterator.hasNext(); ) {
                Pnt point = iterator.next();
                for (Pnt pf : pfix) {
                    if (point.distance(pf) < h0) {
                        iterator.remove();
                        break;
                    }
                }
            }
*/
        }

//        pfix=unique(pfix,'rows'); nfix=size(pfix,1);
        Set<Pnt> pfixSet = pfix == null ? new LinkedHashSet<Pnt>() : new LinkedHashSet<Pnt>(pfix);
        int nfix = pfixSet.size();
//        p=[pfix; p];                                         % Prepend fix points
        p.addAll(0, pfixSet);
        if (logger.isDebugEnabled()) {
            logger.debug("addFixPoints done. " + p.size());
        }
        return nfix;
    }

    public static void rejectionMethod(List<Pnt> p, IEdgeLengthFunction fh) {
//        r0=1./feval(fh,p,varargin{:}).^2;                    % Probability to keep point
        final List<Double> r0 = new ArrayList<Double>(p.size());
        for (Pnt item : p) {
            double v = fh.call(item.get(0), item.get(1));
            r0.add(1. / (v * v));
        }

//        p=p(rand(size(p,1),1)<r0./max(r0),:);                % Rejection method
        final double maxR0 = r0.isEmpty() ? 0 : Collections.max(r0);
        Matlab.filterSelf(p, new ITest<Pnt>() {
            @Override
            public boolean call(int i, Pnt item) {
                return Math.random() < r0.get(i) / maxR0;
            }
        });
        if (logger.isDebugEnabled()) {
            logger.debug("rejectionMethod done. " + p.size());
        }
    }

    public static void removeOutsidePoints(List<Pnt> p, final IDistanceFunction fd, final double geps) {
//        p=p(feval(fd,p,varargin{:})<geps,:);                 % Keep only d<0 points
        Matlab.filterSelf(p, new ITest<Pnt>() {
            @Override
            public boolean call(int i, Pnt item) {
                return fd.call(item.get(0), item.get(1)) < geps;
            }
        });
        if (logger.isDebugEnabled()) {
            logger.debug("removeOutsidePoints done. " + p.size());
        }
    }

    //        % 1. Create initial distribution in bounding box (equilateral triangles)
    public static List<Pnt> initEquiTriangles(double h0, double[][] bbox) {
//        [x,y]=meshgrid(bbox(1,1):h0:bbox(2,1),bbox(1,2):h0*sqrt(3)/2:bbox(2,2));
//        x(2:2:end,:)=x(2:2:end,:)+h0/2;                      % Shift even rows
//        p=[x(:),y(:)];                                       % List of node coordinates

        double[] x = Matlab.vector(bbox[0][0], h0, bbox[1][0]);
        double[] y = Matlab.vector(bbox[0][1], h0 * Math.sqrt(3.) / 2., bbox[1][1]);

        List<Pnt> p = new ArrayList<Pnt>(x.length * y.length);
        for (double aX : x) {
            for (int j = 0; j < y.length; j++) {
                if (j % 2 == 0) {
                    p.add(new Pnt(aX, y[j]));
                } else {
                    p.add(new Pnt(aX + h0 / 2, y[j]));
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("initEquiTriangles done. " + p.size());
        }
        return p;
    }

    public static boolean anyTooClose(List<DistMeshBar> bars) {
        // any(L0>2*L)
        for (DistMeshBar bar : bars) {
            if (bar.getL0() > 2 * bar.getL()) {
                return true;
            }
        }
        return false;
    }

    public static void removeTooClosePoints(List<Pnt> p, List<DistMeshBar> bars, int nfix) {
//              p(setdiff(reshape(bars(L0>2*L,:),[],1),1:nfix),:)=[];
        Set<Integer> indices = new TreeSet<Integer>(Collections.reverseOrder());
        for (DistMeshBar bar : bars) {
            if (bar.getL0() > 2 * bar.getL()) {
                if (bar.getA() >= nfix) {
                    indices.add(bar.getA());
                }
                if (bar.getB() >= nfix) {
                    indices.add(bar.getB());
                }
            }
        }
        for (int i : indices) {
            p.remove(i);
        }
    }

    public static List<Pnt> inf(List<Pnt> p) {
        return Collections.nCopies(p.size(), new Pnt(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    public static void printP(List<Pnt> pList, PrintStream ps) {
        PrintStream out = ps == null ? System.out : ps;
        out.println(pList.size());
        for (Pnt p : pList) {
            out.printf("   % 6.4g   % 6.4g 0.0\n", p.get(0), p.get(1));
        }
    }
}
