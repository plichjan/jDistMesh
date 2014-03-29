importPackage(Packages.cz.cvut.fel.plichjan.distmesh.inputs);
importPackage(Packages.cz.cvut.fel.plichjan.distmesh.matlab);
importPackage(Packages.cz.cvut.fel.plichjan.distmesh);
importClass(Packages.delaunay.Pnt);
importClass(java.util.ArrayList);

/*
//Square with Hole
var pfix = new ArrayList();
pfix.add(new Pnt([-1, -1]));
pfix.add(new Pnt([-1, 1]));
pfix.add(new Pnt([1, -1]));
pfix.add(new Pnt([1, 1]));

var dc = new DCircle(0, 0, 0.5);
var rc = new DRectangle(-1, 1, -1, 1);

var h0 = 0.05;

var mesh2 =
distMesh2D.call(
        new DDiff(rc, dc),
        function call(x, y) {
            return 0.05+0.3*dc.call(x,y);
        },
        h0, [[-1, -1], [1, 1]],
        pfix
);

var lbCorner = new DCircle(-1, -1, 0);

mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);

var nodePrinter = new NodePrinter(dir + "test.node");
nodePrinter.printHeader(mesh2.p.length, 2, 7);
for (var i = 0; i < mesh2.p.length; i++) {
    var node = mesh2.p[i];
    var flags = 0;
    var potential = 0., displacement = zero, bForce = zero, vForce = zero;

    if (Math.abs(callDf(dc, node)) < h0) {
        flags |= IS_SET_POTENTIAL;
        potential = 10.;
    }
    if (Math.abs(callDf(rc, node)) < h0) {
        flags |= IS_SET_POTENTIAL;
        potential = -10.;
    }
    if (Math.abs(node[0] - -1) < h0) {
        flags |= IS_SET_DISPLACEMENT;
        displacement = [0., 0./0.];
    }
    if (Math.abs(callDf(lbCorner, node)) < h0) {
        flags |= IS_SET_DISPLACEMENT;
        displacement = [0., 0.];
    }
    if (Math.abs(node[1] - 1) < h0) {
        flags |= IS_SET_BOUNDARY_FORCE;
        bForce = [0., 200e7];
    }

    nodePrinter.printNode(i+1, node, [
        potential,
        displacement[0], displacement[1],
        bForce[0], bForce[1],
        vForce[0], vForce[1]
    ], flags);
}
nodePrinter.close();

var elePrinter = new ElePrinter(dir + "test.ele");
elePrinter.printHeader(mesh2.t.length, 6);
for (var i = 0; i < mesh2.t.length; i++) {
    var tr = mesh2.t[i];
    elePrinter.printEle(i+1, [tr[0]+1, tr[1]+1, tr[2]+1, tr[4]+1, tr[5]+1, tr[3]+1], 20);
}
elePrinter.close();
*/

