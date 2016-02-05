package main.java.nlp.termranking;

import java.util.List;

import main.java.nlp.Document;
import main.java.nlp.Word;


public class TFRanking implements RankingStrategy{
	
	List<Word> words;
	
	public void rank(Document document) {
		double frequency;
		words=document.getWords();
		double maxCount = words.stream().mapToDouble(w -> w.getSentences().size()).max().orElse(1);
		for(Word w:words) {
			frequency = w.getSentences().size();
			w.setScore(0.5 + 0.5*frequency/maxCount);
		}
	}
	public String toString() {return "Term Frequency";}
}
