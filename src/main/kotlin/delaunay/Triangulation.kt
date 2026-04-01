package delaunay

import java.util.*
import kotlin.collections.AbstractSet

/**
 * A 2D Delaunay Triangulation (DT) with incremental site insertion.
 *
 * This is not the fastest way to build a DT, but it's a reasonable way to build
 * a DT incrementally and it makes a nice interactive display. There are several
 * O(n log n) methods, but they require that the sites are all known initially.
 *
 * A Triangulation is a Set of Triangles. A Triangulation is unmodifiable as a
 * Set; the only way to change it is to add sites (via delaunayPlace).
 *
 * @author Paul Chew
 * Created July 2005. Derived from an earlier, messier version.
 * Modified November 2007. Rewrote to use AbstractSet as parent class and to use
 * the Graph class internally. Tried to make the DT algorithm clearer by
 * explicitly creating a cavity. Added code needed to find a Voronoi cell.
 * Converted to Kotlin in 2026.
 */
class Triangulation(triangle: Triangle) : AbstractSet<Triangle>() {

    private var mostRecent: Triangle? = null // Most recently "active" triangle
    private val triGraph: Graph<Triangle> = Graph() // Holds triangles for navigation

    init {
        triGraph.add(triangle)
        mostRecent = triangle
    }

    /* The following two methods are required by AbstractSet */

    override fun iterator(): Iterator<Triangle> {
        return triGraph.nodeSet().iterator()
    }

    override val size: Int
        get() = triGraph.nodeSet().size

    override fun toString(): String {
        return "Triangulation with $size triangles"
    }

    /**
     * True iff triangle is a member of this triangulation.
     * This method isn't required by AbstractSet, but it improves efficiency.
     * @param element the object to check for membership
     */
    override fun contains(element: Triangle): Boolean {
        return triGraph.nodeSet().contains(element)
    }

    /**
     * Report neighbor opposite the given vertex of triangle.
     * @param site a vertex of triangle
     * @param triangle we want the neighbor of this triangle
     * @return the neighbor opposite site in triangle; null if none
     * @throws IllegalArgumentException if site is not in this triangle
     */
    fun neighborOpposite(site: Pnt, triangle: Triangle): Triangle? {
        if (!triangle.contains(site))
            throw IllegalArgumentException("Bad vertex; not in triangle")
        for (neighbor in triGraph.neighbors(triangle)) {
            if (!neighbor.contains(site)) return neighbor
        }
        return null
    }

    /**
     * Return the set of triangles adjacent to triangle.
     * @param triangle the triangle to check
     * @return the neighbors of triangle
     */
    fun neighbors(triangle: Triangle): Set<Triangle> {
        return triGraph.neighbors(triangle)
    }

    /**
     * Report triangles surrounding site in order (cw or ccw).
     * @param site we want the surrounding triangles for this site
     * @param triangle a "starting" triangle that has site as a vertex
     * @return all triangles surrounding site in order (cw or ccw)
     * @throws IllegalArgumentException if site is not in triangle
     */
    fun surroundingTriangles(site: Pnt, triangle: Triangle): List<Triangle> {
        var currentTri = triangle
        if (!currentTri.contains(site))
            throw IllegalArgumentException("Site not in triangle")
        val list = ArrayList<Triangle>()
        val start = currentTri
        var guide = currentTri.getVertexButNot(site) // Affects cw or ccw
        while (true) {
            list.add(currentTri)
            val previous = currentTri
            currentTri = this.neighborOpposite(guide, currentTri) ?: break // Next triangle
            guide = previous.getVertexButNot(site, guide) // Update guide
            if (currentTri == start) break
        }
        return list
    }

    /**
     * Locate the triangle with point inside it or on its boundary.
     * @param point the point to locate
     * @return the triangle that holds point; null if no such triangle
     */
    fun locate(point: Pnt): Triangle? {
        var triangle = mostRecent
        if (triangle != null && !this.contains(triangle)) triangle = null

        // Try a directed walk (this works fine in 2D, but can fail in 3D)
        val visited = HashSet<Triangle>()
        while (triangle != null) {
            if (visited.contains(triangle)) { // This should never happen
                println("Warning: Caught in a locate loop")
                break
            }
            visited.add(triangle)
            // Corner opposite point
            val corner = point.isOutside(triangle.toTypedArray())
            if (corner == null) return triangle
            triangle = this.neighborOpposite(corner, triangle)
        }
        // No luck; try brute force
        println("Warning: Checking all triangles for $point")
        for (tri in this) {
            if (point.isOutside(tri.toTypedArray()) == null) return tri
        }
        // No such triangle
        println("Warning: No triangle holds $point")
        return null
    }