/*
//Square with Hole
var pfix = new ArrayList();
pfix.add(new Pnt([-1, -1]));
pfix.add(new Pnt([-1, 1]));
pfix.add(new Pnt([1, -1]));
pfix.add(new Pnt([1, 1]));

var dc = new DCircle(0, 0, 0.5);

distMesh2D.call(
        new DDiff(new DRectangle(-1,1,-1,1), dc),
        function call(x, y) {
            return 0.05+0.3*dc.call(x,y);
        },
        0.05, [[-1, -1], [1, 1]],
        pfix
);
*/
/*
//uniform circle
distMesh2D.call(
        new DCircle(0, 0, 1),
        new HUniform(),
        0.2, [[-1, -1], [1, 1]],
        new ArrayList()
);
*/
/*
//nonuniform square
var pfix = new ArrayList();
pfix.add(new Pnt([-1, -1]));
pfix.add(new Pnt([-1, 1]));
pfix.add(new Pnt([1, -1]));
pfix.add(new Pnt([1, 1]));

var dh = [new DCircle(-1, -1, 0), new DCircle(-1, 1, 0), new DCircle(1, -1, 0), new DCircle(1, 1, 0)];
distMesh2D.call(
        new DRectangle(-1, 1, -1, 1),
        function call(x, y) {
            return Math.min(
                    0.02 + 0.3 * dh[0].call(x, y),
                    0.02 + 0.3 * dh[1].call(x, y),
                    0.02 + 0.3 * dh[2].call(x, y),
                    0.02 + 0.3 * dh[3].call(x, y)
            );
        },
        0.02, [[-1, -1], [1, 1]],
        pfix
);
*/
/*
//pdf: Fig. 4 simple hexagon
var pfix = new ArrayList(), p1 = new ArrayList();
var i, n = 6, r1 = 1.;
for (i = 0; i < n; i++) {
    var phi, x, y;
    phi = (i / 6. + 1/24.) * 2 * Math.PI;

    // outer
    x = Math.cos(phi);
    y = Math.sin(phi);
    pfix.add(new Pnt([r1*x, r1*y]));
    p1.add(new Pnt([r1*x, r1*y]));
}

distMesh2D.call(
        new DPoly(p1),
        new HUniform(),
        0.1, [[-1, -1], [1, 1]],
        pfix
);
*/
/*
//pdf: Fig. 4
var pfix = new ArrayList(), p1 = new ArrayList(), p2 = new ArrayList();
var i, n = 6, r1 = 1., r2 = 0.5;
for (i = 0; i < n; i++) {
    var phi, x, y;
    phi = i / 6. * 2 * Math.PI;

    // outer
    x = Math.cos(phi);
    y = Math.sin(phi);
    pfix.add(new Pnt([r1*x, r1*y]));
    p1.add(new Pnt([r1*x, r1*y]));

    // inner
    phi += 1 / 12. * 2 * Math.PI;
    x = Math.cos(phi);
    y = Math.sin(phi);
    pfix.add(new Pnt([r2*x, r2*y]));
    p2.add(new Pnt([r2*x, r2*y]));
}

distMesh2D.call(
        new DDiff(new DPoly(p1), new DPoly(p2)),
        new HUniform(),
        0.1, [[-1, -1], [1, 1]],
        pfix
);
*/
/*
//star
var pfix = new ArrayList(), p1 = new ArrayList();
var i, n = 6, r1 = 1., r2 = 0.5;
for (i = 0; i < n; i++) {
    var phi, x, y;
    phi = i / 6. * 2 * Math.PI;

    // outer
    x = Math.cos(phi);
    y = Math.sin(phi);
    pfix.add(new Pnt([r1*x, r1*y]));
    p1.add(new Pnt([r1*x, r1*y]));

    // inner
    phi += 1 / 12. * 2 * Math.PI;
    x = Math.cos(phi);
    y = Math.sin(phi);
    pfix.add(new Pnt([r2*x, r2*y]));
    p1.add(new Pnt([r2*x, r2*y]));
}
var c1 = new DCircle(0,0,0);
distMesh2D.call(
        new DPoly(p1),
        new HUniform(),
        0.2, [[-1, -1], [1, 1]],
        pfix
);
*/
/*
//pdf: Fig. 6
var d1 = new DCircle(0, 0, 1);
var d2 = new DCircle(-0.4, 0, 0.55);

var pfix = new ArrayList();
pfix.add(new Pnt([-1,  0]));
pfix.add(new Pnt([1, 0]));
pfix.add(new Pnt([-0.4 - 0.55,  0]));
pfix.add(new Pnt([-0.4 + 0.55,  0]));

distMesh2D.call(
        function call(x, y) {
            return Math.max(d1.call(x, y), -d2.call(x, y), -y);
        },
        function call(x, y) {
            return Math.min(0.15 - 0.2 * d1.call(x, y), 0.06 + 0.2 * d2.call(x, y), (d2.call(x, y) - d1.call(x, y))/3.);
        },
        0.05/3, [[-1, 0], [1, 1]],
        pfix
);
*/
/*
// cocka s dirou

var d2 = new DCircle(0, 0, 0.5);
var d3 = new DCircle(-0.1, 0.15, 0.05);
var dh = new DCircle(-0.125, Math.sqrt(15) / 8., 0);
var dh2 = new DCircle(-0.125, -Math.sqrt(15) / 8., 0);
var pfix = new ArrayList();
pfix.add(new Pnt([-0.125,  Math.sqrt(15) / 8.]));
pfix.add(new Pnt([-0.125, -Math.sqrt(15) / 8.]));
distMesh2D.call(
        new DDiff(new DIntersect(new DCircle(-1, 0, 1), d2), d3),
        function call(x, y) {
            return Math.min(0.01 + 0.3 * dh.call(x, y), 0.01 + 0.3 * dh2.call(x, y), 0.01 + 0.3 * d3.call(x, y));
        },
        0.01, [[-0.5, -0.5], [0, 0.5]],
        pfix
);
*/
/*
//%   Example: (NACA0012 airfoil)
//%      hlead=0.01; htrail=0.04; hmax=2; circx=2; circr=4;
//%      a=.12/.2*[0.2969,-0.1260,-0.3516,0.2843,-0.1036];
//%
//%      fd=@(p) ddiff(dcircle(p,circx,0,circr),(abs(p(:,2))-polyval([a(5:-1:2),0],p(:,1))).^2-a(1)^2*p(:,1));
//%      fh=@(p) min(min(hlead+0.3*dcircle(p,0,0,0),htrail+0.3*dcircle(p,1,0,0)),hmax);
//%
//%      fixx=1-htrail*cumsum(1.3.^(0:4)');
//%      fixy=a(1)*sqrt(fixx)+polyval([a(5:-1:2),0],fixx);
//%      fix=[[circx+[-1,1,0,0]*circr; 0,0,circr*[-1,1]]'; 0,0; 1,0; fixx,fixy; fixx,-fixy];
//%      box=[circx-circr,-circr; circx+circr,circr];
//%      h0=min([hlead,htrail,hmax]);
//%
//%      [p,t]=distmesh2d(fd,fh,h0,box,fix);
var hlead=0.01; htrail=0.04; hmax=2; circx=2; circr=4;
var a=[0.2969,-0.1260,-0.3516,0.2843,-0.1036];
var i;
for (i = 0; i < a.length; i++) {
    a[i] *= .12/.2;
}

function polyval(arr, x) {
    var i, val = arr[0];
    for (i = 1; i < arr.length; i++) {
        val *= x;
        val += arr[i];
    }
    return val;
}
function sqr(x) { return x*x; }

// (abs(p(:,2))-polyval([a(5:-1:2),0],p(:,1))).^2-a(1)^2*p(:,1)
function airfoil(x, y) {
    return sqr(Math.abs(y)-polyval([a[4],a[3],a[2],a[1],0],x)) - sqr(a[0])*x;
}

//%      fh=@(p) min(min(hlead+0.3*dcircle(p,0,0,0),htrail+0.3*dcircle(p,1,0,0)),hmax);
var c1 = new DCircle(0,0,0), c2 = new DCircle(1,0,0);
function fh(x, y){
    return Math.min(hlead+0.3*c1.call(x, y), htrail+0.3*c2.call(x, y), hmax);
}

var cumsum = 0; fix = new ArrayList();
//%      fix=[[circx+[-1,1,0,0]*circr; 0,0,circr*[-1,1]]'; 0,0; 1,0; fixx,fixy; fixx,-fixy];
fix.add(new Pnt([circx - circr, 0]));
fix.add(new Pnt([circx + circr, 0]));
fix.add(new Pnt([circx, - circr]));
fix.add(new Pnt([circx,   circr]));
fix.add(new Pnt([0, 0]));
fix.add(new Pnt([1, 0]));

for(i = 0; i <= 4; i++) {
//%      fixx=1-htrail*cumsum(1.3.^(0:4)');
//%      fixy=a(1)*sqrt(fixx)+polyval([a(5:-1:2),0],fixx);
    cumsum += Math.pow(1.3, i);
    var fixx = 1 - htrail * cumsum;
    var fixy = a[0]* Math.sqrt(fixx) + polyval([a[4],a[3],a[2],a[1],0], fixx);
    // fixx,fixy; fixx,-fixy
    fix.add(new Pnt([fixx, fixy]));
    fix.add(new Pnt([fixx, -fixy]));
}

var box = [[circx-circr,-circr], [circx+circr,circr]];
var h0 = Math.min(hlead, htrail, hmax);

distMesh2D.call(
        new DDiff(new DCircle(circx,0,circr), airfoil),
        fh,
        h0, box,
        fix
);
*/
/*
//   Example: (Square, with size function point and line sources)
//      fd=@(p) drectangle(p,0,1,0,1);
//      fh=@(p) min(min(0.01+0.3*abs(dcircle(p,0,0,0)), ...
//                   0.025+0.3*abs(dpoly(p,[0.3,0.7; 0.7,0.5]))),0.15);
//      [p,t]=distmesh2d(fd,fh,0.01,[0,0;1,1],[0,0;1,0;0,1;1,1]);

var pl = new ArrayList();
pl.add(new Pnt([0.3,0.7]));
pl.add(new Pnt([0.7,0.5]));
var d1 = new DCircle(0,0,0), d2 = new DPoly(pl);
function fh(x, y) {
    return Math.min(0.01+0.3*Math.abs(d1.call(x,y)), 0.025+0.3*Math.abs(d2.call(x,y)), 0.15);
}

var fix = new ArrayList();
fix.add(new Pnt([0, 0]));
fix.add(new Pnt([1, 0]));
fix.add(new Pnt([0, 1]));
fix.add(new Pnt([1, 1]));

distMesh2D.call(
        new DRectangle(0,1,0,1),
        fh,
        0.01, [[0,0],[1,1]],
        fix
);
*/
/*
// ellipse [a,b]
var a = 1, b = 0.25;
var h0 = 0.05;
function fd(x, y) { return Math.sqrt(x * x / a / a + y * y / b / b) - 1; }
function fh(x, y) { return h0 + 0.1 * -fd(x, y); }
distMesh2D.call(
        fd,
        fh,
        h0, [[-1, -1],[1,1]],
        []
);
*/
