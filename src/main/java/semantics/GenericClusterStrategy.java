package main.java.semantics;

import java.awt.Color;

import main.java.layout.WordGraph;
import main.java.render.color.ClusterColorHandler;
import main.java.render.color.ColorHandlerConstants;

public class GenericClusterStrategy implements ClusterStrategy {
	
    private int desiredClusterNumber;
    private int colorSequenceLength;
    private int maxIter=5;

    public GenericClusterStrategy(int desiredClusterNumber,int colorSequenceLength){
        this.desiredClusterNumber = desiredClusterNumber;
        this.colorSequenceLength=colorSequenceLength;
    }

    @Override
    public ClusterResult run(WordGraph wordGraph)  {
        if (desiredClusterNumber != -1) {
            ClusterStrategy clusterAlgo = new KMeansStrategy(desiredClusterNumber);
            return clusterAlgo.run(wordGraph);
        }
        else {
            //trying to guess the "best" number
            int n = wordGraph.getWords().size();
            int K = Math.max((int)Math.sqrt((double)n/2),1);
            
            if(K+maxIter > colorSequenceLength) {
            	Color[] newSequence = ColorHandlerConstants.longClusters;
            	ClusterColorHandler.setColorSequence(newSequence);
            } 
            
            ClusterStrategy clusterAlgo = new KMeansStrategy(K);
            ClusterResult clusterResult = clusterAlgo.run(wordGraph);

            for(int i=1;i<=maxIter;i++) {
            	ClusterStrategy algo2 = new KMeansStrategy(K+i);
                ClusterResult clusterResult2 = algo2.run(wordGraph);

                if(clusterResult2.quality() > clusterResult.quality()) clusterResult = clusterResult2;              
                else break;
            }           
            return clusterResult;
        }
    }
}
