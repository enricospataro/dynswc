package main.java.nlp.sentencedetection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class ApacheSentenceDetector implements SentenceDetectionStrategy {

	private List<String> sentencesList = new ArrayList<>();
	private SentenceModel model;
	private SentenceDetectorME sentenceDetector;
	
	public ApacheSentenceDetector(SentenceModel sm) {
		model = sm; 	
		sentenceDetector = new SentenceDetectorME(model);
	}
	public List<String> detect(String text) {
		String[] sentences = sentenceDetector.sentDetect(text);
		IntStream.range(0, sentences.length).forEach(i -> sentencesList.add(sentences[i]));
		return sentencesList;
	}
}