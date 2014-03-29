package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.result.Mesh;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class MeshReader extends TriangleFormatReader {
    private List<int[]> triangles;
    private List<double[]> nodes;

    public MeshReader(String name) throws IOException {
        BufferedReader ele = initTokenizer(name + ".ele");
        parseTriangles();
        ele.close();

        BufferedReader node = initTokenizer(name + ".node");
        parseVertices();
        node.close();
    }

    public Mesh getMesh() {
        final Mesh mesh = new Mesh();
        mesh.setP(nodes.toArray(new double[nodes.size()][]));
        mesh.setT(triangles.toArray(new int[triangles.size()][]));
        return mesh;
    }

    private void parseTriangles() throws IOException {
        // parse header
        int nTriangles = readInt();
        int nodePerTriangle = readInt();
        int nAttribute = readInt();
//        readEOL();

        triangles = new ArrayList<int[]>(nTriangles);

        if ((nodePerTriangle != 6 && nodePerTriangle != 3)) {
            throw new IOException();
        }

        for (int i = 0; i < nTriangles; i++) {
            readInt(); // triangle index
            triangles.add(getNodes(nodePerTriangle));
            readIntArr(nAttribute); // triangle attributes
//            readEOL();
        }
    }

    private int[] getNodes(int nodePerTriangle) throws IOException {
        if (nodePerTriangle == 6) {
            int[] srcNodes = readIntArr(6);
            return new int[] {srcNodes[0], srcNodes[1], srcNodes[2], srcNodes[5], srcNodes[3], srcNodes[4]};
        } else {
            int[] srcNodes = readIntArr(3);
            return new int[] {srcNodes[0], srcNodes[1], srcNodes[2]};
        }
    }

    private void parseVertices() throws IOException {
        // parse header
        int nVertex = readInt();
        int dimension = readInt();
        int nAttribute = readInt();
        int boundaryMarker = readInt();
//        readEOL();

        nodes = new ArrayList<double[]>(nVertex);
        if (dimension != 2 || boundaryMarker != 1) {
            throw new IOException();
        }

        for (int i = 0; i < nVertex; i++) {
            readInt(); // vertex index
            double x = readDouble();
            double y = readDouble();
            nodes.add(new double[]{x, y});

            readDoubles(nAttribute);
            readInt(); // boundaryMarker
//            readEOL();
        }
    }

}
