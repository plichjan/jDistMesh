package cz.cvut.fel.plichjan.distmesh;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 */
public class NodePrinter {
    private PrintStream out;

    public NodePrinter(String name) throws FileNotFoundException {
        out = new PrintStream(new FileOutputStream(name));
    }

    /**
     * Print file header
     *
     * @param node number of nodes
     * @param dim node dimension
     * @param param number of node's parameters
     */
    public void printHeader(int node, int dim, int param) {
        out.format("%d  %d  %d  1\n", node, dim, param);
    }

    public void printNode(int index, double[] node, double[] params, int flags) {
        out.format("\t%d", index);
        for (double v : node) {
            out.print('\t');
            out.print(v);
        }
        for (double v : params) {
            out.print('\t');
            out.print(v);
        }
        out.format("\t%d\n", flags);
    }

    public void close() {
        out.close();
    }
}
