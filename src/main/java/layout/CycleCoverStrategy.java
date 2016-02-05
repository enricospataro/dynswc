package main.java.layout;

import main.java.geometry.Rectangle;
import main.java.graph.CycleCoverExtractor;
import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.GreedyCycleCoverExtractor;
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

public class CycleCoverStrategy extends BaseLayoutStrategy {
	
    private Graph graph;
    private List<Edge> edgesInMatching;
    private boolean useGreedy = false;
    private int numIterations=0;
    private WordPlacer wordPlacer;
    
    public CycleCoverStrategy() {super();}

    public void setUseGreedy(boolean useGreedy) {
        this.useGreedy = useGreedy;
    }

    @Override
    protected void execute() {
        graph = new Graph(wordGraph);

        if(!useGreedy) {
            CycleCoverExtractor tme = new CycleCoverExtractor(graph); 
            tme.runUndirected();
            edgesInMatching = tme.getMatchedEdges();
        } else {
            GreedyCycleCoverExtractor tme = new GreedyCycleCoverExtractor(graph);
            tme.run();
            edgesInMatching = tme.getMatchedEdges();
        }
       
        checkConsistency(edgesInMatching);
       
        List<List<Vertex>> cycles = getCycles(edgesInMatching); 

        int CYCLE_SIZE_LIMIT = 12;
        cycles = breakLongCycles(cycles, CYCLE_SIZE_LIMIT);
        
        List<LayoutResult> cycleLayouts = new ArrayList<LayoutResult>();
        for(List<Vertex> c:cycles)  {
            BaseLayoutStrategy algo = null;

            if(c.size() <= CYCLE_SIZE_LIMIT) algo = new SingleCycleStrategy();
            else algo = new SinglePathStrategy();
           
            cycleLayouts.add(algo.layout(new WordGraph(getCycleWords(c), getCycleWeights(c))));
        }

//        Logger.println("#cycles: " + cycles.size());
//        Logger.println("weight: " + getRealizedWeight());
        
        if(numIterations!=0) wordPlacer = new ClusterForceDirectedPlacer(wordGraph,cycleLayouts,boundingBox,lastResult);
        else wordPlacer = new ClusterForceDirectedPlacer(wordGraph,cycleLayouts,boundingBox,null);
        
        IntStream.range(0,words.size()).forEach(i -> wordPositions.add(wordPlacer.getRectangleForWord(words.get(i))));
        
        new ForceDirectedUniformity<Rectangle>(0.25).run(wordPositions);
        
        numIterations++;
    }

    private List<List<Vertex>> breakLongCycles(List<List<Vertex>> cycles, int cycleSizeLimit) {
        List<List<Vertex>> result = new ArrayList<List<Vertex>>();
        for(List<Vertex> c:cycles)
           if(c.size() <= cycleSizeLimit) result.add(c);
           else result.addAll(breakLongCycle(c,cycleSizeLimit));

        return result;
    }

    private List<List<Vertex>> breakLongCycle(List<Vertex> c, int cycleSizeLimit) {
        List<List<Vertex>> result = new ArrayList<List<Vertex>>();
        List<Vertex> cur = new ArrayList<Vertex>();
        for(Vertex v:c) {
            cur.add(v);
            if(cur.size() >= cycleSizeLimit) { 
                result.add(new ArrayList<Vertex>(cur));
                cur.clear();
            }
        }

        if(cur.size()>1) result.add(new ArrayList<Vertex>(cur)); 

        return result;
    }

    private List<List<Vertex>> getCycles(List<Edge> edges) {
        List<List<Vertex>> result = new ArrayList<List<Vertex>>();

        Map<Vertex, List<Vertex>> next = new HashMap<Vertex, List<Vertex>>();
        for(Vertex v:graph.vertexSet()) next.put(v, new ArrayList<Vertex>());

        for(Edge edge:edges) {
            Vertex u = graph.getEdgeSource(edge);
            Vertex v = graph.getEdgeTarget(edge);
            next.get(u).add(v);
            next.get(v).add(u);
        }

        Set<Vertex> used = new HashSet<Vertex>();
        for(Vertex v:graph.vertexSet())
           if(!used.contains(v)) {
                List<Vertex> cycle = new ArrayList<Vertex>();
                dfs(v, v, next, used, cycle);
                
                result.add(cycle);
            }
        return result;
    }

    private void dfs(Vertex v, Vertex parent, Map<Vertex, List<Vertex>> next, Set<Vertex> used, List<Vertex> cycle)  {
        used.add(v);
        cycle.add(v);

        for(Vertex u:next.get(v))
           if(!u.equals(parent) && !used.contains(u)) dfs(u, v, next, used, cycle);
    }

    private List<Word> getCycleWords(List<Vertex> cycle) {
        List<Word> res = new ArrayList<Word>();
        for(int i=0;i<cycle.size();i++) res.add(cycle.get(i));
       
        return res;
    }

    private Map<WordPair, Double> getCycleWeights(List<Vertex> cycle)  {
        Map<WordPair, Double> res = new HashMap<WordPair, Double>();
        for(int i=0;i<cycle.size();i++)
           for(int j=0;j<cycle.size();j++) {
                Vertex u = cycle.get(i);
                Vertex v = cycle.get(j);

                WordPair wp = new WordPair(u, v);
                res.put(wp, (i == j ? 1.0 : 0));
            }
        
        for(int i=0;i<cycle.size();i++) {
        	Vertex now = cycle.get(i);
            Vertex next = cycle.get((i + 1) % cycle.size());
            if(now.equals(next)) continue; // errore nel codice di pupyrev: cicli di un vertice!
            
            Edge edge = graph.getEdge(now,next);
            double weight = graph.getEdgeWeight(edge);
            WordPair wp = new WordPair(now, next);
            res.put(wp,weight);
        }
        return res;
    }

    private void checkConsistency(List<Edge> edges) {
        // check that we really have cycles
        Map<Vertex, Integer> degree = new HashMap<Vertex, Integer>();
        for(Edge edge:edges) {
            Vertex u = graph.getEdgeSource(edge);
            Vertex v = graph.getEdgeTarget(edge);
            int currentU = (degree.containsKey(u) ? degree.get(u) : 0);
            int currentV = (degree.containsKey(v) ? degree.get(v) : 0);

            if(currentU >= 2 || currentV >= 2) throw new RuntimeException("not a cycle");

            degree.put(u, currentU + 1);
            degree.put(v, currentV + 1);
        }
    }

    public double getRealizedWeight() {
        double realizedWeight = 0;
        for(Edge e:edgesInMatching) realizedWeight += graph.getEdgeWeight(e);

        return realizedWeight;
    }
	public String toString() {return "CycleCover";}
}