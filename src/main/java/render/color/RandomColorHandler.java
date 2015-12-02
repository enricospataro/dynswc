package main.java.render.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import main.java.nlp.Word;
import main.java.semantics.ClusterResult;

public class RandomColorHandler implements ColorHandler {
	private Map<Word,Color> colorsMap = new HashMap<>();
	private Random random = new Random();
	
	public Color getColor(Word w) {
		if(!colorsMap.containsKey(w)) {
			int r = random.nextInt(255);
			int g = random.nextInt(255);
			int b = random.nextInt(255);
			Color c = new Color(0,0,0);
			colorsMap.put(w,c);
		}
		return colorsMap.get(w);
	}
	public Map<Word,Color> getColorsMap() {return colorsMap;}
	@Override
	public ClusterResult getClusterResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
