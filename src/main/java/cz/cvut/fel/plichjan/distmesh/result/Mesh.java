package cz.cvut.fel.plichjan.distmesh.result;

import java.util.Arrays;

/**
 * The node positions p. This N-by-M array contains the M coordinates for each of the N nodes.
 *
 * The triangle indices t. The row associated with each triangle has M+1 integer entries to specify node numbers in that triangle.
 */
public class Mesh {
    private double[][] p;
    private int[][] t;

    public double[][] getP() {
        return p;
    }

    public void setP(double[][] p) {
        this.p = p;
    }

    public int[][] getT() {
        return t;
    }

    public void setT(int[][] t) {
        this.t = t;
    }

    public String toTsin() {
        if (p == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(p.length).append("\n");
        for (double[] point : p) {
            sb.append(point[0]).append(" ");
            sb.append(point[1]).append(" ");
            sb.append(0.).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Mesh{" +
                "\np=" + Arrays.deepToString(p) +
                ", \nt=" + Arrays.deepToString(t) +
                '}';
    }
}
