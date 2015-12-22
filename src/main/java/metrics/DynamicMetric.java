package main.java.metrics;

import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;

public interface DynamicMetric {
	double getValue(WordGraph wordGraphA, LayoutResult layoutA,WordGraph wordGraphB, LayoutResult layoutB);
}
