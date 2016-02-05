package main.java.layout;

import java.util.List;

import main.java.nlp.Word;

public class TagCloudAlphabeticalStrategy extends TagCloudStrategy
{
    protected void sortWords()
    {
        List<Word> lWords = wordGraph.getWords();
        lWords.sort((w1, w2) -> w1.getWord().compareToIgnoreCase(w2.getWord()));

        words = wordGraph.getWords();
        similarity = wordGraph.getSimilarity();
    }
	public String toString() {return "AlphabeticalTagCloud";}
}
