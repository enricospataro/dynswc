package main.java.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.layout.WordGraph;
import main.java.nlp.Word;
import main.java.nlp.WordPair;
import main.java.utils.UnorderedPair;

import org.jgrapht.graph.SimpleWeightedGraph;

public class Graph extends SimpleWeightedGraph<Vertex,Edge>{
	
	private static final long serialVersionUID = 8718545218507259728L;
	
	public Graph(WordGraph wordGraph) {this(wordGraph.getWords(),wordGraph.getSimilarity());}
	public Graph(List<Word> words,Map<WordPair,Double> weights)  {
		super(Edge.class);

	    List<Vertex> vertices = new ArrayList<>();
	    Map<Word,Vertex> wordToVertex = new HashMap<>();
	    for(Word w:words) {
	    	Vertex v = new Vertex(w.getWord(), w.getScore(), this);
	        vertices.add(v); 
	        wordToVertex.put(w,v); 
	    }
	    Map<UnorderedPair<Vertex,Vertex>,Double> vertexWeights = new HashMap<>();
	    for(WordPair wp:weights.keySet()) {
	    	Vertex v1 = wordToVertex.get(wp.getFirst());
	        Vertex v2 = wordToVertex.get(wp.getSecond());

	        UnorderedPair<Vertex,Vertex> newPair = new UnorderedPair<>(v1,v2);
	        vertexWeights.put(newPair,weights.get(wp));
	    	}
	        buildGraph(vertices,vertexWeights);
	    }
	
	  public List<Word> getWords(){return new ArrayList<Word>(vertexSet());} 
	  public Map<WordPair,Double> getSimilarities()  {
		  Map<WordPair,Double> similarity = new HashMap<>();
		  for(Edge e:edgeSet()) {
			  WordPair wp = new WordPair(getEdgeSource(e),getEdgeTarget(e));
			  similarity.put(wp,getEdgeWeight(e));
		  }
		  for(Word w1:vertexSet()) 
			  for(Word w2:vertexSet()) {
				  WordPair wp = new WordPair(w1, w2);
				  if(!similarity.containsKey(wp)) similarity.put(wp,0.0);
			  }
		  return similarity;
	  }
	  private void buildGraph(List<Vertex> vertices, Map<UnorderedPair<Vertex,Vertex>,Double> weights) {
		  for(Vertex v:vertices) addVertex(v); 
		  
		  for(UnorderedPair<Vertex,Vertex> pair:weights.keySet()) {
			  Vertex v1 = pair.getFirst();
			  Vertex v2 = pair.getSecond();
			  if(v1.equals(v2)) continue;
			  
			  Edge e = addEdge(v1,v2);
			  setEdgeWeight(e,weights.get(pair)); 
		  }
	  }
	  public Vertex getOtherSide(Edge e, Vertex v) {
	        if(getEdgeSource(e)==v) return getEdgeTarget(e);
	        else {
	            assert getEdgeTarget(e) == v;
	            return getEdgeSource(e);
	        }
	    }
}
