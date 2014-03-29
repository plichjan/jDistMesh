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

function herodes(file, box, electrode1, electrode2, pfix) {
    var zoom = 5;
    var width = box.width * zoom, height = box.height * zoom, x0 = box.width * (1 - zoom) / 2. , y0 = box.height * (1 - zoom) / 2. ;
    pfix = pfix || [];
    pfix = [[x0, y0], [x0, height + y0], [width + x0, y0], [width + x0, height + y0]].concat(pfix);
    var h0 = 10 / box.dividing;
    var dBox = new DRectangle0(x0, x0 + width, y0, y0 + height);
    var fd = new DDiff(dBox, new DUnion(electrode1, electrode2));

    function fh(x, y) {
        return Math.min(
                h0+0.3*Math.abs(electrode1.call(x,y)),
                h0+0.3*Math.abs(electrode2.call(x,y)));
    }
    var mesh2 = distMesh2D.call(
            fd,
            fh,
            h0, [[x0, y0], [x0 + width, y0 + height]],
            pfix
    );
    mesh2.p = new BndProj().call(mesh2.p, mesh2.t, fd);
    mesh2 = new AddMidpoints().call(mesh2.p, mesh2.t);
    viewFrame.drawMesh(mesh2);

    storeMesh(dir + file, mesh2, h0 / 1000., [
        {df: electrode1, flags: IS_SET_POTENTIAL, potential: -1},
        {df: electrode2, flags: IS_SET_POTENTIAL, potential: +1}
    ], [
        {df: forEvery, matId: 10}
    ]);
}

var Box = {width:280, height:200, dividing:2};

// stop condition - min. movement
distMesh2D.dptol = .01;
//herodes("herodes01", Box,
//        new DPoly([[150, 90], [250, 90], [250, 110], [150, 110]]),
//        new DPoly([[78, 100], [138, 160], [124, 174], [50, 100], [124, 26], [138, 40]]),
//        [[150, 90], [250, 90], [250, 110], [150, 110], [78, 100], [138, 160], [124, 174], [50, 100], [124, 26], [138, 40]]
//);
herodes("herodes02", Box,
        new DPoly([[39, 91], [87, 178], [105, 167], [66, 99], [134, 58], [123, 40]]),
        new DPoly([[197, 25], [211, 104], [134, 119], [138, 138], [234, 121], [216, 22]]),
        [[39, 91], [87, 178], [105, 167], [66, 99], [134, 58], [123, 40], [197, 25], [211, 104], [134, 119], [138, 138], [234, 121], [216, 22]]
);
//herodes("herodes03", Box,
//        new DPoly([[30, 30], [130, 30], [130, 50], [50, 50], [50, 130], [30, 130]]),
//        new DCircle(200, 120, 50),
//        [[30, 30], [130, 30], [130, 50], [50, 50], [50, 130], [30, 130]]
//);
//herodes("herodes04", Box,
//        new DCircle(205, 125, 50),
//        new DCircle(75, 75, 50)
//);
//herodes("herodes05", Box,
//        new DPoly([[45, 175], [30, 160], [95, 85], [110, 95]]),
//        new DPoly([[197, 25], [211, 104], [134, 119], [138, 138], [234, 121], [216, 22]]),
//        [[45, 175], [30, 160], [95, 85], [110, 95], [197, 25], [211, 104], [134, 119], [138, 138], [234, 121], [216, 22]]
//);
//herodes("herodes06", Box,
//        new DPoly([[30, 30], [130, 30], [130, 50], [30, 50]]),
//        new DCircle(200, 120, 50),
//        [[30, 30], [130, 30], [130, 50], [30, 50]]
//);
//herodes("herodes07", Box,
//        new DPoly([[50, 50], [150, 50], [150, 70], [70, 70], [70, 150], [50, 150]]),
//        new DPoly([[130, 130], [230, 130], [230, 150], [130, 150]]),
//        [[50, 50], [150, 50], [150, 70], [70, 70], [70, 150], [50, 150], [130, 130], [230, 130], [230, 150], [130, 150]]
//);
