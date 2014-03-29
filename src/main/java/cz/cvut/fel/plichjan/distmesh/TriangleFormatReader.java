package cz.cvut.fel.plichjan.distmesh;

import java.io.*;

public class TriangleFormatReader {
    private StreamTokenizer st;

    protected int[] readIntArr(int n) throws IOException {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = readInt() - 1;
        }
        return arr;
    }

    protected int readInt() throws IOException {
        if (st.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IOException();
        }
        return Integer.parseInt(st.sval);
    }

    protected double[] readDoubles(int n) throws IOException {
        final double[] arr = new double[n];
        for (int i = 0; i < n; i++) {
            arr[i] = readDouble();
        }
        return arr;
    }

    protected double readDouble() throws IOException {
        if (st.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IOException();
        }
        return Double.parseDouble(st.sval);
    }

    protected BufferedReader initTokenizer(String fileName) throws FileNotFoundException {
        BufferedReader r;
        r = new BufferedReader(new FileReader(fileName));
        st = new StreamTokenizer(r);
        st.resetSyntax();
        st.commentChar('#');
        // set the syntax to read -9.0123E+5 as a single token
        st.whitespaceChars((int) '\u0000', (int) '\u0020');
        st.wordChars((int) '.', (int) '.');
        st.wordChars((int)'-',(int)'-');
        st.wordChars((int)'+',(int)'+');
        st.wordChars((int)'0',(int)'9');
        st.wordChars((int)'E',(int)'E');
        st.wordChars((int) 'e', (int) 'e');
        st.wordChars((int) 'a', (int) 'a');
        st.wordChars((int) 'N', (int) 'N');
        return r;
    }
}
