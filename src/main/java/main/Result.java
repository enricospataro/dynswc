package main.java.main;

import java.util.List;

import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;
import main.java.metrics.CombinationMetric;
import main.java.metrics.SpaceMetric;

public class Result {
	
	List<WordGraph> wordGraphs;
	List<LayoutResult> layoutResults;
	long meanRankingRunningTime;
	long meanSimilRunningTime;
	long meanMorphingRunningTime;
	long meanClusteringRunningTime;
	long meanColorMorphingRunningTime;
	long meanRunningTime;
	long uiRunningTime;
	double bbValue;
	double chValue;
	double[] a={0.0,0.25,0.5,0.75,1.0};
	double[] cmValues = {0.0,0.0,0.0,0.0,0.0};
	double distValue;
	double coherValue;
	double maxDiag;

	public Result(List<WordGraph> wordGraphs, List<LayoutResult> layoutResults,long rankingRunningTime, 
			long similRunningTime, long layoutRunningTime, long morphingRunningTime, long clusteringRunningTime, 
			long colorMorphingRunningTime, long uiRunningTime) {
		
		this.wordGraphs=wordGraphs;
		this.layoutResults=layoutResults;
		this.meanRankingRunningTime=rankingRunningTime/Manager.parts;
		this.meanSimilRunningTime=similRunningTime/Manager.parts;
		this.meanMorphingRunningTime=morphingRunningTime/Manager.parts;
		this.meanClusteringRunningTime=clusteringRunningTime/Manager.parts;
		this.meanColorMorphingRunningTime=colorMorphingRunningTime/Manager.parts;
		this.meanRunningTime=layoutRunningTime/Manager.parts;
		this.uiRunningTime=uiRunningTime;
	}
	public void test(double maxDiag) {
		CombinationMetric cm=null;
		for(int i=0;i<a.length;i++) {
			cm = new CombinationMetric(maxDiag,a[i]);
			cmValues[i] += cm.getValue(wordGraphs,layoutResults);		
		}
		distValue = cmValues[4]; 
		coherValue = cmValues[0];
		SpaceMetric sm = new SpaceMetric(false);
		SpaceMetric sm2 = new SpaceMetric(true);
		bbValue = sm.getValue(wordGraphs,layoutResults); 
		chValue = sm2.getValue(wordGraphs,layoutResults); 
	}
}
