package cz.cvut.fel.plichjan.distmesh.matlab;

/**
 * Edge from point "a" to point "b".
 */
public class Bar implements Comparable<Bar> {
    protected int a;
    protected int b;

    public Bar(int a, int b) {
        this.a = Math.min(a, b);
        this.b = Math.max(a, b);
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    @Override
    public int compareTo(Bar o) {
        return a < o.getA() ? -1 : a > o.getA() ? 1 : b < o.getB() ? -1 : b > o.getB() ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bar)) {
            return false;
        }

        Bar bar = (Bar) o;

        if (a != bar.a) {
            return false;
        }
        if (b != bar.b) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = a;
        result = 31 * result + b;
        return result;
    }

    @Override
    public String toString() {
        return "Bar{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
