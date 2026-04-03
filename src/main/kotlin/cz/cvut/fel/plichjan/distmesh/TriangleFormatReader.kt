package cz.cvut.fel.plichjan.distmesh

import java.io.*

open class TriangleFormatReader {
    private var st: StreamTokenizer? = null

    @Throws(IOException::class)
    protected fun readIntArr(n: Int): IntArray {
        val arr = IntArray(n)
        for (i in 0 until n) {
            arr[i] = readInt() - 1
        }
        return arr
    }

    @Throws(IOException::class)
    protected fun readInt(): Int {
        if (st!!.nextToken() != StreamTokenizer.TT_WORD) {
            throw IOException()
        }
        return st!!.sval.toInt()
    }

    @Throws(IOException::class)
    protected fun readDoubles(n: Int): DoubleArray {
        val arr = DoubleArray(n)
        for (i in 0 until n) {
            arr[i] = readDouble()
        }
        return arr
    }

    @Throws(IOException::class)
    protected fun readDouble(): Double {
        if (st!!.nextToken() != StreamTokenizer.TT_WORD) {
            throw IOException()
        }
        return st!!.sval.toDouble()
    }

    @Throws(FileNotFoundException::class)
    protected fun initTokenizer(fileName: String): BufferedReader {
        val r = BufferedReader(FileReader(fileName))
        st = StreamTokenizer(r).apply {
            resetSyntax()
            commentChar('#'.code)
            // set the syntax to read -9.0123E+5 as a single token
            whitespaceChars('\u0000'.code, '\u0020'.code)
            wordChars('.'.code, '.'.code)
            wordChars('-'.code, '-'.code)
            wordChars('+'.code, '+'.code)
            wordChars('0'.code, '9'.code)
            wordChars('E'.code, 'E'.code)
            wordChars('e'.code, 'e'.code)
            wordChars('a'.code, 'a'.code)
            wordChars('N'.code, 'N'.code)
        }
        return r
    }
}
