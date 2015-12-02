package main.java.ui;

import java.util.List;

public class WordCloudChartPanel extends GenericWordCloudPanel<WordCloudBarChart> {
	
	private static final long serialVersionUID = -8701169528643126535L;
	
	int currentPanel;
	
	public WordCloudChartPanel(List<WordCloudBarChart> charts,int frames) {
		super(charts,frames);
	}

	@Override
	protected void addCards() {
		for(int i=0;i<cards.size();i++) add(cards.get(i).getChartPanel(),""+(i+1));
	}
	
	public void play(int nextTick) {
		showCard(nextTick*frames);
	}
	
	@Override
	public void slide(int panel) {	
		int nextTick = (int) Math.ceil(panel/frames);	
		showCard(nextTick*frames);
	}
}