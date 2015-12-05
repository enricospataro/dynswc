package main.java.layout.clusters;

import main.java.geometry.Rectangle;
import main.java.nlp.Word;

public interface WordPlacer {
    public Rectangle getRectangleForWord(Word w);
    public boolean contains(Word w);
}
