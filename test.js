importPackage(Packages.cz.cvut.fel.plichjan.distmesh.inputs);
importPackage(Packages.cz.cvut.fel.plichjan.distmesh.matlab);
importPackage(Packages.cz.cvut.fel.plichjan.distmesh);
importClass(Packages.delaunay.Pnt);
importClass(java.util.ArrayList);

var dir = "D:\\_CVUT\\work\\DIP\\triangle\\";
var zero = [0., 0.];
var Constrain = {
    df: null, flags: null, potential: null, displacement: null, bForce: null, vForce: null
};

var IS_SET_DISPLACEMENT = 2;
var IS_SET_POTENTIAL = 4;
var IS_SET_FORCE = 8;
var IS_PRINTABLE = 16;
var IS_SET_BOUNDARY_FORCE = 32;
var WALL_NODE = 64;

function callDf(df, node) {
    return df.call(node[0], node[1]);
}

function Inside(df) {
    return {
        call: function (x, y) {
                return Math.max(0., df.call(x,y));
            }

    };
}

function forEvery () { return -1; }

function storeMesh(file, mesh2, h0, list, trList) {
    var nodePrinter = new NodePrinter(file + ".node");
    nodePrinter.printHeader(mesh2.p.length, 2, 7);
    for (var i = 0; i < mesh2.p.length; i++) {
        var node = mesh2.p[i];
        var flags = 0;
        var potential = 0., displacement = zero, bForce = zero, vForce = zero;

        for (var j = 0; j < list.length; j++) {
            var item = list[j];
            if (Math.abs(callDf(item.df, node)) < h0) {
                flags |= item.flags;
                potential = (item.potential || potential);
                displacement = (item.displacement || displacement);
                bForce = (item.bForce || bForce);
                vForce = (item.vForce || vForce);
            }
        }

        nodePrinter.printNode(i+1, node, [
            potential,
            displacement[0], displacement[1],
            bForce[0], bForce[1],
            vForce[0], vForce[1]
        ], flags);
    }
    nodePrinter.close();

    var centroid = Matlab.computeCentroids(mesh2.p, mesh2.t);
    var elePrinter = new ElePrinter(file + ".ele");
    elePrinter.printHeader(mesh2.t.length, 6);
    for (i = 0; i < mesh2.t.length; i++) {
        var tr = mesh2.t[i];
        var matId = 0;

        for (j = 0; j < trList.length; j++) {
            item = trList[j];
            if (callDf(item.df, centroid[i]) < 0) {
                matId = item.matId;
            }
        }
        elePrinter.printEle(i+1, [tr[0]+1, tr[1]+1, tr[2]+1, tr[4]+1, tr[5]+1, tr[3]+1], matId);
    }
    elePrinter.close();
}
///================ end of common functions and variables =======================

