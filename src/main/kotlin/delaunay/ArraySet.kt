package delaunay

import java.util.AbstractSet

/**
 * An ArrayList implementation of Set. An ArraySet is good for small sets; it
 * has less overhead than a HashSet or a TreeSet.
 *
 * @author Paul Chew
 * Created December 2007. For use with Voronoi/Delaunay applet.
 * Converted to Kotlin in 2026.
 */
open class ArraySet<E> : AbstractSet<E>, MutableSet<E> {

    private val items: MutableList<E>

    /**
     * Create an empty set (default initial capacity is 3).
     */
    constructor() : this(3)

    /**
     * Create an empty set with the specified initial capacity.
     * @param initialCapacity the initial capacity
     */
    constructor(initialCapacity: Int) {
        items = ArrayList(initialCapacity)
    }

    /**
     * Create a set containing the items of the collection. Any duplicate
     * items are discarded.
     * @param collection the source for the items of the small set
     */
    constructor(collection: Collection<E>) {
        items = ArrayList(collection.size)
        for (item in collection) {
            if (!items.contains(item)) items.add(item)
        }
    }

    /**
     * Get the item at the specified index.
     * @param index where the item is located in the ListSet
     * @return the item at the specified index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    operator fun get(index: Int): E = items[index]

    /**
     * True iff any member of the collection is also in the ArraySet.
     * @param collection the Collection to check
     * @return true iff any member of collection appears in this ArraySet
     */
    fun containsAny(collection: Collection<*>): Boolean {
        for (item in collection) {
            if (this.contains(item)) return true
        }
        return false
    }

    override fun add(element: E): Boolean {
        if (items.contains(element)) return false
        return items.add(element)
    }

    override fun iterator(): MutableIterator<E> = items.iterator()

    override val size: Int
        get() = items.size

    override fun addAll(elements: Collection<E>): Boolean {
        var modified = false
        for (element in elements) {
            if (add(element)) modified = true
        }
        return modified
    }

    override fun clear() {
        items.clear()
    }

    override fun remove(element: E): Boolean {
        return items.remove(element)
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        return items.removeAll(elements)
    }

    override fun retainAll(elements: Collection<E>): Boolean {
        return items.retainAll(elements)
    }
}
