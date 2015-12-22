package main.java.metrics;

import java.util.List;

import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;

public interface OverallMetric {
	double getValue(List<WordGraph> wordGraphs,List<LayoutResult> layouts);
}
