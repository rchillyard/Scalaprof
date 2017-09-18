package edu.neu.coe.csye._7200;

/**
 * Created by scalaprof on 9/15/16.
 */
public class Nulls {

    /**
     * Optionally return the current time in milliseconds
     * @return current time if it's odd; otherwise we return null
     */
    public static Long getTime() {
        long l = System.currentTimeMillis();
        if (l % 2 == 0)
            return null;
        else
            return l;
    }

    public static void main(String[] args) {
        Long time = getTime();
        if (time != null)
            System.out.println(time);
        else
            System.err.println("cannot get time");
    }
}
