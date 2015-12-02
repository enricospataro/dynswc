package main.java.ui;

import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class WordCloudsContPanel extends JPanel {
	
	private static final long serialVersionUID = -441825271267900175L;
	
	List<WordCloudPanel> panels;
	List<WordCloudBarChart> charts;
	int frames;
	LayoutPanel layout;
	WordCloudPlayer player;
	
	public WordCloudsContPanel(List<WordCloudPanel> panels,List<WordCloudBarChart> charts,int frames) {
		this.frames=frames;
		initUI(panels,charts);
	}

	private void initUI(List<WordCloudPanel> panels,List<WordCloudBarChart> charts) {
		layout=new LayoutPanel(panels,charts,frames);
		player=new WordCloudPlayer(layout,frames);
		
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		add(layout);
		add(player);
	}
}
