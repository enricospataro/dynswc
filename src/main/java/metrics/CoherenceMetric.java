package main.java.metrics;

import java.util.ArrayList;
import java.util.List;

import main.java.geometry.Point;
import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;
import main.java.nlp.Word;

public class CoherenceMetric implements DynamicMetric {
	
	private List<Word> commonWords;
	
	@Override
	public double getValue(WordGraph wordGraphA, LayoutResult layoutA,
			WordGraph wordGraphB, LayoutResult layoutB) {
		
		initCommonWords(wordGraphA.getWords(),wordGraphB.getWords());
		double value = computePositionVariation(layoutA,layoutB);
		
		return 1 - value;
	}
	
	private double computePositionVariation(LayoutResult layoutA,LayoutResult layoutB) {
		double maxVar = Double.MIN_VALUE;
		double var = 0.0;
		double sum = 0.0;
		
		for(Word w:commonWords) {
			Rectangle rectA = layoutA.getWordPosition(w); 
			Rectangle rectB = layoutB.getWordPosition(w);
			Point dir = new Point(rectB.getCenterX()-rectA.getCenterX(),rectB.getCenterY()-rectA.getCenterY());        
			var = dir.length();
			
			sum += var;
			if(var>maxVar) maxVar = var;
		}
		return sum/(commonWords.size()*maxVar);
	}

	private List<Word> initCommonWords(List<Word> wordsA, List<Word> wordsB) {
		commonWords=new ArrayList<>();
		commonWords.addAll(wordsB);
		commonWords.retainAll(wordsA);

		return commonWords;
	}
}
