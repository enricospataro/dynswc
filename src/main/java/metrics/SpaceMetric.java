package main.java.metrics;

import main.java.geometry.Point;
import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;
import main.java.nlp.Word;
import main.java.utils.GeometryUtils;

import java.util.ArrayList;
import java.util.List;

public class SpaceMetric implements OverallMetric{
    //true => use convex hull
    //false => use bounding box
    boolean useConvexHull;

    public SpaceMetric(boolean useConvexHull) {
        this.useConvexHull = useConvexHull;
    }
    
    @Override
    public double getValue(List<WordGraph> wordGraphs,List<LayoutResult> layouts) {
    	double ratio = 0.0;
    	int n = layouts.size();
    	
    	for(int i=0;i<n;i++) {
    		double area = computeTotalArea(wordGraphs.get(i).getWords(),layouts.get(i));  //System.out.println("A "+area);
    		double usedArea = computeUsedArea(wordGraphs.get(i).getWords(),layouts.get(i)); //System.out.println("UA "+usedArea);
        //assert (usedArea <= area);
    		ratio += usedArea/area;
    	}
        return 1 - ratio/n;
    }

    private double computeTotalArea(List<Word> words, LayoutResult algo) {
        if(!useConvexHull)  {
            Rectangle boundingBox = computeBoundingBox(words, algo);
            return boundingBox.getHeight() * boundingBox.getWidth();
        } else {
            List<Point> allPoints = extractPoints(words, algo);
            List<Point> convexHull = GeometryUtils.computeConvexHull(allPoints);
            return GeometryUtils.computeArea(convexHull);
        }
    }

    private List<Point> extractPoints(List<Word> words,LayoutResult algo) {
        List<Point> points = new ArrayList<Point>();
        for(Word w:words) {
            Rectangle rect = algo.getWordPosition(w);
            points.add(new Point(rect.getMinX(), rect.getMinY()));
            points.add(new Point(rect.getMaxX(), rect.getMinY()));
            points.add(new Point(rect.getMinX(), rect.getMaxY()));
            points.add(new Point(rect.getMaxX(), rect.getMaxY()));
        }
        return points;
    }

    public static Rectangle computeBoundingBox(List<Word> words,LayoutResult algo){
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for(Word w:words) {
            Rectangle rect = algo.getWordPosition(w);
            if(rect != null) {
                minX = Math.min(minX, rect.getMinX());
                maxX = Math.max(maxX, rect.getMaxX());
                minY = Math.min(minY, rect.getMinY());
                maxY = Math.max(maxY, rect.getMaxY());
            }
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);

        /*SWCRectangle bb = null;
        for (Word w : words) {
        	SWCRectangle rect = algo.getWordRectangle(w);
        	if (bb == null)
        		bb = new SWCRectangle(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        	else
        		bb.add(rect);
        }

        return bb;*/
    }

    public static double computeUsedArea(List<Word> words,LayoutResult algo) {
        double res = 0;
        for(Word w:words)  {
            Rectangle rect = algo.getWordPosition(w);
            res += rect.getHeight() * rect.getWidth();
        }
        return res;
    }
}
