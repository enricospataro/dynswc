package main.java.render.color;

import java.awt.Color;

import main.java.nlp.Word;
import main.java.semantics.ClusterResult;

public interface ColorHandler {
	public abstract Color getColor(Word w);
	public ClusterResult getClusterResult();
}