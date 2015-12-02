package main.java.layout;

import java.util.ArrayList;
import java.util.List;

import main.java.geometry.Point;
import main.java.geometry.Rectangle;

public class ForceDirectedUniformity<T extends Rectangle> implements OverlapRemoval<T> {
    private static double SCALE_FACTOR = 0.1;
    private static double KR;
    private int MAX_ITER = 500;

    public ForceDirectedUniformity(double KR)  {
    	this.KR=KR;
    }

    public ForceDirectedUniformity(int maxIterations) {MAX_ITER = maxIterations;}

    @Override
    public void run(List<T> wordPositions)  {
        int iter = 0;

        int n = wordPositions.size();
        List<Rectangle> rect = new ArrayList<>(n);
        double maxHeight = wordPositions.stream().mapToDouble(pos -> pos.getHeight()).max().orElse(0.0);
        double delta = maxHeight * SCALE_FACTOR;

        for(int i=0;i<n;i++)  {
        	Rectangle r = new Rectangle(wordPositions.get(i).getX(),wordPositions.get(i).getY(),wordPositions.get(i).getWidth(),wordPositions.get(i).getHeight());
            rect.add(r);
            rect.get(i).setWidth(rect.get(i).getWidth() + delta);
            rect.get(i).setHeight(rect.get(i).getHeight() + delta);
        }

        while(overlaps(rect) && iter++ < MAX_ITER)  {
            // compute the displacement for all words in this time step
            for(int i=0;i<n;i++) {
                Point dxy = new Point();
                double cnt = 0;

                for(int j=0;j<n;j++) {
                    
                	if(i==j) continue;
                	
                    if(rect.get(i).getMaxX()-1 < rect.get(j).getMinX())  continue;
                    if(rect.get(i).getMinX()+1 > rect.get(j).getMaxX())  continue;
                    if(rect.get(i).getMaxY()-1 < rect.get(j).getMinY())  continue;
                    if(rect.get(i).getMinY()+1 > rect.get(j).getMaxY())  continue;

//                    if(rect.get(i).intersection(rect.get(j),1.0)) {
                    	Point dir = computeForceIJ(rect.get(i),rect.get(j));
                    	dxy.subtract(dir);
                    	cnt++;
//                    }
                }

                if(cnt==0)  continue;

                // move the word
                assert (!Double.isNaN(dxy.getX()));
                assert (!Double.isNaN(dxy.getY()));

                dxy.scale(1.0/cnt);
                rect.get(i).setRect(rect.get(i).getX() + dxy.getX(), rect.get(i).getY() + dxy.getY(), 
                		rect.get(i).getWidth(), rect.get(i).getHeight());
            }
        }

        for(int i=0;i<n;i++) {
        	wordPositions.get(i).setX(rect.get(i).getX());
            wordPositions.get(i).setY(rect.get(i).getY());
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
        double force = KR * Math.min(dx,dy);
        Point dir = new Point(rectJ.getCenterX()-rectI.getCenterX(),rectJ.getCenterY()-rectI.getCenterY());
        double len = dir.length();
        
        if(len < 1e-5)  dir = Point.randomPoint();
        else  			dir.scale(1.0/len);

        dir.scale(force);
        return dir;
    }

    private boolean overlaps(List<Rectangle> pos)  {return ForceDirectedOverlapRemoval.overlaps(pos);}
}
