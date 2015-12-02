package main.java.semantics;

import main.java.layout.WordGraph;

public interface ClusterStrategy {
	public ClusterResult run(WordGraph wordGraph);
}
