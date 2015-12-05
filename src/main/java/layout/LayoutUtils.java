package main.java.layout;

public class LayoutUtils {
    public static double idealDistanceConverter(double similarity) {
        double s = 0.05;
        double d = -Math.log((1.0 - s) * similarity + s);
        return Math.max(d, 0.0);
    }
}
