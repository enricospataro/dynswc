package main.java.layout.clusters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.geometry.BoundingBox;
import main.java.geometry.Point;
import main.java.geometry.Rectangle;
import main.java.layout.ForceDirectedOverlapRemoval;
import main.java.layout.LayoutResult;
import main.java.layout.LayoutUtils;
import main.java.layout.WordGraph;
import main.java.layout.mds.DistanceScaling;
import main.java.nlp.Word;
import main.java.nlp.WordPair;
import mdsj.MDSJ;

/**
 * @author spupyrev
 * May 3, 2013
 */
public class ClusterForceDirectedPlacer implements WordPlacer {
	
    private static final double EPS = 1e-6;
    //private static final double KA = 15;
    private static final double KR = 500;
    private static final double TOTAL_ITERATIONS = 500;
    private double T = 1;


    private WordGraph wordGraph;
    private List<Word> words;
    private Map<WordPair, Double> similarities;
    private Map<Word,Rectangle> wordPositions = new HashMap<>();
    private List<? extends LayoutResult> singlePlacers;
    private LayoutResult lastResult;

    private BoundingBox bb;

    public ClusterForceDirectedPlacer(WordGraph wordGraph, List<? extends LayoutResult> singlePlacers, BoundingBox bb,LayoutResult lastResult)     {
        this.wordGraph = wordGraph;
        this.words = wordGraph.getWords();
        this.similarities = wordGraph.getSimilarity();
        this.singlePlacers = singlePlacers;
        this.bb = bb;
        this.lastResult=lastResult;

        run();
    }

    @Override
    public Rectangle getRectangleForWord(Word w) {
        assert(wordPositions.containsKey(w));
        return wordPositions.get(w);
    }

    @Override
    public boolean contains(Word w) {return true;}

    private List<Cluster> clusters;

    private void run()  {                
        //get the groups of words: stars, cycles etc
        clusters = createClusters();
        
        initialPlacement();
        
        if(numIterations!=0) runForceDirected(lastResult);
        
        runForceDirected();

        restoreWordPositions();
    }

    private List<Cluster> createClusters() {
        List<Cluster> result = new ArrayList<Cluster>();
        for(int i=0;i<singlePlacers.size();i++) result.add(new Cluster());
 
        for(Word w:words) {
            Rectangle rect = null;
            for(int i=0;i<singlePlacers.size();i++) {
                Rectangle tmp = singlePlacers.get(i).getWordPositionMap().get(w);
//                Rectangle tmp = singlePlacers.get(i).getWordPosition(w); // perchè non funziona???
                if(tmp != null)  {                	
                    result.get(i).wordPositions.put(w,tmp);
                    rect = tmp;
                    break;
                }
            }

            //create its own cluster
            if(rect==null)  {
                Cluster c = new Cluster();
                c.wordPositions.put(w,bb.getBoundingBox(w));
                result.add(c);
            }
        }
        return result;
    }
    
    //run MDS
    private void initialPlacement(){
        double maxWordSize = 0;
        double[][] desiredDistance = new double[clusters.size()][clusters.size()];
        for(int i=0;i<clusters.size();i++) {
            desiredDistance[i][i] = 0;
            for(Rectangle rect:clusters.get(i).wordPositions.values())
                maxWordSize = Math.max(maxWordSize,rect.getWidth());    
        }

        double SCALING = 1.0;
        for(int i=0;i<clusters.size();i++)
            for(int j=i+1;j<clusters.size();j++) {
                double avgSim = 0, cnt = 0;
                
                //computing average similarity between clusters
                for(Word wi:clusters.get(i).wordPositions.keySet())
                    for(Word wj:clusters.get(j).wordPositions.keySet()) {
                        WordPair wp = new WordPair(wi,wj);
                        if(similarities.containsKey(wp)) avgSim += similarities.get(wp);
                        
                        cnt++;
                    }
                avgSim /= cnt;
            
                desiredDistance[i][j] = desiredDistance[j][i] = 
                		LayoutUtils.idealDistanceConverter(avgSim) * maxWordSize * SCALING;

            }    	

        //apply MDS
        double[][] outputMDS = new DistanceScaling().mds(desiredDistance,2);
        
        //set coordinates
        for(int i=0;i<clusters.size();i++) {
            double x = outputMDS[0][i];
            double y = outputMDS[1][i];
            clusters.get(i).center = new Point(x,y);

            assert(!Double.isNaN(x) && !Double.isNaN(y));
        }
    }

    private void runForceDirected() {
        int numIterations = 0;

        while(numIterations++ < TOTAL_ITERATIONS)  {
            if(numIterations % 1000 == 0)  System.out.println("Finished Iteration " + numIterations);

            if(!doIteration())  break;

            //cooling down the temperature (max allowed step is decreased)
            if (numIterations % 5 == 0)
                T *= 0.95;
        }
    }

    int ni = 0;

    /**
     * perform several iterations
     * returns 'true' iff the last iteration moves rectangles 'alot'
     */
    public boolean doIteration(int iters) {
        int i = 0;
        while(i++ < iters) {
            ni++;
            if(!doIteration())  return false;

            if(ni % 5 == 0)  T *= 0.95;
        }
        restoreWordPositions();
        return true;
    }

