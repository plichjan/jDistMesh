package cz.cvut.fel.plichjan.distmesh

import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream

class ElePrinter @Throws(FileNotFoundException::class) constructor(name: String) {
    private val out: PrintStream = PrintStream(FileOutputStream(name))

    fun printHeader(node: Int, dim: Int) {
        out.format("%d  %d  1\n", node, dim)
    }

    fun printEle(index: Int, ele: IntArray, flags: Int) {
        out.format("\t%d", index)
        for (i in ele) {
            out.print('\t')
            out.print(i)
        }
        out.format("\t%d\n", flags)
    }

    fun close() {
        out.close()
    }
}
