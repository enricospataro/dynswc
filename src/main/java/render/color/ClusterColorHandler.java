package main.java.render.color;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import main.java.layout.WordGraph;
import main.java.nlp.Word;
import main.java.semantics.ClusterResult;
import main.java.semantics.ClusterStrategy;
import main.java.semantics.GenericClusterStrategy;
import main.java.semantics.similarity.ClusterPair;
import main.java.semantics.similarity.ClusterSimilarityStrategy;

public class ClusterColorHandler implements ColorHandler {
	
	private static Color[] colorSequence;
	private ClusterSimilarityStrategy clusterSimilarity;
	private ClusterResult clustering = null;
	private Map<ClusterPair,Double> similarityMap;
	private int[] clusterIndex;
	
	public ClusterColorHandler(Color[] colorSequence,ClusterSimilarityStrategy clusterSimilarity) {
		ClusterColorHandler.colorSequence=colorSequence;
		this.clusterSimilarity=clusterSimilarity;
	}
	
	public ClusterResult getClusterResult() {return clustering;}
	public Color[] getColorSequence() {return colorSequence;}
	
	public Color getColor(Word w) {
		 if(clustering==null) throw new RuntimeException(ClusterColorHandler.class.getName() + " is not initialized"); 
		 int c=clustering.getClusterMap().get(w);	

	     return colorSequence[c];
	}
	
	public void initialize(WordGraph wordGraph,ClusterResult prevResult) {
		ClusterStrategy clusterStrategy = new GenericClusterStrategy(-1,colorSequence.length);
		clustering=clusterStrategy.run(wordGraph);
		
		if(prevResult!=null) updateClusters(prevResult);
	}
	
	private void updateClusters(ClusterResult prevResult) {
		Set<Integer> prevResultGroups = prevResult.getGroups().keySet();
		Set<Integer> clusteringGroups = clustering.getGroups().keySet();
		
		int k = clusteringGroups.size();
		int k_p = prevResultGroups.size();
		
		List<ClusterPair> bestPairs = new ArrayList<>();
		
		similarityMap=clusterSimilarity.computeSimilarity(prevResult,clustering);
		computeBestPairs(bestPairs);
		
		// adds the missing pairs
		if(k_p<k) {
			int length = colorSequence.length;
			List<Boolean> usedIndex1 = new ArrayList<>(Collections.nCopies(length+1,false));		
			for(ClusterPair cp:bestPairs) usedIndex1.set(cp.getFirst(),true);
			
			List<Boolean> usedIndex2 = new ArrayList<>(Collections.nCopies(length+1,false));
			for(ClusterPair cp:bestPairs) usedIndex2.set(cp.getSecond(),true);			
			
			for(int i=0;i<k-k_p;i++) {			
				int new_i=-1;
				int new_j=-1;
				new_i = computeNewIndex(usedIndex1,new_i);
				new_j = computeNewIndex(usedIndex2,new_j);
				usedIndex1.set(new_i,true);
				usedIndex2.set(new_j,true);
				
				bestPairs.add(new ClusterPair(new_i,new_j));
			}
		}
		clustering.updateClusters(bestPairs); 
	}

