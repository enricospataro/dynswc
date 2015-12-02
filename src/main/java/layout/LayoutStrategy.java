package main.java.layout;

import main.java.geometry.BoundingBox;

public interface LayoutStrategy {
	public void setBoundingBox(BoundingBox bb);
	public void setAspectRatio(double ar);
	public LayoutResult layout(WordGraph wordGraphs);
}
