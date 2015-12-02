package main.java.render.color.morphing;

import java.util.List;

import main.java.render.color.ClusterColorHandler;
import main.java.render.color.ColorHandler;

public interface ColorMorphingStrategy {
	public List<ColorHandler> morph(ClusterColorHandler clusterA,ClusterColorHandler clusterB);
	public List<ColorHandler> morph(ClusterColorHandler clusterB);
}
