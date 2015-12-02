package main.java.layout;

import java.util.List;

import main.java.geometry.Rectangle;

public interface OverlapRemoval<T extends Rectangle> {
    public void run(List<T> wordPositions);
}
