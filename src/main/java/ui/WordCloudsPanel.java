package main.java.ui;

import java.util.List;

public class WordCloudsPanel extends GenericWordCloudPanel<WordCloudPanel>{
	
	private static final long serialVersionUID = -4760006300259483646L;
	
	public WordCloudsPanel(List<WordCloudPanel> panels,int frames) {
		super(panels,frames);
	}

	@Override
	protected void addCards() {
		for(int i=0;i<cards.size();i++) add(cards.get(i),""+(i+1));			
	}
}