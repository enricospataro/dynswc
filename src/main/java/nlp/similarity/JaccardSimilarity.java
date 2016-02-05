package main.java.nlp.similarity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.nlp.Document;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

public class JaccardSimilarity extends BaseSimilarityStrategy {
	
	private Map<WordPair,Double> similarityMap;
	
	@Override
	protected void execute(Document doc) {
		List<Word> words = doc.getWords();
        similarityMap = new HashMap<WordPair,Double>();
        
        for (int i=0;i<words.size();i++) {      	
        	Word w1=words.get(i);
        	Set<Integer> w1Sentences = new HashSet<>(w1.getSentences());
        	
        	for(int j=i+1;j<words.size();j++) {       		
        		Word w2=words.get(j);
        		Set<Integer> w2Sentences = new HashSet<>(w2.getSentences());
        		
        		Set<Integer> commonSentences = new HashSet<>(w1Sentences);
        		commonSentences.retainAll(w2Sentences);
        		
        		Set<Integer> unionSentences = new HashSet<>(w1Sentences);
        		unionSentences.addAll(w2Sentences);
        		
        		double score=((double)commonSentences.size())/((double)unionSentences.size());
        		assert (0 <= score && score <= 1.0);
        		similarityMap.put(new WordPair(w1,w2), score);
        	}
        	similarityMap.put(new WordPair(w1,w1), 1.0);
        }
	}

	@Override
	protected Map<WordPair, Double> getSimilarity() {return similarityMap;}
	public String toString() {return "Jaccard";}
}
