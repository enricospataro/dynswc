package main.java.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.java.geometry.Rectangle;
import mdsj.MDSJ;

public class MDSStrategy extends BaseLayoutStrategy {

	@Override
	protected void execute() {
		createBoundingBoxes();
		computeInitialPlacement();
		new ForceDirectedOverlapRemoval<>(0.25).run(wordPositions);
	}
	private void computeInitialPlacement() {
		double[][] outputMDS = runMDS();

	    // set coordinates of rectangles
	    for(int i=0;i<words.size();i++)  {
	    	Rectangle rect = wordPositions.get(i);
	        double x = outputMDS[0][i];
	        double y = outputMDS[1][i];
	        x -= rect.getWidth()/2;
	        y -= rect.getHeight()/2;
	        rect.setX(x);
	        rect.setY(y);
	    }
	    perturbOverlappingPoints();
	}

    private void perturbOverlappingPoints() {
		double eps = 0.1;
		boolean cond=true;
		while(cond) {
			cond=false;
			for(int i=0;i<words.size();i++)
                for(int j=i+1;j<words.size();j++) {
                    Rectangle r1 = wordPositions.get(i);
                    Rectangle r2 = wordPositions.get(j);
		
			if(Math.abs(r1.getX()-r2.getX()) < eps && Math.abs(r1.getY()-r2.getY()) < eps) {
				Random r = new Random();
				r1.setX(r1.getX()+r.nextDouble());
				r1.setY(r1.getY()+r.nextDouble());
				cond=true;
				}
            }
		}
	}
	private double[][] runMDS()  {
        double scaling = computeScaling();
        int dim=2;

        double[][] desiredDistance = new double[words.size()][words.size()];
        for(int i=0;i<words.size();i++)
            for(int j=0;j<words.size();j++) {
                double dist = wordGraph.distance(words.get(i), words.get(j));
                desiredDistance[i][j] = dist * scaling;
            }
        double[][] outputMDS = MDSJ.classicalScaling(desiredDistance,dim);

        for(int i=0;i<desiredDistance[0].length; i++) {
            assert(!Double.isNaN(outputMDS[0][i]));
            assert(!Double.isNaN(outputMDS[1][i]));
        }
        return outputMDS;
    }
    
	private double computeScaling()  {
        double areaSum = wordPositions.stream().mapToDouble(r -> r.getArea()).sum(); 

        List<Double> distances = new ArrayList<>();
        for(int i=0;i<words.size();i++)
            for(int j=0;j<words.size();j++) {
                double dist = wordGraph.distance(words.get(i), words.get(j));
                distances.add(dist);
            }
        double avgDist = distances.stream().mapToDouble(w -> w).average().orElse(1.0);
        return Math.sqrt(areaSum) / avgDist;
    }
}
