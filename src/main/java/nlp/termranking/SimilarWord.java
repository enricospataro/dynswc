package main.java.nlp.termranking;

import java.util.Map;

import main.java.nlp.Word;
import main.java.nlp.WordPair;


public class SimilarWord extends Word implements Similar<SimilarWord> {

	private Word original;
	private Map<WordPair,Double> similarityMap;
	
	public SimilarWord(Word word, Map<WordPair,Double> similarity) {
		super(word.getWord(), word.getScore());
		this.original = word;
		this.similarityMap = similarity;
	}
	@Override
	public double similarity(SimilarWord other) {
		WordPair up = new WordPair(original, other.original);
		assert(similarityMap.containsKey(up));
		return similarityMap.get(up);
	}
}
