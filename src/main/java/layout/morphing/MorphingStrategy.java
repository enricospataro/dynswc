package main.java.layout.morphing;

import java.util.List;

import main.java.layout.LayoutResult;

public interface MorphingStrategy {
	public List<LayoutResult> morph(LayoutResult resultA,LayoutResult resultB);
	public List<LayoutResult> morph(LayoutResult resultB);

}