/*
//Beam plane stress - mesh from FreeFem++
var w = 10, h = 0.1;
var h0 = h / 2;

var mesh2 =
new FreeFemMeshReader(dir + "mesh_sample").getMesh();

var v0Poly = new DPoly([[0, h], [w, h]]);
var c0 = new DCircle(0, 0, 0);

mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
viewFrame.drawMesh(mesh2);
h0 /= 10.;

var file = dir + "mesh_sample";
storeMesh(file, mesh2, h0, [
    {df: new DPoly([[0, 0], [0, h]]), flags: IS_SET_DISPLACEMENT, displacement: [0., 0./0.]},
    {df: c0, flags: IS_SET_DISPLACEMENT, displacement: [0., 0.]},
    {df: v0Poly, flags: IS_SET_BOUNDARY_FORCE, bForce: [0., -16. / 12. * 1e-3 / 1e4]}
], [
    {df: forEvery, matId: 100}
]);

*/
/*
//Beam plane stress
var w = 10, h = 0.1;
var pfix = new ArrayList();
pfix.add(new Pnt([0, 0]));
pfix.add(new Pnt([w, 0]));
pfix.add(new Pnt([w, h]));
pfix.add(new Pnt([0, h]));

var rc = new DRectangle0(0, w, 0, h);

var h0 = h / 2;

var mesh2 =
distMesh2D.call(
        rc,
        new HUniform(),
        h0, [[0, 0], [w + h0, h + h0]],
        pfix
);

var v0Poly = new DPoly([[0, h], [w, h]]);
var c0 = new DCircle(0, 0, 0);

mesh2.p = new BndProj().call(mesh2.p, mesh2.t, rc);
mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
viewFrame.drawMesh(mesh2);
h0 /= 10.;

var file = dir + "test";
storeMesh(file, mesh2, h0, [
    {df: new DPoly([[0, 0], [0, h]]), flags: IS_SET_DISPLACEMENT, displacement: [0., 0./0.]},
    {df: c0, flags: IS_SET_DISPLACEMENT, displacement: [0., 0.]},
    {df: v0Poly, flags: IS_SET_BOUNDARY_FORCE, bForce: [0., 16. / 12. * 1e-3 / 1e4]}
], [
    {df: forEvery, matId: 100}
]);
*/
/*

var mesh = new MeshReader(dir + "problem").getMesh();
viewFrame.drawMesh(mesh);

*/
/*
//Rectangle with Hole in corner
var w = 90e-3, h = 120e-3, r = 30e-3;
var pfix = new ArrayList();
pfix.add(new Pnt([0, 0]));
pfix.add(new Pnt([w, 0]));
pfix.add(new Pnt([w, h]));
pfix.add(new Pnt([r, h]));
pfix.add(new Pnt([0, h - r]));

var dc = new DCircle(0, h, r);
var rc = new DRectangle0(0, w, 0, h);

var h0 = 3e-3;

var mesh2 =
distMesh2D.call(
        new DDiff(rc, dc),
        function call(x, y) {
            return h0+0.05*dc.call(x,y);
        },
        h0, [[0, 0], [w, h]],
        pfix
);

var lbCorner = new DCircle(0, 0, 0);
var v0Poly = new DPoly([[0, 0], [w, 0], [w, h], [w, 0]]);

mesh2.p = new BndProj().call(mesh2.p, mesh2.t, new DDiff(rc, dc));
mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
viewFrame.drawMesh(mesh2);
h0 /= 10.;

var file = dir + "test";
storeMesh(file, mesh2, h0, [
    {df: dc, flags: IS_SET_POTENTIAL, potential: 1.},
    {df: v0Poly, flags: IS_SET_POTENTIAL, potential: 0.},
    {df: new DPoly([[0, 0],[0, h]]), flags: IS_SET_DISPLACEMENT, displacement: [0., 0./0.]},
    {df: new DPoly([[0, h],[w, h]]), flags: IS_SET_DISPLACEMENT, displacement: [0./0., 0.]},
    {df: new DPoly([[0, 0],[w, 0]]), flags: IS_SET_BOUNDARY_FORCE, bForce: [0., -2e10]}
], [
    {df: forEvery, matId: 21}
]);
*/
/*
//two electrodes and space between
var h0 = 0.04;
var electrode = new DRectangle0(-0.8, 0, -1.2, -1);
var beam = new DRectangle0(-1, 0, -0.05, 0.05);
var lbCorner = new DCircle(-1, -0.05, 0);

var area1 = new DUnion(electrode, beam);
*/
/*
var mesh = distMesh2D.call(
        area1,
        new HUniform(),
        h0, [[-1, -1], [1, 1]],
        [[-1, -0.05], [-1, 0.05], [0, -0.05], [0, 0.05]]
);

var pfix2 = new ArrayList();
pfix2.addAll(Matlab.asPntList(mesh.p));
pfix2.addAll(Matlab.asPntList([[-1, -1], [1, -1], [-1, 1], [1, 1]]));

var mesh2 = distMesh2D.call(
        new DDiff(new DRectangle0(-1,1,-1,1), area1),
        function call(x, y) {
            return 0.05+0.3*area1.call(x,y);
        },
        h0, [[-1, -1], [1, 1]],
        pfix2
);

// concatenation arrays of triangle
mesh2.t = mesh.t.concat(mesh2.t);
mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
viewFrame.drawMesh(mesh2);
*//*


var mesh2 = new MeshReader(dir + "testBeam2").getMesh();
viewFrame.drawMesh(mesh2);

// for test
h0 /= 10.;
var v0Poly = new DPoly([[-1, -1], [1, -1], [1, 1], [-1, 1]]);

var nodePrinter = new NodePrinter(dir + "test.node");
nodePrinter.printHeader(mesh2.p.length, 2, 7);
for (var i = 0; i < mesh2.p.length; i++) {
    var node = mesh2.p[i];
    var flags = 0;
    var potential = 0., displacement = zero, bForce = zero, vForce = zero;

    if (Math.abs(callDf(beam, node)) < h0) {
        flags |= IS_SET_POTENTIAL;
        flags |= WALL_NODE;
        potential = 0.;
    }
    if (Math.abs(callDf(electrode, node)) < h0) {
        flags |= IS_SET_POTENTIAL;
        flags |= WALL_NODE;
        potential = 28.e2 * 1.;
    }
    if (Math.abs(node[1] - -1) < h0) {
        flags |= WALL_NODE;
        flags |= IS_SET_DISPLACEMENT;
        displacement = [0., 0.];
    }
    if (Math.abs(node[0] - -1) < h0 && Math.abs(callDf(beam, node)) < h0) {
        flags |= IS_SET_DISPLACEMENT;
        displacement = [0., 0./0.];
    }
    if (Math.abs(callDf(lbCorner, node)) < h0) {
        flags |= IS_SET_DISPLACEMENT;
        displacement = [0., 0.];
    }

    nodePrinter.printNode(i+1, node, [
        potential,
        displacement[0], displacement[1],
        bForce[0], bForce[1],
        vForce[0], vForce[1]
    ], flags);
}
nodePrinter.close();

var centroid = Matlab.computeCentroids(mesh2.p, mesh2.t);
var elePrinter = new ElePrinter(dir + "test.ele");
elePrinter.printHeader(mesh2.t.length, 6);
for (var i = 0; i < mesh2.t.length; i++) {
    var tr = mesh2.t[i];
    elePrinter.printEle(i+1, [tr[0]+1, tr[1]+1, tr[2]+1, tr[4]+1, tr[5]+1, tr[3]+1],
            callDf(area1, centroid[i]) < 0 ? 19 : 10);
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
var rc = new DRectangle0(-1, 1, -1, 1);

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
        new DDiff(new DRectangle0(-1,1,-1,1), dc),
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
        new DRectangle0(-1, 1, -1, 1),
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
//      fd=@(p) DRectangle0(p,0,1,0,1);
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
        new DRectangle0(0,1,0,1),
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
//// 01_temaDIP_cantilever.pdf Fig. 3 (a)
//var
//        L = 200e-6,
//        t = 3e-6,
//        g = 2e-6,
//        w = 50e-6,
//        alpha = 0.1,
//        V = 28;
//// w, t are width and thickness of the beam, alpha is ratio of electrode length (l_e) over beam length (L)
//var l_e = alpha * L;
//var width = 2*L;
//var height = 2*g + t;
//
////two electrodes and space between
//var h0 = Math.min(t, g) / 4.;
//var beam = new DRectangle0(0., L, g, g + t);
//var electrode = new DRectangle0(L - l_e, L, 0. - t, 0.);
//var pLeft = new DPoly([[0, g], [0, g + t]]);
//var pRight = new DPoly([[L, g], [L, g + t]]);
//var pBottom = new DPoly([[0, g], [L, g]]);
//var pTop = new DPoly([[0, g + t], [L, g + t]]);
//function fh(x, y) {
//    return Math.min(
//            h0+0.05*Math.abs(pLeft.call(x,y)),
//            2*h0+0.05*Math.abs(pRight.call(x,y)),
//            L/100+0.05*Math.abs(pBottom.call(x,y)),
//            L/100+0.05*Math.abs(pTop.call(x,y)),
//            4*h0+0.05*Math.abs(electrode.call(x,y)));
//}
//
//var area1 = new DUnion(electrode, beam);
///*
//distMesh2D.dptol = 0.01;
//var mesh = distMesh2D.call(
//        beam,
//        fh,
//        h0, [[0., 0.], [width, height]],
//        [*/
///*[0., g], [0., g + t], [L, g], [L, g + t]*//*
//]
//);
//
//var pfix2 = new ArrayList();
//pfix2.addAll(Matlab.asPntList(mesh.p));
////pfix2.addAll(Matlab.asPntList([[0, 0], [0, height], [width, 0], [width, height]]));
//
//var mesh2 = distMesh2D.call(
//        new DDiff(new DRectangle0(0, width, 0, height), area1),
//        fh,
//        h0, [[0, 0], [width, height]],
//        pfix2
//);
//
//// concatenation arrays of triangle
//mesh2.t = mesh.t.concat(mesh2.t);
//*/
///*
//var node = [
//    [0., 0.],
//    [L - 2*l_e, 0.],
//    [L - l_e, 0.],
//    [L, 0.],
//    [L+ 2*l_e, 0.],
//    [0., g],
//    [L - 2*l_e, g],
//    [L - l_e, g],
//    [L, g],
//    [L+ 2*l_e, g+t/2],
//    [0., g+t],
//    [L - 2*l_e, g+t],
//    [L - l_e, g+t],
//    [L, g+t],
//    [0., 2*g+t],
//    [L - 2*l_e, 2*g+t],
//    [L - l_e, 2*g+t],
//    [L, 2*g+t],
//    [L+ 2*l_e, 2*g+t]
//];
//var tr = [
//    [1, 2, 7],
//    [2, 3, 8],
//    [3, 4, 9],
//    [4, 5, 9],
//    [1, 7, 6],
//    [2, 8, 7],
//    [3, 9, 8],
//    [9, 5,10],
//    [ 6, 7,12],
//    [ 7, 8,13],
//    [ 8, 9,14],
//    [ 9,10,14],
//    [ 6,12,11],
//    [ 7,13,12],
//    [ 8,14,13],
//    [11,12,16],
//    [12,13,17],
//    [13,14,18],
//    [14,10,19],
//    [11,16,15],
//    [12,17,16],
//    [13,18,17],
//    [14,19,18]
//];
//for (var i = 0; i < tr.length; i++) {
//    tr[i][0]--;
//    tr[i][1]--;
//    tr[i][2]--;
//}
//var mesh2 =
//new AddMidpoints().call(node, tr);
//*/
//
//var mesh2 =
//new FreeFemMeshReader("c:\\Program Files\\FreeFem++-cs-14.3\\Contents\\Resources\\cantilever").getMesh();
//mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
//viewFrame.drawMesh(mesh2);
//
//// for test
//h0 /= 1000.;
//var lbCorner = new DCircle(0, g, 0);
//
//var file = dir + "cantilever";
//storeMesh(file, mesh2, h0, [
//    {df: beam, flags: IS_SET_POTENTIAL | WALL_NODE, potential: 0. },
//    {df: electrode, flags: IS_SET_POTENTIAL | WALL_NODE, potential: V },
//    {df: new DPoly([[0, 0], [width, 0]]), flags: IS_SET_DISPLACEMENT, displacement: [0., 0.]},
//    {df: new DPoly([[0, g], [0, g + t]]), flags: IS_SET_DISPLACEMENT, displacement: [0., 0./0]},
//    {df: lbCorner, flags: IS_SET_DISPLACEMENT, displacement: [0., 0.]}
//], [
//    {df: forEvery, matId: 10}, // air
//    {df: area1, matId: 19} // E = 57e9
//]);
//
var b = 5e-6;
var h = 5e-6;
var L = 10e-6;
var F = 1.2242E-07;

