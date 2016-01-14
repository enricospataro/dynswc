package main.java.nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.main.Manager;
import main.java.nlp.sentencedetection.SentenceDetectionStrategy;
import main.java.nlp.termranking.RankingStrategy;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.tokenize.TokenizerME;


public class Document {
	
	private String text;
	private SentenceDetectionStrategy sentenceDetector;
	private List<String> sentences;
	private TokenizerME tokenizer;
	private String[] tempTokens;
	private Stemmer stemmer;
	private List<String> stopWords;
	private List<Word> words;
	
	//stem => word
    Map<String, Word> wordMap = new HashMap<>();
    //stem => list of original words
    Map<String, List<String>> stemMap = new HashMap<>();
	
    public Document(String text, SentenceDetectionStrategy sentenceDetector, TokenizerME tokenizer, Stemmer stemmer, List<String> stopWords) {
    	this.text=text;
		this.sentenceDetector=sentenceDetector;
		this.tokenizer=tokenizer;
		this.stemmer=stemmer;
		this.stopWords=stopWords;
	}
    
	public void setWords(List<Word> list) {this.words=list;}
    public List<Word> getWords() {return words;}

	public void parse() {
		sentences=sentenceDetector.detect(text);
		for(int i=0;i<sentences.size();i++) { 
			String currentSentence = sentences.get(i);
			tempTokens = tokenizer.tokenize(currentSentence);
			for(int j=0;j<tempTokens.length;j++) {
				
				String currentWord = tempTokens[j].toLowerCase();				
				if(!isWord(currentWord)) continue;
				
		        if(currentWord.charAt(0)=='“' || currentWord.charAt(0)=='#')  currentWord = currentWord.substring(1,currentWord.length());
		        
				String currentStem = stemmer.stem(currentWord).toString();
				if(stopWords.contains(currentWord) || stopWords.contains(currentStem)) continue;
				
				if(!wordMap.containsKey(currentStem)) {
                    wordMap.put(currentStem, new Word("",0.0));
                    stemMap.put(currentStem, new ArrayList<String>());
                }
				wordMap.get(currentStem).setStem(currentStem);
                wordMap.get(currentStem).addSentence(i);
                stemMap.get(currentStem).add(tempTokens[j]); 
            }
		}
		restoreMostPopularWord();
	}
	
	//restore the most popular word variant
	private void restoreMostPopularWord() {
        words = new ArrayList<>();
        for(String stem:wordMap.keySet()) {
            Map<String,Integer> variationMap = new HashMap<>();
            for(String w:stemMap.get(stem)) {
                if(!variationMap.containsKey(w)) variationMap.put(w,0);
                variationMap.put(w,variationMap.get(w)+1);
            }
            String maxVariation=null;
            for(String variant:variationMap.keySet())
                if(maxVariation == null || variationMap.get(variant)>variationMap.get(maxVariation)) maxVariation=variant;
            
            wordMap.get(stem).setWord(maxVariation);
            words.add(wordMap.get(stem));
        }
	}
	private boolean isWord(String word) {
        char firstChar = word.charAt(0);
        if (!Character.isLetter(firstChar) && firstChar != '#' && firstChar != '“' ) return false;
        for(int i=1; i<word.length(); i++) {
            char c = word.charAt(i);
            boolean isLetter = Character.isLetter(c) || c == '-';
            if(!isLetter) return false;
        }
        return true;
    }
	/**
     * Keep the most important words
     */
    public void rankFilter(int maxWords,RankingStrategy rankStrategy) {
        rankStrategy.rank(this);
        Collections.sort(words,Comparator.reverseOrder());       
        if(words.size()>maxWords) words=words.subList(0,maxWords);
        Manager.setWords(words.size());
        rescaleScores();
    }
    /**
     * scaling weights from 1 to 5
     */
    protected void rescaleScores(){rescaleScores(5);}

    /**
     * logarithmic re-scaling weights from 1 to upper
     */
    private void rescaleScores(double upper){
        if (words.size()<=1) return;

        double mn = words.get(words.size()-1).getScore();
        double mx = words.get(0).getScore();
        
        for (Word w:words){
            double num = Math.log(w.getScore()) - Math.log(mn);
            double den = (mx > mn ? Math.log(mx) - Math.log(mn) : 1.0); 

            w.setScore(1.0 + (upper-1.0) * num / den);
        }
    }

}
