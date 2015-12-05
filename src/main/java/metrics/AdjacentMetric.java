package main.java.metrics;

import java.util.List;

import main.java.layout.LayoutResult;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

public interface AdjacentMetric {
    List<WordPair> getCloseWords(List<Word> words,LayoutResult layout);
}