var h0 = b / 4;
var beam = new DRectangle0(0., b, 0, L);
var mesh2 = distMesh2D.call(
        beam,
        new HUniform(),
        h0, [[0., 0.], [2*b, 2*L]],
        [[b/2., 0.]]
);
mesh2.p = new BndProj().call(mesh2.p, mesh2.t, beam);
mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
viewFrame.drawMesh(mesh2);

// mechanic only
var file = dir + "testSH";
storeMesh(file, mesh2, h0 / 1000., [
    {df: new DPoly([[0, 0], [b, 0]]), flags: IS_SET_BOUNDARY_FORCE, bForce: [0., -F/b/h]},
    {df: new DPoly([[0, L], [b, L]]), flags: IS_SET_DISPLACEMENT, displacement: [0./0., 0.]},
    {df: new DCircle(0, L, 0), flags: IS_SET_DISPLACEMENT, displacement: [0., 0.]},
    {df: new DCircle(b/2., 0, 0), flags: IS_PRINTABLE}
], [
    {df: forEvery, matId: 10}, // air
    {df: beam, matId: 200} // E = 1e5
]);

/*
// electrostatic part
*/
//b*=10;
beam = new DRectangle0(0., b, 0, L);
// for electrostatic-mechanic
mesh2 = distMesh2D.call(
        beam,
        new HUniform(),
        h0, [[0., 0.], [2*b, 2*L]],
        [[b/2., 0.]]
);

