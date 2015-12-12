package main.java.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import main.java.nlp.Word;
import main.java.nlp.WordPair;


public class WordGraphStats {
    private List<Word> words;
    private Map<WordPair,Double> similarity;
    private Map<WordPair,Double> distance;

    private Map<Word,Double> weightedDegree = new HashMap<>();
    private Map<Word,Integer[]> nonZeroAdjacency = new HashMap<>();
    private Map<WordPair,Double> shortestPaths = new HashMap<>();

    public WordGraphStats(List<Word> words, Map<WordPair,Double> similarity, Map<WordPair,Double> distance) {
        this.words = words;
        this.similarity = similarity;
        this.distance = distance;
    }
    public double weightedDegree(Word w) {
        if(!weightedDegree.containsKey(w)) createWeightedDegree(w);
        return weightedDegree.get(w);
    }
    public Integer[] nonZeroAdjacency(Word w) {
        if(!nonZeroAdjacency.containsKey(w)) createNonZeroAdjacency(w);
        return nonZeroAdjacency.get(w);
    }
    public double shortestPath(Word w1, Word w2) {
        WordPair wp = new WordPair(w1,w2);
        if(!shortestPaths.containsKey(wp))  createShortestPaths(w1);

        return shortestPaths.get(wp);
    }
    private void createWeightedDegree(Word w) {
    	double degree=0;  	
    	for(int i=0;i<words.size();i++) {
    		Word p=words.get(i);
    		if(w.equals(p)) continue;
    	
    	WordPair wp = new WordPair(w,p);
    	degree += similarity.get(wp);
    	}
    	weightedDegree.put(w,degree);
    }
    private void createNonZeroAdjacency(Word w){
    	List<Integer> adj = new ArrayList<>();
        
    	for(int i=0;i<words.size();i++) {
            Word p=words.get(i);
            if(w.equals(p)) continue;

            WordPair wp = new WordPair(w,p);
            if(similarity.get(wp)>0) adj.add(i);
        }
    	Integer[] adjArray = new Integer[adj.size()]; 
        nonZeroAdjacency.put(w,adj.toArray(adjArray));
    }
    private void createShortestPaths(Word s) {
        Map<Word,Integer> wIndex = new HashMap<>();
        for(int i=0;i<words.size();i++) wIndex.put(words.get(i),i);

        double INF = 123456789.0;

        double[] dist = new double[words.size()];
        Arrays.fill(dist, INF);
        dist[wIndex.get(s)] = 0;

        PriorityQueue<Word> q = new PriorityQueue<>();
        q.add(s);

        while(!q.isEmpty()) {
            Word now = q.poll();

            int v = wIndex.get(now);
            for(int i=0;i<words.size();i++) {
                Word next = words.get(i);
                WordPair wp = new WordPair(now, next);
                if(distance.containsKey(wp)) {
                    double len = distance.get(wp);
                    if(dist[i] > dist[v] + len) {
                       dist[i] = dist[v] + len;
                       q.add(next);
                    }
                }
            }
        }
        for(int i=0;i<words.size();i++) {
            WordPair wp = new WordPair(s,words.get(i));
            shortestPaths.put(wp,dist[i]);
        }
    }
}
