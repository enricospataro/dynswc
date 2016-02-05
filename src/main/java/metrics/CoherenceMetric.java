package main.java.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import main.java.geometry.Point;
import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;
import main.java.nlp.Word;

public class CoherenceMetric implements DynamicMetric {
	
	private List<Word> commonWords;
	private double maxDiag;

	public CoherenceMetric(double maxDiag) {
		this.maxDiag=maxDiag;
	}
	
	@Override
	public double getValue(WordGraph wordGraphA, LayoutResult layoutA,
			WordGraph wordGraphB, LayoutResult layoutB) {
		
		initCommonWords(wordGraphA.getWords(),wordGraphB.getWords());
		
		double value = computePositionVariation(layoutA,layoutB);
		
		return 1 - value;
	}
	
	private double computePositionVariation(LayoutResult layoutA,LayoutResult layoutB) {
		double sum = 0.0;
		
		for(Word w:commonWords) {
			Rectangle rectA = layoutA.getWordPosition(w); 
			Rectangle rectB = layoutB.getWordPosition(w);
			Point dir = new Point(rectB.getCenterX()-rectA.getCenterX(),rectB.getCenterY()-rectA.getCenterY());        
			
			sum += dir.length();		
		}
		return sum/(commonWords.size()*maxDiag);
	}

	private List<Word> initCommonWords(List<Word> wordsA, List<Word> wordsB) {
		commonWords=new ArrayList<>();
		commonWords.addAll(wordsB);
		commonWords.retainAll(wordsA);

		return commonWords;
	}
}