	private void computeBestPairs(List<ClusterPair> bestPairs) {
		Comparator<? super Entry<ClusterPair, Double>> maxValueComparator = (
				entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
		
	    while(!similarityMap.isEmpty()){
	    	Optional<Entry<ClusterPair,Double>> maxValue = similarityMap.entrySet().stream().max(maxValueComparator);
	    	int index_i=maxValue.get().getKey().getFirst();
	    	int index_j=maxValue.get().getKey().getSecond();
	    	ClusterPair cp = new ClusterPair(index_i,index_j);
	    	bestPairs.add(cp);			

	    	similarityMap.keySet().removeIf(entry -> entry.getFirst().equals(index_i));
	    	similarityMap.keySet().removeIf(entry -> entry.getSecond().equals(index_j));
	    }
	}	

	private int computeNewIndex(List<Boolean> usedIndex, int newIndex) {
		for(boolean b:usedIndex) {
			if(!b) {
				newIndex=usedIndex.indexOf(b);
				continue;
			}
		}
		return newIndex;
	}
	private void sortClusters(List<Word> words)
    {
        int K = clustering.getClusterCount();
        int[] cnt = new int[K];
        for (Word w : words)
            cnt[clustering.getCluster(w)]++;

        clusterIndex = new int[K];
        for (int i = 0; i < K; i++)
            clusterIndex[i] = i;

        for (int i = 0; i < K; i++)
            for (int j = i + 1; j < K; j++)
                if (cnt[clusterIndex[i]] < cnt[clusterIndex[j]])
                {
                    int tmp = clusterIndex[i];
                    clusterIndex[i] = clusterIndex[j];
                    clusterIndex[j] = tmp;
                }

        int[] clusterIndexRev = new int[K];
        for (int i = 0; i < K; i++)
            clusterIndexRev[clusterIndex[i]] = i;

        clusterIndex = clusterIndexRev;
    }
	
	public static void setColorSequence(Color[] colorSequence) {ClusterColorHandler.colorSequence=colorSequence;}
}

//private Color[] colorSequence;
//private ClusterSimilarityStrategy clusterSimilarity;
//private ClusterResult clustering = null;
//Map<ClusterPair,Double> similarityMap;
//
//public ClusterColorHandler(Color[] colorSequence,ClusterSimilarityStrategy clusterSimilarity) {
//	this.colorSequence=colorSequence;
//	this.clusterSimilarity=clusterSimilarity;
//}
//
//public Color getColor(Word w) {
//	 if(clustering==null) throw new RuntimeException(ClusterColorHandler.class.getName() + " is not initialized"); 
//	 int c=clustering.getClusters().get(w);
//	 return colorSequence[c];
//}
//
//public void initialize(WordGraph wordGraph,ClusterResult prevResult) {
//	ClusterStrategy clusterStrategy = new GenericClusterStrategy();
//	clustering=clusterStrategy.cluster(wordGraph);
//	if(prevResult!=null) updateClusters(prevResult);
//}
//
//private void updateClusters(ClusterResult prevResult) {
//	int k_p = prevResult.getK();
//	int k = clustering.getK();
//
//	List<ClusterPair> bestPairs = new ArrayList<>();
//
//	similarityMap=clusterSimilarity.computeSimilarity(prevResult,clustering);
//	Comparator<? super Entry<ClusterPair, Double>> maxValueComparator = (
//			entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue());
//	
//    while(!similarityMap.isEmpty()){
//    	Optional<Entry<ClusterPair,Double>> maxValue = similarityMap.entrySet().stream().max(maxValueComparator);
//    	int index_i=maxValue.get().getKey().getFirst();
//    	int index_j=maxValue.get().getKey().getSecond();
//    	ClusterPair cp = new ClusterPair(index_i,index_j);
//    	bestPairs.add(cp);			
//
//    	similarityMap.keySet().removeIf(entry -> entry.getFirst().equals(index_i));
//    	similarityMap.keySet().removeIf(entry -> entry.getSecond().equals(index_j));
//    }
//    
////	for(int i=0;i<k_p;i++) {
////		ClusterPair bestCp = null;
////		double max=Double.MIN_VALUE;
////		for(int j=0;j<k;j++) {
////			ClusterPair cp = new ClusterPair(i,j);
////			if(!similarityMap.containsKey(cp)) continue;
////			double temp=similarityMap.get(cp);
////			if(temp>max) {
////				max=temp;
////				bestCp=cp;
////			}
////		}
////		if(bestCp!=null) bestPairs.add(bestCp);
////	}
//
//	if(k_p<k) {
//		for(int i=0;i<k-k_p;i++) {
//			List<Boolean> usedIndex1 = new ArrayList<>(Collections.nCopies(k,false));				
//			for(ClusterPair cp:bestPairs) usedIndex1.set(cp.getFirst(),true);
//
//			List<Boolean> usedIndex2 = new ArrayList<>(Collections.nCopies(k,false));
//			for(ClusterPair cp:bestPairs) usedIndex2.set(cp.getSecond(),true);			
//
//			int new_i=-1;
//			int new_j=-1;
//			for(boolean b:usedIndex1) {
//				if(!b) {
//					new_i=usedIndex1.indexOf(b);
//					continue;
//				}
//			}
//			for(boolean b:usedIndex2) {
//				if(!b) {
//					new_j=usedIndex2.indexOf(b);
//					continue;
//				}
//			}
//			bestPairs.add(new ClusterPair(new_i, new_j));
//		}
//	}		   
//	System.out.println(k_p+","+k);
//	clustering.updateClusters(bestPairs); 
//}	
//
//public ClusterResult getClusterResult() {return clustering;}
//}