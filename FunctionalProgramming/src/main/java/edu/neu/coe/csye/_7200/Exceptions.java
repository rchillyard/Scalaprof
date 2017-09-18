package edu.neu.coe.csye._7200;

/**
 * Created by scalaprof on 9/15/16.
 */
public class Exceptions {

    /**
     * Try to return the current (odd) time in milliseconds
     * @throws RuntimeException if time is even
     */
    public static Long getTime() {
        long l = System.currentTimeMillis();
        if (l % 2 == 0)
            throw new RuntimeException("time was even");
        else
            return l;
    }

    public static void main(String[] args) {
        try {
            Long time = getTime();
            System.out.println(time);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
