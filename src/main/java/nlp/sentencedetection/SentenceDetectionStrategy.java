package main.java.nlp.sentencedetection;

import java.util.List;


public interface SentenceDetectionStrategy {
	public List<String> detect(String text);
}
