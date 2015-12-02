package main.java.nlp.termranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.nlp.Document;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

/**
 * @author spupyrev
 * Aug 18, 2013
 */
public class LexRankRanking implements RankingStrategy {
	private static final double LEXRANK_THRESHOLD = 0.1;

	@Override
	public void rank(Document document) {
		Map<WordPair,Double> coocNumber = initCoocNumber(document);
		
		List<Word> words = document.getWords();
		Map<SimilarWord,Word> wordMap = new HashMap<>();
		List<SimilarWord> similarWords = new ArrayList<>(words.size());
		for (Word w:words) {
			SimilarWord similarWord = new SimilarWord(w,coocNumber);
			similarWords.add(similarWord);
			wordMap.put(similarWord, w);
		}
		LexRankResults<SimilarWord> results = LexRanker.rank(similarWords, LEXRANK_THRESHOLD, true);
		double max = results.scores.get(results.rankedResults.get(0));

		if (Double.isInfinite(max)) {throw new IllegalArgumentException("Argument not suited for lexrank");}

		for (SimilarWord w:results.rankedResults) {
			Word word = wordMap.get(w);
			word.setScore(results.scores.get(w)/max);
		}
	}
	private Map<WordPair,Double> initCoocNumber(Document document) {
		int n = document.getWords().size();
		double[][] sim = new double[n][n];

		Map<WordPair,Double> coocNumber = new HashMap<>();
		for (int i=0; i<n; i++)
			for (int j=i; j<n; j++) {
				Word w1 = document.getWords().get(i);
				Word w2 = document.getWords().get(j);

				Set<Integer> intersection = new HashSet<>(w1.getSentences());
				intersection.retainAll(w2.getSentences());
				//Set<Integer> union = new HashSet<Integer>(w1.getSentences());
				//union.addAll(w2.getSentences());

				double res1 = intersection.size();
				double res2 = 1;//union.size();
				double res = res1 / res2;
				coocNumber.put(new WordPair(w1, w2), res);

				sim[i][j] = sim[j][i] = res;
			}
		return coocNumber;
	}
}
