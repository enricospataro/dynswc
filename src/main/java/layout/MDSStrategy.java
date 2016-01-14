package main.java.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import main.java.geometry.Rectangle;
import main.java.layout.mds.DistanceScaling;
import mdsj.MDSJ;

public class MDSStrategy extends BaseLayoutStrategy {

	private boolean useOverlapRemoval = true;

	public MDSStrategy(boolean useOverlapRemoval) {this.useOverlapRemoval = useOverlapRemoval;}

	public MDSStrategy(){this(true);}

	@Override
	protected void execute()  {
	
		//maps words to their bounding rectangle
	    createBoundingBoxes();

	    //mds placement
	    computeInitialPlacement();

	    //force-directed overlap removal
	    if(useOverlapRemoval) new ForceDirectedOverlapRemoval<Rectangle>(0.25).run(wordPositions);
    }
	
	private void computeInitialPlacement()  {
		
		double[][] outputMDS = runMDS();
		
		// set coordinates
		for(int i=0;i<words.size();i++) {		
			Rectangle rect = wordPositions.get(i);
			double x = outputMDS[0][i];
			double y = outputMDS[1][i];
			x -= rect.getWidth()/2.;
			y -= rect.getHeight()/2.;
			rect.setRect(x,y,rect.getWidth(),rect.getHeight());
	    }
		perturbOverlappingPoints();
	}

	private void perturbOverlappingPoints() {
	
		// Perturb coincident word positions
		double EPS = 0.1;
	    boolean progress = false;
	    while(progress) {
	    	progress = false;
	        for(int i=0;i<words.size();i++)
	           for(int j=i+1;j<words.size();j++) {
	        	   
	              Rectangle r1 = wordPositions.get(i);
	              Rectangle r2 = wordPositions.get(j);

	              if((Math.abs(r1.getX()-r2.getX())) < EPS && (Math.abs(r1.getY()-r2.getY()) < EPS))  {
	                 Random r = new Random();
	                 r1.setRect(r1.getX()+r.nextDouble(),r1.getY()+r.nextDouble(),r1.getWidth(),r1.getHeight());
	                 progress = true;
	                    }
	                }
	        }
	    }

	    private double[][] runMDS() {
	        double scaling = computeScaling();

	        double[][] desiredDistance = new double[words.size()][words.size()];
	        for(int i=0;i<words.size();i++)
	            for(int j=0;j<words.size();j++) {
	                double dist = wordGraph.distance(words.get(i),words.get(j));

	                desiredDistance[i][j] = dist * scaling;
	            }

	        //aply MDS
	        double[][] outputMDS = new DistanceScaling().mds(desiredDistance,2);

	        for(int i=0;i<desiredDistance[0].length;i++) {
	            assert(!Double.isNaN(outputMDS[0][i]));
	            assert(!Double.isNaN(outputMDS[1][i]));
	        }
	        return outputMDS;
	    }

	    private double computeScaling() {
	        double areaSum = wordPositions.stream().mapToDouble(r -> r.getArea()).sum();
	        
	        List<Double> distances = new ArrayList();
	        for(int i=0;i<words.size();i++)
	            for(int j=0;j<words.size();j++) {
	                double dist = wordGraph.distance(words.get(i),words.get(j));
	                distances.add(dist);
	            }

	        double avgDist = distances.stream().mapToDouble(w -> w).average().orElse(1.0);

	        return Math.sqrt(areaSum) / avgDist;
	    }

	}
