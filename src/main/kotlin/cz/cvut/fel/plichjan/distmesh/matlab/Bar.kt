package cz.cvut.fel.plichjan.distmesh.matlab

import kotlin.math.max
import kotlin.math.min

/**
 * Edge from point "a" to point "b".
 */
open class Bar(a: Int, b: Int) : Comparable<Bar> {
    var a: Int = min(a, b)
    var b: Int = max(a, b)

    override fun compareTo(other: Bar): Int {
        return when {
            a < other.a -> -1
            a > other.a -> 1
            b < other.b -> -1
            b > other.b -> 1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Bar) return false

        if (a != other.a) return false
        if (b != other.b) return false

        return true
    }

    override fun hashCode(): Int {
        var result = a
        result = 31 * result + b
        return result
    }

    override fun toString(): String {
        return "Bar(a=$a, b=$b)"
    }
}
