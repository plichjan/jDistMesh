package delaunay

import java.util.Collections

/**
 * Straightforward undirected graph implementation.
 * Nodes are generic type N.
 *
 * @author Paul Chew
 * Created November, December 2007. For use in Delaunay/Voronoi code.
 * Converted to Kotlin in 2026.
 */
class Graph<N> {

    private val theNeighbors = HashMap<N, MutableSet<N>>() // Node -> adjacent nodes
    private val theNodeSet = Collections.unmodifiableSet(theNeighbors.keys) // Set view of all nodes

    /**
     * Add a node. If node is already in graph then no change.
     * @param node the node to add
     */
    fun add(node: N) {
        if (theNeighbors.containsKey(node)) return
        theNeighbors[node] = ArraySet()
    }

    /**
     * Add a link. If the link is already in graph then no change.
     * @param nodeA one end of the link
     * @param nodeB the other end of the link
     * @throws NullPointerException if either endpoint is not in graph
     */
    fun add(nodeA: N, nodeB: N) {
        theNeighbors[nodeA]?.add(nodeB) ?: throw NullPointerException("nodeA not in graph")
        theNeighbors[nodeB]?.add(nodeA) ?: throw NullPointerException("nodeB not in graph")
    }

    /**
     * Remove node and any links that use node. If node not in graph, nothing
     * happens.
     * @param node the node to remove.
     */
    fun remove(node: N) {
        val neighbors = theNeighbors[node] ?: return
        for (neighbor in neighbors) {
            theNeighbors[neighbor]?.remove(node) // Remove "to" links
        }
        neighbors.clear() // Remove "from" links
        theNeighbors.remove(node) // Remove the node
    }

    /**
     * Remove the specified link. If link not in graph, nothing happens.
     * @param nodeA one end of the link
     * @param nodeB the other end of the link
     * @throws NullPointerException if either endpoint is not in graph
     */
    fun remove(nodeA: N, nodeB: N) {
        theNeighbors[nodeA]?.remove(nodeB) ?: throw NullPointerException("nodeA not in graph")
        theNeighbors[nodeB]?.remove(nodeA) ?: throw NullPointerException("nodeB not in graph")
    }

    /**
     * Report all the neighbors of node.
     * @param node the node
     * @return the neighbors of node
     * @throws NullPointerException if node does not appear in graph
     */
    fun neighbors(node: N): Set<N> {
        val neighbors = theNeighbors[node] ?: throw NullPointerException("node not in graph")
        return Collections.unmodifiableSet(neighbors)
    }

    /**
     * Returns an unmodifiable Set view of the nodes contained in this graph.
     * The set is backed by the graph, so changes to the graph are reflected in
     * the set.
     * @return a Set view of the graph's node set
     */
    fun nodeSet(): Set<N> {
        return theNodeSet
    }
}
