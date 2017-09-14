package edu.neu.coe.info._6205;

public class Tuple {

    public Tuple(int x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Tuple("+x+", "+y+")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;

        if (x != tuple.x) return false;
        return (new Double(y).equals(new Double(tuple.y)));
//     return Double.compare(tuple.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + new Double(y).hashCode();
        return result;
    }
    public static void main(String[] args) {
        Tuple tuple1 = new Tuple(100, 23);
        Tuple tuple2 = new Tuple(200, 33);
        assertTrue(tuple1.hashCode() == 1077349404, "tuple1.hashCode()==1077349404");
        assertTrue(tuple2.hashCode() == 1077975096, "tuple2.hashCode()==1077975096");
        assertTrue(tuple1.equals(tuple1), "tuple1.equals(tuple1)");
        assertTrue(tuple2.equals(tuple2), "tuple2.equals(tuple2)");
        assertTrue(!tuple1.equals(tuple2), "!tuple1.equals(tuple2)");
        System.out.println(tuple1);
        assertTrue(tuple1.toString().equals("Tuple(100, 23.0)"), "tuple1.toString()==\"Tuple(100, 23.0)\"");
    }

    public static void assertTrue(boolean b, String msg) {
        if (!b) System.out.println(msg + " not true");
    }

    private final int x;
    private final double y;
}
