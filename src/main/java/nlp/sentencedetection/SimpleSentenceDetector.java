package main.java.nlp.sentencedetection;

import java.util.List;

import main.java.utils.TextUtils;

public class SimpleSentenceDetector implements SentenceDetectionStrategy {
	
	private List<String> sentencesList;
	
	/* every sentence has an average length of 15-20 words and each word has 
	 * an average length of 5.1 characters,where every character equals to 1 byte, 
	 * plus we have to take into account the spaces between two words and the punctuation
	 * so the number of bytes to be read is at least 100
	 */ 
	private int bytesToRead=120; 
	
	public List<String> detect(String text) {
		sentencesList = TextUtils.splitText(text,bytesToRead);
		return sentencesList;
	}
}
