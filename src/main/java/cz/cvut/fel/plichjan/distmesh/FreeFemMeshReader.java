package cz.cvut.fel.plichjan.distmesh;

import cz.cvut.fel.plichjan.distmesh.result.Mesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * http://www.ann.jussieu.fr/~lehyaric/ffcs/doc/ff/ffdocsu17.html
 */
public class FreeFemMeshReader extends TriangleFormatReader {
    private int nVertex;
    private int nTriangles;
    private List<int[]> triangles;
    private List<double[]> nodes;

    public FreeFemMeshReader(String name) throws IOException {
        BufferedReader msh = initTokenizer(name + ".msh");
        parseHeader();
        parseVertices();
        parseTriangles();
        msh.close();
    }

    public Mesh getMesh() {
        final Mesh mesh = new Mesh();
        mesh.setP(nodes.toArray(new double[nodes.size()][]));
        mesh.setT(triangles.toArray(new int[triangles.size()][]));
        return mesh;
    }

    private void parseHeader() throws IOException {
        // parse header
        nVertex = readInt();
        nTriangles = readInt();
        readInt(); // the number of edges on boundary
    }

    private void parseTriangles() throws IOException {
        triangles = new ArrayList<int[]>(nTriangles);

        for (int i = 0; i < nTriangles; i++) {
            triangles.add(readIntArr(3));
            readInt(); // triangle label
//            readEOL();
        }
    }

    private void parseVertices() throws IOException {
        nodes = new ArrayList<double[]>(nVertex);

        for (int i = 0; i < nVertex; i++) {
            nodes.add(readDoubles(2));
            readInt(); // vertex label
//            readEOL();
        }
    }

}
