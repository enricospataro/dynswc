package main.java.layout;

import java.util.Collections;
import java.util.Comparator;

import main.java.layout.TagCloudStrategy;

public class TagCloudRankStrategy extends TagCloudStrategy {
	
    public TagCloudRankStrategy() {}

    protected void sortWords() {Collections.sort(words,Comparator.reverseOrder());}
	public String toString() {return "RankTagCloud";}
}
