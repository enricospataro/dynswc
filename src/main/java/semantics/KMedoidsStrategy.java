package main.java.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.java.layout.WordGraph;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

public class KMedoidsStrategy implements ClusterStrategy {
	
	private int k;
	private List<Word> words;
    private WordGraph wordGraph;   
    private Map<WordPair, Double> similarities;
    
	public KMedoidsStrategy(int k) {this.k=k;}
	
	@Override
	public ClusterResult run(WordGraph wordGraph) {
		assert(k>=1);
		this.words=wordGraph.getWords();
		this.wordGraph=wordGraph;
		this.similarities = wordGraph.getSimilarity();
		 
		int n=words.size();
		if(k>n) setK(n);
		
		return execute();
	}
	private void setK(int k) {this.k=k;}
	
	private ClusterResult execute() {
		int maxAttempts=600/words.size();
        maxAttempts=Math.min(maxAttempts,10);
        maxAttempts=Math.max(maxAttempts,2);

        ClusterResult result=null;
        
		List<Word> medoids=chooseMedoids(k);
		Map<Word,Integer> clusters=groupPoints(medoids);
		double cost=computeCost(medoids,clusters);
		result=updateClusters(medoids,clusters,cost);
		
		boolean changed=true;
		int count=0;
		while(changed && count<maxAttempts) {
			changed=false;
			count++;
			changed=recalculateMedoids(clusters,medoids,result);			
		}
		return result;
	}
	private List<Word> chooseMedoids(int k) {
		List<Word> tempWords = new ArrayList<>();
        tempWords.addAll(words);
        List<Word> initialMedoids = new ArrayList<>();
        for(int i=0;i<k;i++) {
        	int index = new Random().nextInt(tempWords.size());
            initialMedoids.add(tempWords.get(index));
            tempWords.remove(index);
         }
         return initialMedoids;
	}
	private Map<Word,Integer> groupPoints(List<Word> medoids) {
		Map<Word,Integer> clusters = new HashMap<>();
		for(int i=0;i<words.size();i++) {
			double minDist = wordGraph.distance(words.get(i),medoids.get(0));
			int nearestIndex=0;
			for(int j=1;j<medoids.size();j++) {
				double tempDist = wordGraph.distance(words.get(i),medoids.get(j));
				if(tempDist<minDist) {
					minDist=tempDist;
					nearestIndex=j;
				}
			}
			clusters.put(words.get(i),nearestIndex);
		}
		return clusters;
	}
	private List<Word> getGroup(Map<Word,Integer> groups,int index) {
        List<Word> res=new ArrayList<Word>();
        for(Word w:groups.keySet())
        	if(groups.get(w)==index) res.add(w);
        return res;
    }
	private double computeCost(List<Word> medoids,Map<Word,Integer> groups) {
		double cost=0;
		for(int i=0;i<k;i++) {
			List<Word> wordsCluster=new ArrayList<>();
			wordsCluster=getGroup(groups,i);
			Word medoid=medoids.get(i);
			double dist=0;
			for(Word w:wordsCluster) {
				dist=wordGraph.distance(w,medoid);
				cost+=dist*dist;
			}
		}
		return cost;
	}
	private boolean recalculateMedoids(Map<Word,Integer> groups,List<Word> medoids,ClusterResult result) {
		List<Word> temp = new ArrayList<>();
		temp.addAll(medoids);
		
		boolean changed=false;
		double minCost=result.quality();

		List<Word> nonMedoids = new ArrayList<>();
		nonMedoids.addAll(words);
		nonMedoids.removeAll(temp);
		
		if(nonMedoids.size()==0) return false;
		
		//replace a random element in the medoids list with a random element in the non-medoids list
		int index = new Random().nextInt(nonMedoids.size());
		temp.set(new Random().nextInt(k),nonMedoids.get(index));
		Map<Word,Integer> newGroups=groupPoints(temp);	
		double newCost=computeCost(temp,newGroups);
		
		if(newCost<minCost) {
			result=updateClusters(temp,newGroups,newCost);
			changed=true;
		}
		return changed;
	}
	private ClusterResult updateClusters(List<Word> medoids,Map<Word,Integer> newGroups,double cost) {
		return new ClusterResult(words, similarities, newGroups, wordGraph);
	}
}
