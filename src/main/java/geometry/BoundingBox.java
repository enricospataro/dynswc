package main.java.geometry;

import main.java.nlp.Word;
import main.java.utils.AWTFont;

public class BoundingBox {

	private double scale;
	
	public BoundingBox() {scale=1.0;}
	public BoundingBox(double scale) {this.scale=scale;}
	
	public double getScale() {return scale;}
	public void setScale(double scale) {this.scale=scale;}
	
	public Rectangle getBoundingBox(Word word) {
		Rectangle rect = AWTFont.getInstance("Calibri").getBoundingBox(word.getWord());
		rect.scale(word.getScore()*scale);
		return rect;
	}
}