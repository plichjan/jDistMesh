package cz.cvut.fel.plichjan.distmesh

import cz.cvut.fel.plichjan.distmesh.result.Mesh
import java.io.IOException
import java.util.ArrayList

class FreeFemMeshReader @Throws(IOException::class) constructor(name: String) : TriangleFormatReader() {
    private var nVertex = 0
    private var nTriangles = 0
    private var triangles: MutableList<IntArray>? = null
    private var nodes: MutableList<DoubleArray>? = null

    init {
        val msh = initTokenizer("$name.msh")
        parseHeader()
        parseVertices()
        parseTriangles()
        msh.close()
    }

    val mesh: Mesh
        get() {
            val mesh = Mesh()
            mesh.p = nodes!!.toTypedArray()
            mesh.t = triangles!!.toTypedArray()
            return mesh
        }

    @Throws(IOException::class)
    private fun parseHeader() {
        nVertex = readInt()
        nTriangles = readInt()
        readInt() // number of edges on boundary
    }

    @Throws(IOException::class)
    private fun parseTriangles() {
        triangles = ArrayList(nTriangles)
        for (i in 0 until nTriangles) {
            triangles!!.add(readIntArr(3))
            readInt() // triangle label
        }
    }

    @Throws(IOException::class)
    private fun parseVertices() {
        nodes = ArrayList(nVertex)
        for (i in 0 until nVertex) {
            nodes!!.add(readDoubles(2))
            readInt() // vertex label
        }
    }
}
