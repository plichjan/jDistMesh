package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.result.Mesh
import java.io.*
import java.util.ArrayList

class MeshReader @Throws(IOException::class) constructor(name: String) : TriangleFormatReader() {
    private var triangles: MutableList<IntArray>? = null
    private var nodes: MutableList<DoubleArray>? = null

    init {
        val ele = initTokenizer("$name.ele")
        parseTriangles()
        ele.close()

        val node = initTokenizer("$name.node")
        parseVertices()
        node.close()
    }

    val mesh: Mesh
        get() {
            val mesh = Mesh()
            mesh.p = nodes!!.toTypedArray()
            mesh.t = triangles!!.toTypedArray()
            return mesh
        }

    @Throws(IOException::class)
    private fun parseTriangles() {
        // parse header
        val nTriangles = readInt()
        val nodePerTriangle = readInt()
        val nAttribute = readInt()

        triangles = ArrayList(nTriangles)

        if (nodePerTriangle != 6 && nodePerTriangle != 3) {
            throw IOException()
        }

        for (i in 0 until nTriangles) {
            readInt() // triangle index
            triangles!!.add(getNodes(nodePerTriangle))
            readIntArr(nAttribute) // triangle attributes
        }
    }

    @Throws(IOException::class)
    private fun getNodes(nodePerTriangle: Int): IntArray {
        return if (nodePerTriangle == 6) {
            val srcNodes = readIntArr(6)
            intArrayOf(srcNodes[0], srcNodes[1], srcNodes[2], srcNodes[5], srcNodes[3], srcNodes[4])
        } else {
            val srcNodes = readIntArr(3)
            intArrayOf(srcNodes[0], srcNodes[1], srcNodes[2])
        }
    }

    @Throws(IOException::class)
    private fun parseVertices() {
        // parse header
        val nVertex = readInt()
        val dimension = readInt()
        val nAttribute = readInt()
        val boundaryMarker = readInt()

        nodes = ArrayList(nVertex)
        if (dimension != 2 || (boundaryMarker != 1 && boundaryMarker != 0)) {
            // Some .node files might have 0 boundary markers
            // throw new IOException();
        }

        for (i in 0 until nVertex) {
            readInt() // vertex index
            val x = readDouble()
            val y = readDouble()
            nodes!!.add(doubleArrayOf(x, y))

            readDoubles(nAttribute)
            readInt() // boundaryMarker
        }
    }
}
