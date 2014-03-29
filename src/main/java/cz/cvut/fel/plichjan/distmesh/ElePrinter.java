package cz.cvut.fel.plichjan.distmesh;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 */
public class ElePrinter {
    private PrintStream out;

    public ElePrinter(String name) throws FileNotFoundException {
        out = new PrintStream(new FileOutputStream(name));
    }

    /**
     * Print file header
     *
     * @param node number of nodes
     * @param dim node dimension
     */
    public void printHeader(int node, int dim) {
        out.format("%d  %d  1\n", node, dim);
    }

    public void printEle(int index, int[] ele, int flags) {
        out.format("\t%d", index);
        for (int i : ele) {
            out.print('\t');
            out.print(i);
        }
        out.format("\t%d\n", flags);
    }

    public void close() {
        out.close();
    }
}
