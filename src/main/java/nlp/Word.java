package main.java.nlp;

import java.util.ArrayList;
import java.util.List;


public class Word implements Comparable<Word>, Cloneable{
	
	protected String word;
    protected String stem;
    protected double score;
    
    private List<Integer> sentences;

    public Word(String word,double score) {
    	this.word=word;
    	this.stem=null;
    	this.score=score;
    	sentences=new ArrayList<>();
    }

    public void addSentence(int id) {sentences.add(id);}
    public List<Integer> getSentences()  {return sentences;}   
    
    public String getWord() {return word;}   
    public String getStem() {return stem;}
    public double getScore() {return score;}
    
    public void setWord(String w) {word=w;}
    public void setStem(String s) {stem=s;}
    public void setScore(double s) {score=s;}
    public void incrementScore(double s) {setScore(score+s);}
    public void decrementScore(double s) {setScore(score-s);}
    
    @Override
    public int hashCode() {return word.hashCode();}
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Word))return false;
        return word.equals(((Word)o).word);
    }
    public int compareTo(Word o) {
        return Double.compare(score, o.score); }

    public String toString() {return stem;}

    @Override
    public Object clone() {
        try{return super.clone();}
        catch (Exception e) {throw new RuntimeException(e);}
    }
}
