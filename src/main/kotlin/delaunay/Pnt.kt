package delaunay

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Points in Euclidean space, implemented as DoubleArray.
 *
 * Includes simple geometric operations.
 * Uses matrices; a matrix is represented as an array of Pnts.
 * Uses simplices; a simplex is represented as an array of Pnts.
 *
 * @author Paul Chew
 * Created July 2005. Derived from an earlier, messier version.
 * Modified November 2007. Minor clean up.
 * Converted to Kotlin in 2026.
 */
class Pnt(vararg coords: Double) {
    private val coordinates: DoubleArray = coords.copyOf()
    var index: Int = 0

    override fun toString(): String {
        if (coordinates.isEmpty()) return "Pnt()"
        return coordinates.joinToString(separator = ",", prefix = "Pnt(", postfix = ")")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pnt) return false
        return coordinates.contentEquals(other.coordinates)
    }

    override fun hashCode(): Int {
        return coordinates.contentHashCode()
    }

    /* Pnts as vectors */

    fun coord(i: Int): Double = coordinates[i]

    fun dimension(): Int = coordinates.size

    fun dimCheck(p: Pnt): Int {
        val len = coordinates.size
        if (len != p.coordinates.size)
            throw IllegalArgumentException("Dimension mismatch")
        return len
    }

    fun extend(vararg coords: Double): Pnt {
        val result = DoubleArray(coordinates.size + coords.size)
        System.arraycopy(coordinates, 0, result, 0, coordinates.size)
        System.arraycopy(coords, 0, result, coordinates.size, coords.size)
        return Pnt(*result)
    }

    fun dot(p: Pnt): Double {
        val len = dimCheck(p)
        var sum = 0.0
        for (i in 0 until len)
            sum += coordinates[i] * p.coordinates[i]
        return sum
    }

    fun magnitude(): Double = sqrt(this.dot(this))

    fun subtract(p: Pnt): Pnt {
        val len = dimCheck(p)
        val coords = DoubleArray(len)
        for (i in 0 until len)
            coords[i] = coordinates[i] - p.coordinates[i]
        return Pnt(*coords)
    }

    fun add(p: Pnt): Pnt {
        val len = dimCheck(p)
        val coords = DoubleArray(len)
        for (i in 0 until len)
            coords[i] = coordinates[i] + p.coordinates[i]
        return Pnt(*coords)
    }

    fun add(a: Double, p: Pnt): Pnt {
        val len = dimCheck(p)
        val coords = DoubleArray(len)
        for (i in 0 until len)
            coords[i] = coordinates[i] + a * p.coordinates[i]
        return Pnt(*coords)
    }

    fun angle(p: Pnt): Double = acos(this.dot(p) / (this.magnitude() * p.magnitude()))

    fun bisector(point: Pnt): Pnt {
        dimCheck(point)
        val diff = this.subtract(point)
        val sum = this.add(point)
        val dotResult = diff.dot(sum)
        return diff.extend(-dotResult / 2.0)
    }

    /* Pnts as matrices */

    companion object {
        fun toString(matrix: Array<Pnt>): String {
            return matrix.joinToString(separator = " ", prefix = "{ ", postfix = " }")
        }

        fun determinant(matrix: Array<Pnt>): Double {
            if (matrix.size != matrix[0].dimension())
                throw IllegalArgumentException("Matrix is not square")
            val columns = BooleanArray(matrix.size) { true }
            return try {
                determinant(matrix, 0, columns)
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw IllegalArgumentException("Matrix is wrong shape")
            }
        }

        private fun determinant(matrix: Array<Pnt>, row: Int, columns: BooleanArray): Double {
            if (row == matrix.size) return 1.0
            var sum = 0.0
            var sign = 1
            for (col in columns.indices) {
                if (!columns[col]) continue
                columns[col] = false
                sum += sign * matrix[row].coordinates[col] * determinant(matrix, row + 1, columns)
                columns[col] = true
                sign = -sign
            }
            return sum
        }

        fun cross(matrix: Array<Pnt>): Pnt {
            val len = matrix.size + 1
            if (len != matrix[0].dimension())
                throw IllegalArgumentException("Dimension mismatch")
            val columns = BooleanArray(len) { true }
            val result = DoubleArray(len)
            var sign = 1
            try {
                for (i in 0 until len) {
                    columns[i] = false
                    result[i] = sign.toDouble() * determinant(matrix, 0, columns)
                    columns[i] = true
                    sign = -sign
                }
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw IllegalArgumentException("Matrix is wrong shape")
            }
            return Pnt(*result)
        }

        fun content(simplex: Array<Pnt>): Double {
            val matrix = Array(simplex.size) { i -> simplex[i].extend(1.0) }
            var fact = 1
            for (i in 1 until matrix.size) fact *= i
            return determinant(matrix) / fact.toDouble()
        }

        fun circumcenter(simplex: Array<Pnt>): Pnt {
            val dim = simplex[0].dimension()
            if (simplex.size - 1 != dim)
                throw IllegalArgumentException("Dimension mismatch")
            val matrix = Array(dim) { i -> simplex[i].bisector(simplex[i+1]) }
            val hCenter = cross(matrix) // Center in homogeneous coordinates
            val last = hCenter.coordinates[dim]
            val result = DoubleArray(dim)
            for (i in 0 until dim) result[i] = hCenter.coordinates[i] / last
            return Pnt(*result)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val p = Pnt(1.0, 2.0, 3.0)
            println("Pnt created: $p")
            val matrix1 = arrayOf(Pnt(1.0, 2.0), Pnt(3.0, 4.0))
            val matrix2 = arrayOf(Pnt(7.0, 0.0, 5.0), Pnt(2.0, 4.0, 6.0), Pnt(3.0, 8.0, 1.0))
            print("Results should be -2 and -288: ")
            println("${determinant(matrix1)} ${determinant(matrix2)}")
            val p1 = Pnt(1.0, 1.0)
            val p2 = Pnt(-1.0, 1.0)
            println("Angle between $p1 and $p2: ${p1.angle(p2)}")
            println("$p1 subtract $p2: ${p1.subtract(p2)}")
            val v0 = Pnt(0.0, 0.0)
            val v1 = Pnt(1.0, 1.0)
            val v2 = Pnt(2.0, 2.0)
            val vs = arrayOf(v0, Pnt(0.0, 1.0), Pnt(1.0, 0.0))
            val vp = Pnt(0.1, 0.1)
            println("$vp isInside ${toString(vs)}: ${vp.isInside(vs)}")
            println("$v1 isInside ${toString(vs)}: ${v1.isInside(vs)}")
            println("$vp vsCircumcircle ${toString(vs)}: ${vp.vsCircumcircle(vs)}")
            println("$v1 vsCircumcircle ${toString(vs)}: ${v1.vsCircumcircle(vs)}")
            println("$v2 vsCircumcircle ${toString(vs)}: ${v2.vsCircumcircle(vs)}")
            println("Circumcenter of ${toString(vs)} is ${circumcenter(vs)}")
        }
    }

    /* Pnts as simplices */

    fun relation(simplex: Array<Pnt>): IntArray {
        val dim = simplex.size - 1
        if (this.dimension() != dim)
            throw IllegalArgumentException("Dimension mismatch")

        /* Create and load the matrix */
        val matrix = Array(dim + 1) { i ->
            val coords = DoubleArray(dim + 2)
            if (i == 0) {
                for (j in coords.indices) coords[j] = 1.0
            } else {
                coords[0] = coordinates[i - 1]
                for (j in simplex.indices)
                    coords[j + 1] = simplex[j].coordinates[i - 1]
            }
            Pnt(*coords)
        }

        /* Compute and analyze the vector of areas/volumes/contents */
        val vector = cross(matrix)
        val contentResult = vector.coordinates[0]
        val result = IntArray(dim + 1)
        for (i in result.indices) {
            val value = vector.coordinates[i + 1]
            if (abs(value) <= 1.0e-6 * abs(contentResult)) result[i] = 0
            else if (value < 0) result[i] = -1
            else result[i] = 1
        }
        var finalContent = contentResult
        if (finalContent < 0) {
            for (i in result.indices)
                result[i] = -result[i]
        }
        if (finalContent == 0.0) {
            for (i in result.indices)
                result[i] = abs(result[i])
        }
        return result
    }

    fun isOutside(simplex: Array<Pnt>): Pnt? {
        val result = this.relation(simplex)
        for (i in result.indices) {
            if (result[i] > 0) return simplex[i]
        }
        return null
    }

    fun isOn(simplex: Array<Pnt>): Pnt? {
        val result = this.relation(simplex)
        var witness: Pnt? = null
        for (i in result.indices) {
            if (result[i] == 0) witness = simplex[i]
            else if (result[i] > 0) return null
        }
        return witness
    }

    fun isInside(simplex: Array<Pnt>): Boolean {
        val result = this.relation(simplex)
        for (r in result) if (r >= 0) return false
        return true
    }

    fun vsCircumcircle(simplex: Array<Pnt>): Int {
        val matrix = Array(simplex.size + 1) { i ->
            if (i < simplex.size) simplex[i].extend(1.0, simplex[i].dot(simplex[i]))
            else this.extend(1.0, this.dot(this))
        }
        val d = determinant(matrix)
        var result = if (d < 0) -1 else if (d > 0) 1 else 0
        if (content(simplex) < 0) result = -result
        return result
    }

    fun get(i: Int): Double = coord(i)

    fun getData(): DoubleArray = coordinates

    fun scale(a: Double): Pnt {
        val len = dimension()
        val coords = DoubleArray(len)
        for (i in 0 until len)
            coords[i] = a * coordinates[i]
        return Pnt(*coords)
    }
}
