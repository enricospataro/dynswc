package main.java.geometry;

import java.util.Random;

public class Point {
	
	private static final Random rand = new Random();
	private double x;
	private double y;
	
	public Point(double x,double y) {
		this.x=x;
		this.y=y;
	}
	public Point() {
		this.x=0;
		this.y=0;
	}
	public Point(Point p) {
		this.x=p.getX();
		this.y=p.getY();
	}
	public static Point randomPoint() {return new Point(-1 + 2*rand.nextDouble(), -1 + 2*rand.nextDouble());}
	
	public double getX() {return x;}
	public void setX(double x) {this.x = x;}
	public double getY() {return y;}
	public void setY(double y) {this.y = y;}
	
	public void scale(double s) {
    	setX(x*s);
    	setY(y*s);
    }
	public String toString() {return "("+x+","+"("+y+")";}
	
	// return signed area of triangle a->b->c
    public static double area(Point a,Point b,Point c) {
    	return 0.5*(a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y);
    }
    // is a->b->c a counterclockwise turn
    // +1 (yes), -1 (no), 0 (collinear)
    public static int ccw(Point a,Point b,Point c) {
        double area = area(a, b, c);
        if(area>0) return +1;
        else if(area<0) return -1;
        else return 0;
    }
    // is invoking point inside circle defined by a-b-c
    // if circle is degenerate, return true
    public boolean inside(Point a,Point b,Point c) {
        if(ccw(a,b,c) > 0) return (in(a,b,c)>0);
        else if(ccw(a,b,c) < 0) return (in(a,b,c)<0);
        return true;
    }
 // return positive, negative, or zero depending on whether
    // invoking point is inside circle defined by a, b, and c
    // assumes a-b-c are counterclockwise
    private double in(Point a,Point b,Point c) {
        Point d = this;
        double adx = a.x - d.x;
        double ady = a.y - d.y;
        double bdx = b.x - d.x;
        double bdy = b.y - d.y;
        double cdx = c.x - d.x;
        double cdy = c.y - d.y;

        double abdet = adx * bdy - bdx * ady;
        double bcdet = bdx * cdy - cdx * bdy;
        double cadet = cdx * ady - adx * cdy;
        double alift = adx * adx + ady * ady;
        double blift = bdx * bdx + bdy * bdy;
        double clift = cdx * cdx + cdy * cdy;

        return alift * bcdet + blift * cadet + clift * abdet;
    }
    public double computeDistance(Point p) {
    	double dx = this.x-p.getX();
    	double dy = this.y-p.getY();
    	return Math.sqrt(dx*dx + dy*dy);
    }
    public double computeSquaredDistance(Point p) {
    	double dx = this.x-p.getX();
    	double dy = this.y-p.getY();
    	return dx*dx + dy*dy;
    }
    public double originDistance() {return computeDistance(new Point());}
    public double originSquaredDistance() {return computeSquaredDistance(new Point());} 
    public void normalize() {
    	double length=originDistance();
    	if(length<1e-6) return;
    	setX(x/length);
    	setY(y/length);
    }
    public void add(Point p) {
        x += p.x;
        y += p.y;
    }
    public void subtract(Point p) {
        x -= p.x;
        y -= p.y;
    }
    public double length() {return computeDistance(new Point());}
    public static double VectorProduct(Point u, Point v) {return u.x*v.x + u.y*v.y;}
}
