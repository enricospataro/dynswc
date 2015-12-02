package main.java.semantics.similarity;

import java.util.Map;

import main.java.semantics.ClusterResult;

public interface ClusterSimilarityStrategy {
	public Map<ClusterPair,Double> computeSimilarity(ClusterResult resultA, ClusterResult resultB);
}
