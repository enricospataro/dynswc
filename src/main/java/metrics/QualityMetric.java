package main.java.metrics;

import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;

public interface QualityMetric {
    double getValue(WordGraph wordGraph, LayoutResult layout);
}
