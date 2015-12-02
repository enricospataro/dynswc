package main.java.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import main.java.render.RenderedWord;
import main.java.render.WordCloudRenderer;

public class WordCloudPanel extends JPanel{

	private static final long serialVersionUID = -3977741581283775405L;
	
	private WordCloudRenderer renderer;
	
	public WordCloudPanel(List<RenderedWord> words,double width,double height,double minX,double minY,double maxX,double maxY) {
		this.setRenderer(new WordCloudRenderer(words,width,height,minX,minY,maxX,maxY));
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	@Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        renderer.render(g2d);
	}
	public WordCloudRenderer getRenderer() {return renderer;}
	public void setRenderer(WordCloudRenderer renderer) {this.renderer=renderer;}
	
	public void setShowRectangles(boolean showRectangles) {renderer.setShowRectangles(showRectangles);}
	public void setShowText(boolean showText) {renderer.setShowText(showText);}
	public boolean isShowRectangles(){return renderer.isShowRectangles();}
	public boolean isShowText(){return renderer.isShowText();} 
}
