package main.java.layout;

import main.java.utils.GeometryUtils;
import main.java.geometry.Point;
import main.java.geometry.Rectangle;
import main.java.layout.ForceDirectedOverlapRemoval;
import main.java.layout.ForceDirectedUniformity;
import main.java.nlp.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ContextPreservingStrategy extends BaseLayoutStrategy {
	
    private static final double EPS = 1e-6;
    private static final double KA = 15;
    private static final double KR = 1000;
    private static final double TOTAL_ITERATIONS = 1000;
    private double T = 1;
    private int numIterations = 0;

    public ContextPreservingStrategy() {super();}

    @Override
    protected void execute() {
    	
        //initial layout
        LayoutResult initialLayout = new MDSStrategy().layout(wordGraph); 
        IntStream.range(0,words.size()).forEach(i -> wordPositions.add(initialLayout.getWordPosition(words.get(i))));
        IntStream.range(0,words.size()).forEach(i -> wordPositionsMap.put(words.get(i),initialLayout.getWordPosition(words.get(i))));
        
        if(numIterations!=0) runForceDirected(lastResult);
        
        //compute Delaunay
        delaunayEdges = computeDelaunay();

        runForceDirected();

        numIterations++; 
    }

	int[][] delaunayEdges;

    /**
     * TODO: the format is terrible
     */
    private int[][] computeDelaunay() {
        List<Point> points2D = new ArrayList<>(words.size());
        List<List<Integer>> edges = new ArrayList<List<Integer>>();
        GeometryUtils.computeDelaunayTriangulation(words, wordPositions, points2D, edges);

        Point[] points = new Point[words.size()];
        
        for(int i=0;i<words.size();i++)
            points[i] = new Point(points2D.get(i).getX(),points2D.get(i).getY());

        int res[][] = new int[words.size()][];
        for(int i=0;i<words.size();i++)  {
            List<Integer> arr = new ArrayList<Integer>();

            List<Integer> neighborsI = edges.get(i);
            for(int j=0;j<neighborsI.size();j++) {
                List<Integer> neighborsJ = edges.get(neighborsI.get(j));

                List<Integer> commonNeighbors = new ArrayList<Integer>(neighborsI);
                commonNeighbors.retainAll(neighborsJ);

                Point pi = points2D.get(i);
                Point pj = points2D.get(neighborsI.get(j));
                for(int k=0;k<commonNeighbors.size();k++) {
                    Point pk = points2D.get(commonNeighbors.get(k));

                    if (orientation(pi,pj,pk) >= 0)  {
                        arr.add(neighborsI.get(j));
                        arr.add(commonNeighbors.get(k));
                    }
                    else  {
                        arr.add(commonNeighbors.get(k));
                        arr.add(neighborsI.get(j));
                    }
                }
            }

            res[i] = new int[arr.size()];
            for(int j=0;j<arr.size();j++) res[i][j] = arr.get(j);
        }
        return res;
    }

    private int orientation(Point pi,Point pj,Point pk) {return GeometryUtils.Cross(pi,pj,pk);}

    private void runForceDirected() {
    	int numIterations = 0;
        while(numIterations++ < TOTAL_ITERATIONS)  {
            if(numIterations % 1000 == 0) System.out.println("Finished Iteration " + numIterations);

            if(!doIteration()) break;

            //cooling down the temperature (max allowed step is decreased)
            if(numIterations % 5 == 0) T *= 0.95;
        }
		new ForceDirectedOverlapRemoval<>(0.25).run(wordPositions);
	    new ForceDirectedUniformity<>(0.3).run(wordPositions);
    }
    
    private void runForceDirected(LayoutResult lastResult) {

		int numIterations = 0;
//		doIteration(lastResult);
		while(numIterations++ < TOTAL_ITERATIONS)  {
	        if(numIterations % 1000 == 0) System.out.println("Finished Iteration: " + numIterations);
	        if(!doIteration(lastResult)) break;

	        //cooling down the temperature (max allowed step is decreased)
	        if(numIterations % 5 == 0) T *= 0.95;
	    }
//		new ForceDirectedOverlapRemoval<>(0.15).run(wordPositions);
//	    new ForceDirectedUniformity<>(0.1).run(wordPositions);
	}

	/**
     * perform several iterations
     * returns 'true' iff the last iteration moves rectangles 'alot'
     */
    public boolean doIteration(int iters) {
        int i = 0;
        while(i++ < iters) {
            if(!doIteration()) return false;

            if(i%5 == 0) T *= 0.95;
        }
        return true;
    }
    
    private boolean doIteration(LayoutResult lastResult) {
    	
    	double avgStep = 0;
    	
		List<Word> lastWords = lastResult.getWords();
		Map<Word,Rectangle> lastWordPositions = lastResult.getWordPositionMap();

		List<Word> commonWords = new ArrayList<>(words);
		commonWords.retainAll(lastWords);
		
		double maxScore = computeMaxScore(commonWords);
		Rectangle bb = computeBoundingBox();
		
		// compute attractive force between the common words of the two layouts
		for(Word w:commonWords) {
			
			Rectangle rect = wordPositionsMap.get(w);
			Rectangle lastRect = lastWordPositions.get(w);
			
            Point dxy = new Point();
            dxy.add(computeAttractiveForce(bb,w,rect,lastRect,maxScore));

            // move the rectangle
            rect.setRect(rect.getX()+dxy.getX(),rect.getY()+dxy.getY(),rect.getWidth(),rect.getHeight());

            assert (!Double.isNaN(rect.getX()));
            assert (!Double.isNaN(rect.getY()));
            avgStep += dxy.computeDistance(new Point());
		}
		avgStep /= commonWords.size();
        return avgStep > Math.max(bb.getWidth(),bb.getHeight())/10000.0;
	}

	/**
     * perform one iteration
     * returns 'true' iff the iteration moves rectangles 'alot'
     */
    private boolean doIteration() {
    	
        double maxScore = computeMaxScore(words);
        Rectangle bb = computeBoundingBox();

        double avgStep = 0;
        
        // compute the displacement for the word in this time step
        for(int i=0;i<words.size();i++) {
            Rectangle rect = wordPositions.get(i);
            Point dxy = new Point();

            if(!overlap(i)) {
                //attractive force (compact principle)
                dxy.add(computeAttractiveForce(bb,i,rect,maxScore));
                //repulsion force (planar principle)
                dxy.add(computePlanarForce(bb,i,rect));
            }
            else {
                //repulsion force (removing overlaps)
                dxy.add(computeRepulsiveForce(bb,i,rect));
                //repulsion force (planar principle)
                dxy.add(computePlanarForce(bb,i,rect));
            }

            // move the rectangle
            dxy = normalize(dxy,bb);
            rect.setRect(rect.getX() + dxy.getX(),rect.getY() + dxy.getY(),rect.getWidth(),rect.getHeight());

            assert (!Double.isNaN(rect.getX()));
            assert (!Double.isNaN(rect.getY()));
            avgStep += dxy.computeDistance(new Point());
        }

        avgStep /= words.size();
        return avgStep > Math.max(bb.getWidth(),bb.getHeight())/10000.0;
    }
    
    private Point computeAttractiveForce(Rectangle bb,Word w,Rectangle rect,Rectangle lastRect,double maxScore) {
    	
    	Point dxy = new Point();     
        double k = Math.max(w.getScore(),maxScore/5.0);     
        
        // distance between the two rectangles
        Point dir = new Point(lastRect.getCenterX()-rect.getCenterX(),lastRect.getCenterY()-rect.getCenterY()); 

//        double force = Math.pow(2,k);
//        double force = k*k;
        double force = Math.sqrt(k)*k;
//        double force = k;

    	dir.normalize();
        dir.scale(force); 
        dxy.add(dir);
        return dxy;
	}
    
    private Point computeAttractiveForce(Rectangle bb,int i,Rectangle rectI,double maxWeight) {
        Point dxy = new Point();

        double cnt = 0;
        for(int j=0;j<words.size();j++) {
            if(i == j) continue;
            
            Rectangle rectJ = wordPositions.get(j);

            double wi = Math.max(words.get(i).getScore(), maxWeight / 5.0);
            double wj = Math.max(words.get(j).getScore(), maxWeight / 5.0);
            double dist = GeometryUtils.rectToRectDistance(rectI,rectJ);
            double force = wi * wj * dist / (maxWeight * maxWeight);
            if(T < 0.5) force *= T;

            Point dir = new Point(rectJ.getCenterX() - rectI.getCenterX(), rectJ.getCenterY() - rectI.getCenterY());

            double len = dir.computeDistance(new Point());
            
            if(len<EPS) continue;

            dir.normalize();
            dir.scale(force);

            dxy.add(dir);
            cnt++;
        }

        if (cnt == 0.0)
            cnt = 1;
        dxy.scale(1.0 / cnt);
        return dxy;
    }

    private Point computeRepulsiveForce(Rectangle bb,int i,Rectangle rectI) {
        
    	Point dxy = new Point();

        double cnt = 0;
        
        for(int j=0;j<words.size();j++) {
            if(i == j) continue;

            // compute the displacement due to the overlap repulsive force
            Rectangle rectJ = wordPositions.get(j);

            if(rectI.intersection(rectJ,0.0)) {
            	
                double hix = Math.min(rectI.getMaxX(), rectJ.getMaxX());
                double lox = Math.max(rectI.getMinX(), rectJ.getMinX());
                double hiy = Math.min(rectI.getMaxY(), rectJ.getMaxY());
                double loy = Math.max(rectI.getMinY(), rectJ.getMinY());
                double dx = hix - lox; // hi > lo
                double dy = hiy - loy;
                assert (dx >= -EPS);
                assert (dy >= -EPS);

                double force = KR * Math.min(dx, dy);

                Point dir = new Point(rectJ.getCenterX() - rectI.getCenterX(), rectJ.getCenterY() - rectI.getCenterY());

                double len = dir.computeDistance(new Point());
                if(len < EPS)  continue;

                dir.normalize();
                dir.scale(force);

                dxy.subtract(dir);
                cnt++;
            }
        }

        if(cnt == 0.0) cnt = 1;
        dxy.scale(1.0 / cnt);
        
        return dxy;
    }

    private Point computePlanarForce(Rectangle bb,int i,Rectangle rectI) {
        Point dxy = new Point(0, 0);

        double cnt = 0;
        for(int t=0;t<delaunayEdges[i].length;t += 2) {
        	
            int j = delaunayEdges[i][t];
            int k = delaunayEdges[i][t+1];

            Point pi = getCenter(i);
            Point pj = getCenter(j);
            Point pk = getCenter(k);

            if(orientation(pi,pj,pk) < 0) {
            	
                Point force = planarForce(pi, pj, pk);
                force.scale(KA);

                dxy.add(force);
                cnt++;
            }
        }

        if (cnt > 0)
            dxy.scale(1.0 / cnt);
        return dxy;
    }

    private Point planarForce(Point pi,Point pj,Point pk) {
        double dx = pk.getX() - pj.getX();
        double dy = pk.getY() - pj.getY();

        double dist = GeometryUtils.pointToLineDistance(new Point(pj),new Point(pk),new Point(pi));

        Point norm = new Point(-dy, dx);
        norm.scale(dist);

        return norm;
    }

    private Point getCenter(int index) {return wordPositions.get(index).getCenter();}

    private boolean overlap(int i) {
        Rectangle rectI = wordPositions.get(i);
        for(int j=0;j<words.size();j++)
            if(i != j) {
                Rectangle rectJ = wordPositions.get(j);
                if(rectI.intersection(rectJ,1.0)) return true;
            }
        return false;
    }

    private Point normalize(Point force,Rectangle bb) {
        //maximum allowed movement is 2% of the screen width/height
        double mx = Math.min(bb.getWidth(), bb.getHeight());
        double len = force.length();
        
        if(len<1e-3) return force;

        double maxLen = Math.min(len,T*mx/50.0);
        force.scale(maxLen/len);
        return force;
    }

    private Rectangle computeBoundingBox() {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for(Rectangle rect:wordPositions) {
            minX = Math.min(minX,rect.getMinX());
            maxX = Math.max(maxX,rect.getMaxX());
            minY = Math.min(minY,rect.getMinY());
            maxY = Math.max(maxY,rect.getMaxY());
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    private double computeMaxScore(List<Word> words){
        double maxScore = 0;
        for(int i=0;i<words.size();i++) {
            maxScore = Math.max(maxScore,words.get(i).getScore());
            assert (words.get(i).getScore() > 0.001);
        }
        return maxScore;
    }

    public List<Rectangle> getDelaunay() {
        List<Rectangle> res = new ArrayList<Rectangle>();
        for(int i=0;i<words.size();i++) {
            for(int t=0;t<delaunayEdges[i].length;t++) {
                res.add(wordPositions.get(i));
                res.add(wordPositions.get(delaunayEdges[i][t]));
            }
        }
        return res;
    }

}


