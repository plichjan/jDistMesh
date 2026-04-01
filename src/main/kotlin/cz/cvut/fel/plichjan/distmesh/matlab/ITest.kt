package cz.cvut.fel.plichjan.distmesh.matlab

/**
 */
fun interface ITest<E> {
    fun call(i: Int, item: E): Boolean
}
