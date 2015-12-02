package main.java.semantics.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.nlp.Word;
import main.java.semantics.ClusterResult;

public class ClusterCosineSimilarity extends BaseClusterSimilarityStrategy{
	
	private Map<ClusterPair,Double> similarityMap;

	@Override
	protected void execute(ClusterResult resultA, ClusterResult resultB) {
		similarityMap = new HashMap<ClusterPair,Double>();
		Map<Integer,List<Word>> groupsA = resultA.getGroups();
		Map<Integer,List<Word>> groupsB = resultB.getGroups();
		
		for(Integer i:groupsA.keySet()) {
			List<Word> clusterA = groupsA.get(i);
			for(Integer j:groupsB.keySet()) {				
				List<Word> clusterB = groupsB.get(j); 
				List<Word> commonWords = new ArrayList<>(clusterB);
				commonWords.retainAll(clusterA);
				
				double score=((double)commonWords.size())/Math.sqrt((double)clusterB.size()*clusterA.size());
        		assert (0 <= score && score <= 1.0);
				similarityMap.put(new ClusterPair(i,j),score);
			}
		}
	}

	@Override
	protected Map<ClusterPair, Double> getSimilarity() {return similarityMap;}
}
