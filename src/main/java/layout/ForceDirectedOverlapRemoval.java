package main.java.layout;

import java.util.List;

import main.java.geometry.Point;
import main.java.geometry.Rectangle;

public class ForceDirectedOverlapRemoval<T extends Rectangle> implements OverlapRemoval<T> {
	
    private static double KR;
    private int MAX_ITER = 1000;

    public ForceDirectedOverlapRemoval(double KR)  {
    	this.KR=KR;
    }

    public ForceDirectedOverlapRemoval(int maxIterations) {MAX_ITER = maxIterations;}

    @Override
    public void run(List<T> rect)  {
        int n = rect.size();
        int iter = 0;
        while (overlaps((List<Rectangle>) rect) && iter++ < MAX_ITER) {
            
        	// compute the displacement for all words in this time step
            for(int i=0;i<n;i++) {
                Point dxy = new Point();
                double cnt = 0;

                for(int j=0;j<n;j++) {
                    
                	if(i==j)  continue;
                	
                    if(rect.get(i).getMaxX()-1 < rect.get(j).getMinX())  continue;
                    if(rect.get(i).getMinX()+1 > rect.get(j).getMaxX())  continue;
                    if(rect.get(i).getMaxY()-1 < rect.get(j).getMinY())  continue;
                    if(rect.get(i).getMinY()+1 > rect.get(j).getMaxY())  continue;

                    //if (rect[i].intersects(rect[j])) {
                    Point dir = computeForceIJ(rect.get(i), rect.get(j));
                    dxy.subtract(dir);
                    cnt++;
                    //}
                }

                if(cnt==0)  continue;

                // move the word
                assert(!Double.isNaN(dxy.getX()));
                assert(!Double.isNaN(dxy.getY()));

                dxy.scale(1.0/cnt);
                rect.get(i).setRect(rect.get(i).getX() + dxy.getX(), rect.get(i).getY() + dxy.getY(), 
                		rect.get(i).getWidth(), rect.get(i).getHeight());
            }
        }
    }

    private Point computeForceIJ(Rectangle rectI,Rectangle rectJ) {
    	
        double hix = Math.min(rectI.getMaxX(),rectJ.getMaxX());
        double lox = Math.max(rectI.getMinX(),rectJ.getMinX());
        double hiy = Math.min(rectI.getMaxY(),rectJ.getMaxY());
        double loy = Math.max(rectI.getMinY(),rectJ.getMinY());
        double dx = (hix-lox)*1.1; // hi > lo
        double dy = (hiy-loy)*1.1;
        assert(dx >= -1e-5);
        assert(dy >= -1e-5);

        // use force-directed to get rid of overlaps
        // f(a,b) = kr * min{dx, dy}
        double force = KR * Math.min(dx, dy);
        Point dir = new Point(rectJ.getCenterX()-rectI.getCenterX(),rectJ.getCenterY()-rectI.getCenterY());
        double len = dir.length();
        
        if(len < 1e-5) dir = Point.randomPoint();
        else           dir.scale(1.0/len);

        dir.scale(force);
        return dir;
    }

    public static boolean overlaps(List<Rectangle> pos)  {
        for(int i=0;i<pos.size();i++)
            for(int j=i+1;j<pos.size();j++) {
                if(pos.get(i).intersection(pos.get(j),1.0))  return true;
            }
        return false;
    }
}
