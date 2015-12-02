package main.java.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import main.java.geometry.Point;
import main.java.geometry.Rectangle;
import main.java.nlp.Word;

public class GeometryUtils {
	
	public static int Cross(Point p0, Point p1, Point p2) {
        double cr = (p1.getX() - p0.getX()) * (p2.getY() - p0.getY()) - (p2.getX() - p0.getX()) * (p1.getY() - p0.getY());
        if(cr>1e-8)  return 1;
        if(cr<-1e-8) return -1;
        return 0;
    }
    public static List<Point> computeConvexHull(List<Point> points) {
        int n = points.size();

        Point[] p = points.toArray(new Point[points.size()]);
        int minI = 0;
        for(int i=1;i<n;i++)
            if(p[i].getY()<p[minI].getY() || p[i].getY()==p[minI].getY() && p[i].getX()<p[minI].getX())
                minI = i;

        final Point p0 = p[minI];
        p[minI] = p[0];
        p[0] = p0;

        Arrays.sort(p,1,n, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                int cr = Cross(p0, p1, p2);
                if (cr != 0) return -cr;
                Double d1 = p0.computeDistance(p1);
                Double d2 = p0.computeDistance(p2);
                return d1.compareTo(d2);
            }
        });

        Point[] hull = new Point[n];
        hull[0] = p[0];
        hull[1] = p[1];
        int top = 2;
        for (int i=2;i<n;i++) {
            while (top >= 2 && Cross(hull[top - 2],hull[top - 1],p[i])<0) top--;
            if(top>=2 && Cross(hull[top-2], hull[top-1],p[i])==0) top--;
            hull[top++] = p[i];
        }

        List<Point> res = new ArrayList<Point>();
        for (int i=0; i<top;i++) res.add(hull[i]);
        return res;
    }

    /**
     * Computes DelaunayTriangulation
     * input: words + wordPositions
     * result: points + edges
     * @param l2 Line point 2
     * @param x The point to get the distance of
     * @return Distance of x from l1->l2
     */
    public static void computeDelaunayTriangulation(List<Word> words,List<Rectangle> wordPositions,List<Point> points, List<List<Integer>> edges) {
        IntStream.range(0,words.size()).forEach(i -> edges.add(i, new ArrayList<Integer>()));
 
        // System.out.println("Triangulation");
        for(int i=0;i<words.size();i++) {
            Rectangle temp = wordPositions.get(i);
            points.add(new Point(temp.getCenterX(),temp.getCenterY()));
        }
        // determine if i-j-k is a circle with no interior points
        for(int i=0;i<words.size();i++) {
            for (int j=i+1;j<words.size();j++) {
                for (int k=j+1;k<words.size();k++) {
                    boolean isTriangle = true;
                    for(int a=0;a<words.size();a++) {
                        if (a==i || a==j || a==k) continue;
                        if (pointInsideCircle(points.get(a),points.get(i),points.get(j),points.get(k))) {
                            isTriangle = false;
                            break;
                        }
                    }
                    if(isTriangle) {
                        if (!edges.get(i).contains(j)) edges.get(i).add(j);
                        if (!edges.get(i).contains(k)) edges.get(i).add(k);
                        if (!edges.get(j).contains(i)) edges.get(j).add(i);
                        if (!edges.get(j).contains(k)) edges.get(j).add(k);
                        if (!edges.get(k).contains(i)) edges.get(k).add(i);
                        if (!edges.get(k).contains(j)) edges.get(k).add(j);
                    }
                }
            }
        }
    }
    public static double computeArea(List<Point> points) {
        double a=0,b=0;
        int n = points.size();
        for(int i=0; i<n;++i) {
            a += points.get(i).getX()*points.get((i+1) % n).getY();
            b += points.get(i).getY()*points.get((i+1) % n).getX();
        }
        return (a-b)/2;
    }

    // Is p inside the circle formed by triangle a-b-c?
    public static boolean pointInsideCircle(Point p,Point a,Point b,Point c) {
        //find center
        Point bb = new Point(b.getX()-a.getX(),b.getY()-a.getY());
        Point cc = new Point(c.getX()-a.getX(),c.getY()-a.getY());

        double delta = 2.0 * (bb.getX()*cc.getY() - bb.getY()*cc.getX());
        if (Math.abs(delta)<1e-6) return false;

        double bdot = bb.getX()*bb.getX() + bb.getY()*bb.getY();
        double cdot = cc.getX()*cc.getX() + cc.getY()*cc.getY();
        double delta1 = cc.getY()*bdot - bb.getY()*cdot;
        double delta2 = bb.getX()*cdot - cc.getX()*bdot;

        Point cen = new Point(a.getX() + delta1/delta, a.getY() + delta2/delta);
        return cen.computeDistance(p) <= cen.computeDistance(a);
    }

    /**
     * Computes the distance of point x from the line through l1, l2
     * @param l1 Line point 1
     * @param l2 Line point 2
     * @param x The point to get the distance of
     * @return Distance of x from l1->l2
     */
    public static double pointToLineDistance(Point l1,Point l2,Point x) {
        Point bc = new Point(l2.getX(),l2.getY());
        bc.setX(bc.getX()-l1.getX());
        bc.setY(bc.getY()-l1.getY());
        Point ba = new Point(x.getX(),x.getY());
        ba.setX(ba.getX()-l1.getX());
        ba.setY(ba.getY()-l1.getY());

        double c1 = Point.VectorProduct(bc,ba);
        double c2 = Point.VectorProduct(bc,bc);
        double parameter = c1/c2;
        Point res = new Point(ba.getX(),ba.getY());
        bc.scale(parameter);
        res.setX(res.getX()-bc.getX());
        res.setY(res.getY()-bc.getY());
        return res.originDistance();

        /*double normalLength = Math.hypot((l2.x() - l1.x()), (l2.y() - l1.y()));
        if (normalLength == 0) {
        	normalLength = 0.00001;
        }
        return Math.abs((x.x() - l1.x()) * (l2.y() - l1.y()) - (x.y() - l1.y()) * (l2.x() - l1.x())) / normalLength;*/
    }

    /**
     * Computes the distance of point x from the line through l1, l2
     * @param l1 Line point 1
     * @param l2 Line point 2
     * @param x The point to get the distance of
     * @return Distance of x from l1->l2
     */
    public static double rectToRectDistance(Rectangle rect1,Rectangle rect2) {
        double dx = difference(rect1.getMinX(),rect1.getMaxX(),rect2.getMinX(),rect2.getMaxX());
        double dy = difference(rect1.getMinY(),rect1.getMaxY(),rect2.getMinY(),rect2.getMaxY());

        assert(dx>=0);
        assert(dy>=0);

        return Math.sqrt(dx*dx + dy*dy);
    }
    private static double difference(double m1,double M1,double m2,double M2) {
        if(M1<=m2) return m2-M1;
        if(M2<=m1) return m1-M2;
        //return Math.min(M1, M2) - Math.max(m1, m2);
        return 0;
    }
}