    /**
     * Place a new site into the DT.
     * Nothing happens if the site matches an existing DT vertex.
     * @param site the new Pnt
     * @throws IllegalArgumentException if site does not lie in any triangle
     */
    fun delaunayPlace(site: Pnt) {
        // Uses straightforward scheme rather than best asymptotic time

        // Locate containing triangle
        val triangle = locate(site)
        // Give up if no containing triangle or if site is already in DT
        if (triangle == null)
            throw IllegalArgumentException("No containing triangle")
        if (triangle.contains(site)) return

        // Determine the cavity and update the triangulation
        val cavity = getCavity(site, triangle)
        mostRecent = update(site, cavity)
    }

    /**
     * Determine the cavity caused by site.
     * @param site the site causing the cavity
     * @param triangle the triangle containing site
     * @return set of all triangles that have site in their circumcircle
     */
    private fun getCavity(site: Pnt, triangle: Triangle): Set<Triangle> {
        var currentTri = triangle
        val encroached = HashSet<Triangle>()
        val toBeChecked: Queue<Triangle> = LinkedList()
        val marked = HashSet<Triangle>()
        toBeChecked.add(currentTri)
        marked.add(currentTri)
        while (!toBeChecked.isEmpty()) {
            currentTri = toBeChecked.remove()
            if (site.vsCircumcircle(currentTri.toTypedArray()) == 1)
                continue // Site outside triangle => triangle not in cavity
            encroached.add(currentTri)
            // Check the neighbors
            for (neighbor in triGraph.neighbors(currentTri)) {
                if (marked.contains(neighbor)) continue
                marked.add(neighbor)
                toBeChecked.add(neighbor)
            }
        }
        return encroached
    }

    /**
     * Update the triangulation by removing the cavity triangles and then
     * filling the cavity with new triangles.
     * @param site the site that created the cavity
     * @param cavity the triangles with site in their circumcircle
     * @return one of the new triangles
     */
    private fun update(site: Pnt, cavity: Set<Triangle>): Triangle {
        val boundary = HashSet<Set<Pnt>>()
        val neighborsSet = HashSet<Triangle>()

        // Find boundary facets and adjacent triangles
        for (triangle in cavity) {
            neighborsSet.addAll(neighbors(triangle))
            for (vertex in triangle) {
                val facet = triangle.facetOpposite(vertex)
                if (boundary.contains(facet)) boundary.remove(facet)
                else boundary.add(facet)
            }
        }
        neighborsSet.removeAll(cavity) // Adj triangles only

        // Remove the cavity triangles from the triangulation
        for (triangle in cavity) triGraph.remove(triangle)

        // Build each new triangle and add it to the triangulation
        val newTriangles = HashSet<Triangle>()
        for (vertices in boundary) {
            val mutableVertices = vertices.toMutableSet()
            mutableVertices.add(site)
            val tri = Triangle(mutableVertices)
            triGraph.add(tri)
            newTriangles.add(tri)
        }

        // Update the graph links for each new triangle
        val allTriangles = HashSet<Triangle>(neighborsSet)
        allTriangles.addAll(newTriangles) // Adj triangle + new triangles
        for (triangle in newTriangles) {
            for (other in allTriangles) {
                if (triangle.isNeighbor(other))
                    triGraph.add(triangle, other)
            }
        }

        // Return one of the new triangles
        return newTriangles.iterator().next()
    }

    companion object {
        /**
         * Main program; used for testing.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val tri = Triangle(Pnt(-10.0, 10.0), Pnt(10.0, 10.0), Pnt(0.0, -10.0))
            println("Triangle created: $tri")
            val dt = Triangulation(tri)
            println("DelaunayTriangulation created: $dt")
            dt.delaunayPlace(Pnt(0.0, 0.0))
            dt.delaunayPlace(Pnt(1.0, 0.0))
            dt.delaunayPlace(Pnt(0.0, 1.0))
            println("After adding 3 points, we have a $dt")
            Triangle.moreInfo = true
            println("Triangles: ${dt.triGraph.nodeSet()}")
            for (triangle in dt) {
                val vertices = arrayOf(Pnt(-10.0, 10.0), Pnt(10.0, 10.0), Pnt(0.0, -10.0))
                var disjoint = true
                for (v in vertices) {
                    if (triangle.contains(v)) {
                        disjoint = false
                        break
                    }
                }
                if (disjoint) {
                    println(triangle)
                }
            }
        }
    }
}
