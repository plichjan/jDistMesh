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

//Circle with Hole
var r0 = 1, r1 = 5;
var v0 = 1, v1 = 0;
var c0 = new DCircle(0, 0, r0);
var c1 = new DCircle(0, 0, r1);

function myCircle(i, h0) {
    distMesh2D.dptol = 0.0001;
    var df = new DDiff(c1, c0);

    var mesh2 =
    distMesh2D.call(
            df,
            function call(x, y) {
                return h0+0.3*c0.call(x,y);
            },
            h0, [[-r1, -r1], [r1, r1]],
            []
    );

    mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
    mesh2.p = new BndProj().call(mesh2.p, mesh2.t, df);
    viewFrame.drawMesh(mesh2);

    var file = dir + "circleNu0" + i;
    storeMesh(file, mesh2, h0 / 4, [
        {df: c0, flags: IS_SET_POTENTIAL, potential: v0 },
        {df: c1, flags: IS_SET_POTENTIAL, potential: v1 }
    ], [
        {df: forEvery, matId: 10}
    ]);
    return mesh2;
}

function freeCircle(i) {
    var df = new DDiff(c1, c0);
    var mesh2 =
    new FreeFemMeshReader("c:\\Program Files\\FreeFem++-cs-14.3\\Contents\\Resources\\examples\\circle0" + i).getMesh();
    mesh2.p = new BndProj().call(mesh2.p, mesh2.t, df);
    mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
    viewFrame.drawMesh(mesh2);

    // for test
    var h0 = Math.PI * 2 / (8*4*i * 2);

    var file = dir + "circle"+i;
    storeMesh(file, mesh2, h0, [
        {df: c0, flags: IS_SET_POTENTIAL, potential: v0 },
        {df: c1, flags: IS_SET_POTENTIAL, potential: v1 }
    ], [
        {df: forEvery, matId: 10}
    ]);
}

function myCircleSplit(mesh2, i, h0) {
    var df = new DDiff(c1, c0);

    mesh2 = new Split().call(mesh2.p, mesh2.t);
    mesh2.p = new BndProj().call(mesh2.p, mesh2.t, df);
    mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
    viewFrame.drawMesh(mesh2);

    var file = dir + "circleNu0" + i;
    storeMesh(file, mesh2, h0 / 4, [
        {df: c0, flags: IS_SET_POTENTIAL, potential: v0 },
        {df: c1, flags: IS_SET_POTENTIAL, potential: v1 }
    ], [
        {df: forEvery, matId: 10}
    ]);
    return mesh2;
}

var mesh, i, h0 = 1;
for(i = 1; i <= 20; i++ ) {
    freeCircle(i);
}

/*
for(i = 1; i <= 8; i++ ) {
    if (i <= 3) {
        mesh = myCircle(i, h0);
    } else {
        mesh = myCircleSplit(mesh, i, h0);
    }
    h0 /= 2;
}
*/
