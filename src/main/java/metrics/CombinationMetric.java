package main.java.metrics;

import java.util.List;

import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;

public class CombinationMetric implements OverallMetric {
	
	private double a;
	private double b;
	
	public CombinationMetric(double a) {
		this.a=a;
		this.b=1-a; // a + b = 1
	}
	
	@Override
	public double getValue(List<WordGraph> wordGraphs,List<LayoutResult> layouts) {
		double dist=0.0;
		double coherence=0.0;
		int n = layouts.size();
		
		DistortionMetric dm = new DistortionMetric();	
		CoherenceMetric cm = new CoherenceMetric();
		
		for(int i=0;i<n;i++) {			
			dist += a * dm.getValue(wordGraphs.get(i),layouts.get(i));			
			if(i==0) {
				coherence += b * 1.0 ;
				continue;
			}
			coherence += b * cm.getValue(wordGraphs.get(i-1),layouts.get(i-1),wordGraphs.get(i),layouts.get(i));
		}
		return (dist+coherence)/n;
	}
}
