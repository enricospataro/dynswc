package main.java.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.nlp.Word;
import main.java.nlp.WordPair;


public class WordGraph {
	private List<Word> words;
    private Map<WordPair,Double> similarity;
    private Map<WordPair,Double> distance;
    private WordGraphStats stats;

    public WordGraph(List<Word> words,Map<WordPair,Double> similarity)  {
        this.words = words;
        this.similarity = similarity;
        initDistances();
        stats = new WordGraphStats(words,similarity,distance);
    }

    public List<Word> getWords() {return words;}
    public Map<WordPair,Double> getSimilarity() {return similarity;}
    public Map<WordPair,Double> getDistance() {return distance;}
    public double distance(Word w1, Word w2) {return distance.get(new WordPair(w1, w2));}
    
    public double weightedDegree(Word w){return stats.weightedDegree(w);}
    public Integer[] nonZeroAdjacency(Word w){return stats.nonZeroAdjacency(w);}
    public double shortestPath(Word w1, Word w2){return stats.shortestPath(w1, w2);}
    
    public Word[] convertWordsToArray() {return words.toArray(new Word[words.size()]);}    
    public double[][] computeSimilarityMatrix() {
    	int n=words.size();
    	double[][] similarityMatrix = new double[n][n];
    	for(int i=0;i<n;i++)
    		for(int j=0;j<n;j++) {
    			WordPair wp = new WordPair(words.get(i),words.get(j));
    			similarityMatrix[i][j]=similarity.get(wp);
    		}
    	return similarityMatrix;
    }
    private void initDistances() {
    	distance = new HashMap<>();
    	for(int i=0;i<words.size();i++) 
    		for(int j=0;j<words.size();j++) {
    			WordPair wp = new WordPair(words.get(i),words.get(j));
    			double simil = similarity.get(wp);
    			double dist = convertSimilarity(simil);
    			distance.put(wp,dist);
    		}
    }
    private double convertSimilarity(double similarity) {
    	double s=0.05;
        double d = -Math.log((1.0-s) * similarity + s);
        return Math.max(d,0.0);
    }
    
    void reorderWords(int startIndex) {
        int n = words.size();
        List<Word> path = new ArrayList<Word>();
        
        for(int i=0;i<n;i++)  path.add(words.get((i + startIndex + 1) % n));
        for(int i=0;i<n;i++)  words.set(i, path.get(i));
    }

    private void checkConsistency() {
        for(int i=0;i<words.size();i++){
            Word wi = words.get(i);
            WordPair wp = new WordPair(wi,wi);

            assert(similarity.containsKey(wp) && similarity.get(wp) == 1.0);
            assert(1.0 <= wi.getScore() && wi.getScore() <= 5.0);

            for(int j=0;j<words.size();j++) {
                Word wj = words.get(j);
                WordPair wp2 = new WordPair(wi, wj);
                assert (similarity.containsKey(wp2));
                double sim = similarity.get(wp2);

                assert(0 <= sim && sim <= 1.0);
            }
        }
    }
}
