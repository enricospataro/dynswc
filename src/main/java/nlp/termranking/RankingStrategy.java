package main.java.nlp.termranking;

import main.java.nlp.Document;

public interface RankingStrategy {
	public void rank(Document document);
	public String toString();
}
