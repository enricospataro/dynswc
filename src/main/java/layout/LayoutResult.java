package main.java.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import main.java.geometry.Rectangle;
import main.java.nlp.Word;

public class LayoutResult {
    private Map<Word,Rectangle> wordPositions;
    private List<Word> words;

    public LayoutResult(List<Word> words,List<Rectangle> positions) {
    	this.words=words;
        this.wordPositions = new HashMap<>(); 
        IntStream.range(0,words.size()).forEach(i -> wordPositions.put(words.get(i),positions.get(i)));
    }
    
    public LayoutResult(Map<Word,Rectangle> wordPositions) {
    	this.words=new ArrayList<>(wordPositions.keySet());
    	this.wordPositions=wordPositions;
    }
    
    public List<Word> getWords() {return words;}
    public Rectangle getWordPosition(Word w) {return wordPositions.get(w);}
    public Map<Word,Rectangle> getWordPositionMap() {return wordPositions;}
}
