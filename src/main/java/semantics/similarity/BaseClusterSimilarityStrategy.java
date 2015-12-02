package main.java.semantics.similarity;

import java.util.Map;

import main.java.semantics.ClusterResult;

public abstract class BaseClusterSimilarityStrategy implements ClusterSimilarityStrategy {

    @Override
    public Map<ClusterPair,Double> computeSimilarity(ClusterResult resultA,ClusterResult resultB) {
        execute(resultA,resultB);
        return getSimilarity();
    }
    protected abstract void execute(ClusterResult resultA,ClusterResult resultB); //template method
    protected abstract Map<ClusterPair,Double> getSimilarity();
}
