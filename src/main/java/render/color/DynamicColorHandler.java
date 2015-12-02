package main.java.render.color;

import java.awt.Color;
import java.util.List;

import main.java.nlp.Word;
import main.java.semantics.ClusterResult;

public class DynamicColorHandler implements ColorHandler {
	
	private Color[] newWordsColorSequence;
	private Color[] disappearingWordsColorSequence;
	private Color[][] commonWordsColorMatrix;
	private List<Word> commonWords;
	private List<Word> disappearingWords;
	private List<Word> newWords;
	private ClusterResult clusterResultA;
	private ClusterResult clusterResultB;
	
	public DynamicColorHandler(Color[] newWordsColorSequence,Color[] disappearingWordsColorSequence,
							Color[][] commonWordsColorMatrix, List<Word> commonWords, List<Word> disappearingWords, List<Word> newWords,
							ClusterResult clusterResultA,ClusterResult clusterResultB) {
		
		this.newWordsColorSequence = newWordsColorSequence;
		this.disappearingWordsColorSequence = disappearingWordsColorSequence;
		this.commonWordsColorMatrix = commonWordsColorMatrix;
		this.commonWords = commonWords;
		this.disappearingWords = disappearingWords;
		this.newWords = newWords;
		this.clusterResultA=clusterResultA;
		this.clusterResultB=clusterResultB;
	}
	
	public DynamicColorHandler(Color[] newWordsColorSequence,List<Word> newWords,ClusterResult clusterResultB) {
		this.newWordsColorSequence = newWordsColorSequence;
		this.newWords = newWords;
		this.clusterResultB=clusterResultB;
	}
	
	@Override
	public Color getColor(Word w) {
		Color color=null;

		if(newWords.contains(w)) {		
			Word w0 = clusterResultB.getClusterMap().keySet().stream().filter(cw -> cw.getStem().equals(w.getStem())).findFirst().get();
			int i = clusterResultB.getClusterMap().get(w0);
			color = newWordsColorSequence[i];
			return color;
		}
		else if(disappearingWords.contains(w)) {
			Word w0 = clusterResultA.getClusterMap().keySet().stream().filter(cw -> cw.getStem().equals(w.getStem())).findFirst().get();
			int i = clusterResultA.getClusterMap().get(w0);		
			color = disappearingWordsColorSequence[i];
			return color;
		}
		else if(commonWords.contains(w)) {
			Word w0 = clusterResultA.getClusterMap().keySet().stream().filter(cw -> cw.getStem().equals(w.getStem())).findFirst().get();
			Word w1 = clusterResultB.getClusterMap().keySet().stream().filter(cw -> cw.getStem().equals(w.getStem())).findFirst().get();
			int i = clusterResultA.getClusterMap().get(w0);
			int j = clusterResultB.getClusterMap().get(w1);

			color = commonWordsColorMatrix[i][j];
		}	
		if(color==null) throw new RuntimeException("Cannot find color in " + DynamicColorHandler.class.getName()); 
		
		return color;		
	}
	@Override
	public ClusterResult getClusterResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
