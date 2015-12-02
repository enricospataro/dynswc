package main.java.nlp.similarity;

import java.util.Map;

import main.java.nlp.Document;
import main.java.nlp.WordPair;

public interface SimilarityStrategy {
    public Map<WordPair, Double> computeSimilarity(Document document);
}