package main.java.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	long meanLayoutRunningTime;
	long meanMorphingRunningTime;
	long meanClusteringRunningTime;
	long meanColorMorphingRunningTime;
	long meanRunningTime;
	long uiRunningTime;
	long totalRunningTime;
	double bbValue;
	double chValue;
	double[] a={0.0,0.25,0.5,0.75,1.0};
	double[] cmValues = {0.0,0.0,0.0,0.0,0.0};
	double distValue;
	double coherValue;
	double maxDiag;

	public Result(List<WordGraph> wordGraphs, List<LayoutResult> layoutResults,long rankingRunningTime, 
			long similRunningTime, long layoutRunningTime, long morphingRunningTime, long clusteringRunningTime, 
			long colorMorphingRunningTime, long uiRunningTime, long totalRunningTime) {
		
		this.wordGraphs=wordGraphs;
		this.layoutResults=layoutResults;
		this.meanRankingRunningTime=rankingRunningTime/Manager.PARTS;
		this.meanSimilRunningTime=similRunningTime/Manager.PARTS;
		this.meanLayoutRunningTime=layoutRunningTime/Manager.PARTS;
		this.meanMorphingRunningTime=morphingRunningTime/Manager.PARTS;
		this.meanClusteringRunningTime=clusteringRunningTime/Manager.PARTS;
		this.meanColorMorphingRunningTime=colorMorphingRunningTime/Manager.PARTS;
		this.meanRunningTime=layoutRunningTime/Manager.PARTS;
		this.uiRunningTime=uiRunningTime;
		this.totalRunningTime=totalRunningTime;
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
	public String createResult() throws IOException {
		
		String result = distValue + "\r\n";
		result += coherValue + "\r\n";
		result += bbValue + "\r\n";
		result += chValue + "\r\n";
		result += meanRankingRunningTime + "\r\n";
		result += meanSimilRunningTime + "\r\n";
		result += meanLayoutRunningTime + "\r\n";
		result += meanMorphingRunningTime + "\r\n";
		result += meanClusteringRunningTime + "\r\n";
		result += meanColorMorphingRunningTime + "\r\n";
		result += uiRunningTime + "\r\n";
		result += totalRunningTime + "\r\n";
		result += "\r\n";
		
		return result;
	}
}
