package delaunay

/**
 * A Triangle is an immutable Set of exactly three Pnts.
 *
 * All Set operations are available. Individual vertices can be accessed via
 * iterator() and also via triangle.get(index).
 *
 * Note that, even if two triangles have the same vertex set, they are
 * *different* triangles. Methods equals() and hashCode() are consistent with
 * this rule.
 *
 * @author Paul Chew
 * Created December 2007. Replaced general simplices with geometric triangle.
 * Converted to Kotlin in 2026.
 */
class Triangle : ArraySet<Pnt> {

    val idNumber: Int
    private var circumcenter: Pnt? = null

    companion object {
        private var idGenerator = 0
        var moreInfo = false
    }

    /**
     * @param vertices the vertices of the Triangle.
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    constructor(vararg vertices: Pnt) : this(vertices.toList())

    /**
     * @param collection a Collection holding the Simplex vertices
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    constructor(collection: Collection<Pnt>) : super(collection) {
        idNumber = idGenerator++
        if (this.size != 3)
            throw IllegalArgumentException("Triangle must have 3 vertices")
    }

    override fun toString(): String {
        if (!moreInfo) return "Triangle$idNumber"
        return "Triangle$idNumber" + super.toString()
    }

    /**
     * Get arbitrary vertex of this triangle, but not any of the bad vertices.
     * @param badVertices one or more bad vertices
     * @return a vertex of this triangle, but not one of the bad vertices
     * @throws NoSuchElementException if no vertex found
     */
    fun getVertexButNot(vararg badVertices: Pnt): Pnt {
        val bad = badVertices.toList()
        for (v in this) if (!bad.contains(v)) return v
        throw NoSuchElementException("No vertex found")
    }

    /**
     * True iff triangles are neighbors. Two triangles are neighbors if they
     * share a facet.
     * @param triangle the other Triangle
     * @return true iff this Triangle is a neighbor of triangle
     */
    fun isNeighbor(triangle: Triangle): Boolean {
        var count = 0
        for (vertex in this) {
            if (!triangle.contains(vertex)) count++
        }
        return count == 1
    }

    /**
     * Report the facet opposite vertex.
     * @param vertex a vertex of this Triangle
     * @return the facet opposite vertex
     * @throws IllegalArgumentException if the vertex is not in triangle
     */
    fun facetOpposite(vertex: Pnt): ArraySet<Pnt> {
        val facet = ArraySet(this)
        if (!facet.remove(vertex))
            throw IllegalArgumentException("Vertex not in triangle")
        return facet
    }

    /**
     * @return the triangle's circumcenter
     */
    fun getCircumcenter(): Pnt {
        return circumcenter ?: Pnt.circumcenter(this.toTypedArray()).also { circumcenter = it }
    }

    /* The following methods ensure that a Triangle is immutable in spirit */

    override fun add(element: Pnt): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<Pnt>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun remove(element: Pnt): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(elements: Collection<Pnt>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun retainAll(elements: Collection<Pnt>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun iterator(): MutableIterator<Pnt> {
        val it = super<ArraySet>.iterator()
        return object : MutableIterator<Pnt> {
            override fun hasNext(): Boolean = it.hasNext()
            override fun next(): Pnt = it.next()
            override fun remove() {
                throw UnsupportedOperationException()
            }
        }
    }

    /* The following two methods ensure that all triangles are different. */

    override fun hashCode(): Int {
        return idNumber
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }
}