var d = 5e-6;
var V = 150;
var air = new DRectangle0(-0, b, -d, L);

var mesh = mesh2;
mesh2 = distMesh2D.call(
        new DDiff(air, beam),
        new HUniform(),
        h0, [[-2*b, -2*d], [3*b, 2*L]],
        Matlab.asPntList(mesh.p)
);
// concatenation arrays of triangle
mesh2.t = mesh.t.concat(mesh2.t);
mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
viewFrame.drawMesh(mesh2);

var file2 = dir + "testSEle";
storeMesh(file2, mesh2, h0 / 1000, [
    {df: new Inside(beam), flags: IS_SET_POTENTIAL | WALL_NODE, potential: V},
    {df: new DPoly([[0, -d], [b, -d]]), flags: IS_SET_POTENTIAL | WALL_NODE, potential: 0.},
    {df: new DPoly([[0, L], [b, L]]), flags: IS_SET_DISPLACEMENT, displacement: [0./0., 0.]},
    {df: new DCircle(0, L, 0), flags: IS_SET_DISPLACEMENT, displacement: [0., 0.]},
    {df: new DCircle(b/2, 0, 0), flags: IS_PRINTABLE}
], [
    {df: forEvery, matId: 210}, // air - equals thickness as 200
    {df: beam, matId: 200} // E = 1e5
]);
//= 9.96096038e-8 - F podle vzorce

(function () {
    var x;
})();

