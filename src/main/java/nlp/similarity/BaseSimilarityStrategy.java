package main.java.nlp.similarity;

import java.util.Map;

import main.java.nlp.Document;
import main.java.nlp.WordPair;


public abstract class BaseSimilarityStrategy implements SimilarityStrategy {

    @Override
    public Map<WordPair,Double> computeSimilarity(Document document) {
        execute(document);
        return getSimilarity();
    }
    protected abstract void execute(Document document); //template method
    protected abstract Map<WordPair,Double> getSimilarity();
}
