package main.java.layout;

import main.java.geometry.Rectangle;
import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;
import main.java.layout.clusters.ClusterForceDirectedPlacer;
import main.java.layout.clusters.WordPlacer;
import main.java.layout.ForceDirectedUniformity;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.jgrapht.alg.ConnectivityInspector;

/**
 * @author spupyrev 
 * Aug 29, 2013 
 */
public class StarForestStrategy extends BaseLayoutStrategy {
	
	private WordPlacer wordPlacer;
	private int numIterations = 0;
	
    public StarForestStrategy()  {super();}

    @Override
    public void execute() {
    	
        Graph g = new Graph(wordGraph); 
        
        List<LayoutResult> forest = greedyExtractStarForest(g);
        
        if(numIterations!=0) wordPlacer = new ClusterForceDirectedPlacer(wordGraph,forest,boundingBox,lastResult);
        else wordPlacer = new ClusterForceDirectedPlacer(wordGraph,forest,boundingBox,null);
        
        IntStream.range(0,words.size()).forEach(i -> wordPositions.add(wordPlacer.getRectangleForWord(words.get(i))));
        
        new ForceDirectedUniformity<Rectangle>(0.25).run(wordPositions);
        
        numIterations++; 
    }

    private List<LayoutResult> greedyExtractStarForest(Graph g) {
        List<LayoutResult> result = new ArrayList<>();
        Set<Vertex> usedVertices = new HashSet<>();

        while(true) {
            //find best star center
            double bestSum = 0;
            Vertex bestStarCenter = null;
            
            for(Vertex v:g.vertexSet())
                if(!usedVertices.contains(v)) {
                    double sum = 0;
                    for(Edge e:g.edgesOf(v))  {
                        Vertex u = g.getOtherSide(e,v);
                        if(!usedVertices.contains(u)) sum += g.getEdgeWeight(e);
                    }

                    if(bestStarCenter == null || sum>bestSum)  {
                        bestSum = sum;
                        bestStarCenter = v;
                    }
                }
            
            //every word is taken
            if(bestStarCenter==null) break;

            assert(!usedVertices.contains(bestStarCenter));

            //run FPTAS on the star
            Graph star = createStar(bestStarCenter,usedVertices,g);      
            SingleStarStrategy ssa = new SingleStarStrategy();
            ssa.setGraph(star); 

            //take the star
            result.add(ssa.layout(wordGraph));
            
            //update used
            usedVertices.addAll(ssa.getRealizedVertices());
            
        	}
        return result;
    }

    private Graph createStar(Vertex center, Set<Vertex> usedVertices, Graph g) {
        List<Word> words = new ArrayList<>();
        for(Vertex v:g.vertexSet())
            if(!usedVertices.contains(v)) words.add(v);

        Map<WordPair,Double> weights = new HashMap<WordPair,Double>();
        for(Vertex v:g.vertexSet()) {
            if(center.equals(v)) continue;
            if(usedVertices.contains(v)) continue;
            if(!g.containsEdge(center,v)) continue;

            WordPair wp = new WordPair(center,v);
            Edge edge = g.getEdge(center,v);
            weights.put(wp,g.getEdgeWeight(edge));
        }    
        return new Graph(words,weights);
    }
	public String toString() {return "StarForest";}
}
