package main.java.speechconversion;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.decoder.adaptation.Stats;
import edu.cmu.sphinx.decoder.adaptation.Transform;
import edu.cmu.sphinx.result.WordResult;


public class SpeechRecognizer {
	
	public static void main(String[] args) throws Exception {
		
		Configuration configuration = new Configuration();
		
		// Set path to acoustic model.
		configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
		// Set path to dictionary.
		configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
		// Set language model.
		configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
		
		String pathToTextFile = "/home/enricospataro/Desktop/Tesi/Speeches/iran-deal-speech.txt"; // Transcription
		// file
		String finalText = "";
		FileOutputStream text = new FileOutputStream(pathToTextFile);
		
		StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
		recognizer.startRecognition(new FileInputStream("/home/enricospataro/Desktop/Tesi/Speeches/iran-deal-speech.wav"));

	        // Simple recognition with generic model
	        SpeechResult result;
	        while ((result = recognizer.getResult()) != null) {
	        	System.out.format("Hypothesis: %s\n", result.getHypothesis());
	        	finalText = result.getHypothesis() + " ";
	        	text.write(finalText.getBytes());

	            System.out.println("List of recognized words and their times:");
	            for (WordResult r : result.getWords()) {
	                System.out.println(r);
	            }

	            System.out.println("Best 3 hypothesis:");
	            for (String s : result.getNbest(3))
	                System.out.println(s);
	        }
	        recognizer.stopRecognition();
	        text.close();
	        
	}
}
