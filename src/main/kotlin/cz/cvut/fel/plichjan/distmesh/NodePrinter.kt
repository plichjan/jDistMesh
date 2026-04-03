package cz.cvut.fel.plichjan.distmesh

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream

class NodePrinter @Throws(FileNotFoundException::class) constructor(name: String) {
    private val out: PrintStream = PrintStream(FileOutputStream(name))

    fun printHeader(node: Int, dim: Int, param: Int) {
        out.format("%d  %d  %d  1\n", node, dim, param)
    }

    fun printNode(index: Int, node: DoubleArray, params: DoubleArray, flags: Int) {
        out.format("\t%d", index)
        for (v in node) {
            out.print('\t')
            out.print(v)
        }
        for (v in params) {
            out.print('\t')
            out.print(v)
        }
        out.format("\t%d\n", flags)
    }

    fun close() {
        out.close()
    }
}