    /**
     * perform one iteration
     * returns 'true' iff the iteration moves rectangles 'alot'
     */
    private boolean doIteration() {
        Rectangle bb = computeBoundingBox(clusters);

        double avgStep = 0;
        // compute the displacement for the word in this time step
        for(int i=0;i<clusters.size();i++) {
            Point dxy = new Point(0,0);

            if(!clusters.get(i).overlap(clusters)) {
                //attractive force (compact principle)
                dxy.add(computeAttractiveForce(bb, clusters, clusters.get(i)));
                assert (!Double.isNaN(dxy.getX()));
            }
            else {
                //repulsion force (removing overlaps)
                dxy.add(computeRepulsiveForce(bb, clusters, clusters.get(i)));
            }

            // move the rectangle
            dxy = normalize(dxy,bb);
            clusters.get(i).center.add(dxy);

            assert(!Double.isNaN(clusters.get(i).center.getX()));
            assert(!Double.isNaN(clusters.get(i).center.getY()));
            double len = dxy.length() / Math.max(bb.getWidth(), bb.getHeight());
            avgStep = Math.max(avgStep, len);
        }

        //avgStep /= clusters.size();
        //System.out.println("avgStep = " + avgStep);
        return avgStep > 0.0001;
    }

    private Point computeAttractiveForce(Rectangle bb, List<Cluster> clusters, Cluster cluster) {
        Point dxy = new Point(0, 0);

        double cnt = 0;
        for(int j=0;j<clusters.size();j++) {
            if(cluster.equals(clusters.get(j)))  continue;

            dxy.add(cluster.computeAttractiveForce(bb,clusters.get(j)));
            cnt++;
        }

        if(cnt>0) dxy.scale(1.0/cnt);
        return dxy;
    }

    private Point computeRepulsiveForce(Rectangle bb, List<Cluster> clusters, Cluster cluster) {
        Point dxy = new Point(0, 0);

        double cnt = 0;
        for(int j=0;j<clusters.size();j++) {
            if(cluster.equals(clusters.get(j))) continue;

            dxy.subtract(cluster.computeRepulsiveForce(bb,clusters.get(j)));
            cnt++;
        }

        if(cnt>0) dxy.scale(1.0/cnt);
        dxy.scale(1.0/cnt);
        return dxy;
    }

    private Rectangle computeBoundingBox(List<Cluster> clusters) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for(Cluster c:clusters)  {
            Rectangle rect = c.getBoundingBox();

            minX = Math.min(minX,rect.getMinX());
            maxX = Math.max(maxX,rect.getMaxX());
            minY = Math.min(minY,rect.getMinY());
            maxY = Math.max(maxY,rect.getMaxY());
        }
        return new Rectangle(minX,minY,maxX-minX,maxY-minY);
    }

    private Point normalize(Point force,Rectangle bb) {
        //maximum allowed movement is 2% of the screen width/height
        double mx = Math.min(bb.getWidth(), bb.getHeight());
        double len = force.length();
        if(len<1e-3) return force;

        double maxLen = Math.min(len, T * mx / 50.0);
        force.scale(maxLen/len);
        return force;
    }

    private void restoreWordPositions() {
        for(Cluster c:clusters)
            for(Word w:c.wordPositions.keySet())
                wordPositions.put(w,c.actualWordPosition(w));

        List<Rectangle> rects = new ArrayList<>(words.size());
        for(int i=0;i<words.size();i++)
            rects.add(wordPositions.get(words.get(i)));

        new ForceDirectedOverlapRemoval<Rectangle>(0.25).run(rects);
    }

    private class Cluster  {
        private Map<Word,Rectangle> wordPositions = new HashMap<Word,Rectangle>();
        private Point center;

        public Rectangle getBoundingBox()  {
            Rectangle bb = new Rectangle();
            for(Word w:wordPositions.keySet()) {
                Rectangle r = actualWordPosition(w);
                bb.add(r);
            }
            return bb;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            for(Word w:wordPositions.keySet())
                sb.append(" " + w.getWord());
            return sb.toString();
        }

        boolean overlap(List<Cluster> list) {
            for(Cluster c:list) {
                if(c.equals(this)) continue;

                if(overlap(c)) return true;
            }
            return false;
        }

        boolean overlap(Cluster other) {
            for(Word w1:wordPositions.keySet())
                for(Word w2:other.wordPositions.keySet()) {
                    Rectangle rect1 = actualWordPosition(w1);
                    Rectangle rect2 = other.actualWordPosition(w2);

                    if(rect1.intersection(rect2, 1.0))  return true;
                }
            return false;
        }

        Rectangle actualWordPosition(Word word) {
            Rectangle r1 = wordPositions.get(word);
            return new Rectangle(r1.getX()+center.getX(), r1.getY() + center.getY(), r1.getWidth(), r1.getHeight());
        }

        Point computeRepulsiveForce(Rectangle bb, Cluster other)  {
            if(!overlap(other)) return new Point();

            // compute the displacement due to the overlap repulsive force
            Rectangle rectI = getBoundingBox();
            Rectangle rectJ = other.getBoundingBox();

            assert (rectI.intersection(rectJ,0.0));

            double hix = Math.min(rectI.getMaxX(),rectJ.getMaxX());
            double lox = Math.max(rectI.getMinX(),rectJ.getMinX());
            double hiy = Math.min(rectI.getMaxY(),rectJ.getMaxY());
            double loy = Math.max(rectI.getMinY(),rectJ.getMinY());
            double dx = hix - lox; // hi > lo
            double dy = hiy - loy;
            assert (dx >= -EPS);
            assert (dy >= -EPS);

            double force = KR * Math.min(dx, dy);

            Point dir = new Point(other.center.getX()-center.getX(),other.center.getY()-center.getY());
            double len = dir.length();
            if(len<EPS) dir = Point.randomPoint();

            dir.normalize();
            dir.scale(force);

            return dir;
        }

        Point computeAttractiveForce(Rectangle bb, Cluster other) {
            double force = center.computeDistance(other.center);
            if(T<0.5) force *= T;

            Point dir = new Point(other.center.getX()-center.getX(),other.center.getY()-center.getY());
            dir.normalize();
            dir.scale(force);

            return dir;
        }
    }
    
}
