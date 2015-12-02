package main.java.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.nlp.Word;
import main.java.render.color.ColorHandler;

public class RenderedWord {
	
	private String text;
	private Rectangle boundingBox;
	private Color color;
	
	public String getText() {return text;}
	public void setText(String text) {this.text=text;}
	
	public Rectangle getBoundingBox() {return boundingBox;}
	public void setBoundingBox(Rectangle r) {this.boundingBox=r;}
	
	public Color getColor() {return color;}
	public void setColor(Color c) {this.color=c;}
	
	public static List<RenderedWord> renderWords(List<Word> words,LayoutResult layout,ColorHandler colorHandler) {
		List<RenderedWord> result = new ArrayList<>();
		for(Word w:words) {
			RenderedWord rw = new RenderedWord();
			rw.setText(w.getWord());
			rw.setBoundingBox(layout.getWordPosition(w));
			rw.setColor(colorHandler.getColor(w));
			result.add(rw);
		}
		return result;
	}
}
