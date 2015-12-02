package main.java.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;

public class LayoutPanel extends JPanel {
	
	private static final long serialVersionUID = -2912563177853615237L;
	
	List<WordCloudPanel> panels;
	List<WordCloudBarChart> charts;
	WordCloudsPanel wordCloudsPanel;
	static WordCloudChartPanel wordCloudChartPanel;
	int currentPanel;
	int frames;
	
	public LayoutPanel(List<WordCloudPanel> panels,List<WordCloudBarChart> charts,int frames) {
		this.panels=panels;
		this.charts=charts;
		this.frames=frames;
		initUI(panels,charts);
	}

	private void initUI(List<WordCloudPanel> panels,List<WordCloudBarChart> charts) {
		wordCloudsPanel=new WordCloudsPanel(panels,frames);
		wordCloudChartPanel=new WordCloudChartPanel(charts,frames);
		wordCloudChartPanel.setVisible(true);
		setLayout(new BorderLayout());
		add(wordCloudsPanel,BorderLayout.CENTER);
	}
	
	public void showCard(int panel) {
		wordCloudsPanel.showCard(panel);
		wordCloudChartPanel.showCard(panel);
		currentPanel=wordCloudsPanel.currentPanel;
	}
	
	public void play(int nextTick){	
		wordCloudsPanel.play();		 
		wordCloudChartPanel.play(nextTick);
		currentPanel=wordCloudsPanel.currentPanel;
	}

	public void reset() {
		wordCloudsPanel.reset();
		wordCloudChartPanel.reset();
		currentPanel=wordCloudsPanel.currentPanel;
	}
	public void slide(int panel) {
		wordCloudsPanel.slide(panel);
		wordCloudChartPanel.slide(panel);
		currentPanel=wordCloudsPanel.currentPanel;
	}

}
