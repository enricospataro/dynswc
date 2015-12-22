package main.java.metrics;

import java.util.List;
import java.util.Map;

import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.layout.WordGraph;
import main.java.nlp.Word;
import main.java.nlp.WordPair;
import main.java.utils.GeometryUtils;

/*
 *  computes the weighting(normalized) of semantic similarity with geometric similarity
 */

public class DistortionMetric implements QualityMetric {

	@Override
	public double getValue(WordGraph wordGraph, LayoutResult layout) {
		List<Word> words = wordGraph.getWords();
	    Map<WordPair,Double> similarity = wordGraph.getSimilarity();
	        
	    if(words.isEmpty()) return 0;
	    
	    double[][] matrixSimilarity = wordGraph.computeSimilarityMatrix();
        double[][] matrixGeometryDistance = getGeometryDistance(words,similarity,layout);
        
        double dist = computeDistortion(matrixSimilarity,matrixGeometryDistance);
		return dist;
	}

	private double computeDistortion(double[][] matrixSimilarity, double[][] matrixGeometryDistance) {
		int n = matrixSimilarity.length;
		double distance=0.0;
		double value=0.0;
		double sum=0.0;
		
		// va considerata tutta la matrice o solo metà, essendo simmetrica???
		for(int i=0;i<n;i++)
			for(int j=0;j<n;j++) {
				distance = (1 - matrixGeometryDistance[i][j])*(1 - matrixGeometryDistance[i][j]);
				value += distance*matrixSimilarity[i][j];
				sum += matrixSimilarity[i][j];
	        }		
		return value/sum;
	}

	private double[][] getGeometryDistance(List<Word> words,
			Map<WordPair,Double> similarity, LayoutResult layout) {
		
		int n = words.size();
        double[][] matrix = new double[n][n];
        double max=Double.MIN_VALUE;
        double dist=0.0;
        
        for(int i=0;i<n;i++)
           for(int j=0;j<n;j++) {
        	   dist=computeDistance(words.get(i),words.get(j),layout);
               matrix[i][j]=dist;
               if(dist>max) max=dist;
           }       
        normalize(matrix,max);

        return matrix;
	}

	private void normalize(double[][] matrix,double value) {
		for(int i=0;i<matrix.length;i++)
			for(int j=0;j<matrix[i].length;j++)
				matrix[i][j]=matrix[i][j]/value;
	}

	private double computeDistance(Word first,Word second,LayoutResult layout) {
		Rectangle rect1 = layout.getWordPosition(first);
        Rectangle rect2 = layout.getWordPosition(second);
        double dist=GeometryUtils.rectToRectDistance(rect1,rect2);
        
        return dist;
	}

}
