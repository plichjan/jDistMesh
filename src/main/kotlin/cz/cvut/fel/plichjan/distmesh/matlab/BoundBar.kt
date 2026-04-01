package cz.cvut.fel.plichjan.distmesh.matlab

/**
 */
class BoundBar : Bar {
    var c: Int = 0
    var tr: Int = 0
    var ix: Int = 0

    constructor(a: Int, b: Int) : super(a, b)

    constructor(a: Int, b: Int, c: Int) : super(a, b) {
        this.c = c
    }

    constructor(a: Int, b: Int, c: Int, tr: Int) : super(a, b) {
        this.c = c
        this.tr = tr
    }
}
