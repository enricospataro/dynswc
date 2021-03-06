package main.java.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import main.java.geometry.BoundingBox;
import main.java.geometry.Rectangle;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

public abstract class BaseLayoutStrategy implements LayoutStrategy {
	
	protected WordGraph wordGraph;
	protected List<Word> words;
    protected Map<WordPair,Double> similarity;
    protected List<Rectangle> wordPositions;
    protected Map<Word,Rectangle> wordPositionsMap;
	protected BoundingBox boundingBox;
	protected double aspectRatio;
	protected List<LayoutResult> results;
	protected LayoutResult lastResult;
	
	public BaseLayoutStrategy() {
		boundingBox=new BoundingBox();
		aspectRatio=16.0/9.0;
	}
	
	@Override
	public void setBoundingBox(BoundingBox bb) {boundingBox=bb;}
	@Override
	public void setAspectRatio(double ar) {aspectRatio=ar;}
	
    protected abstract void execute();
    
    public final LayoutResult layout(WordGraph wordGraph) {
        this.wordGraph=wordGraph;
        this.words=wordGraph.getWords();
        this.similarity=wordGraph.getSimilarity();
        this.wordPositions=new ArrayList<Rectangle>(words.size());
        this.wordPositionsMap=new HashMap<>();  
        
        execute(); 
        
        lastResult = createResult();
        return lastResult;
    }
    
    protected LayoutResult createResult() {
    	IntStream.range(0,words.size()).forEach(i -> wordPositionsMap.put(words.get(i),wordPositions.get(i)));

    	return new LayoutResult(words,wordPositions);
//    	return new LayoutResult(wordPositionsMap);
    }
	public void createBoundingBoxes() {
		IntStream.range(0,words.size()).forEach(i -> wordPositions.add(getBoundingBox(words.get(i))));
	}	
	public Rectangle getBoundingBox(Word word) {return boundingBox.getBoundingBox(word);}
	public abstract String toString();
}
