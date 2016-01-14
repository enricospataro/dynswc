package main.java.nlp.termranking;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.java.nlp.Document;
import main.java.nlp.Word;
import main.java.utils.DataInputHandler;


public class TFIDFRanking  implements RankingStrategy{
	
	private Map<String,Double> idfMap = new HashMap<>();
	final int CORPUS_SIZE = 500;
	
	public void rank(Document document) {
		computeIDF();
		List<Word> words = document.getWords();
		for(Word w:words) {
			double tf = 1 + Math.log10(w.getSentences().size()); 
			double idf;
			
			if(idfMap.containsKey(w.getStem())) idf=idfMap.get(w.getStem());
			else idf=Math.log10(CORPUS_SIZE); // idf=7;
			
			w.setScore(tf*idf);
		}	
	}
	
	public void computeIDF() {
		DataInputHandler dataIn = new DataInputHandler(new File("src/main/resources/corpus/brown.bin"));
		ObjectInputStream ois = dataIn.getObjectInputStream();
		try {
			idfMap = (HashMap<String,Double>)ois.readObject();
			ois.close();
		} catch (ClassNotFoundException | IOException e) {e.printStackTrace();}
		for(String s:idfMap.keySet()) {
			idfMap.put(s, Math.log10(CORPUS_SIZE/idfMap.get(s)));
		}
	}
}
