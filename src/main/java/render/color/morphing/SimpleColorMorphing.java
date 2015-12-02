package main.java.render.color.morphing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.java.nlp.Word;
import main.java.render.color.ClusterColorHandler;
import main.java.render.color.ColorHandler;
import main.java.render.color.DynamicColorHandler;
import main.java.semantics.ClusterResult;

public class SimpleColorMorphing implements ColorMorphingStrategy{
	
	private int frames;
	private ClusterColorHandler colorHandlerA;
	private ClusterColorHandler colorHandlerB;
	private ClusterResult clusterResultA;
	private ClusterResult clusterResultB;
	private List<Word> commonWords;
	private List<Word> disappearingWords;
	private List<Word> newWords;
	private List<ColorHandler> results;

	
	public SimpleColorMorphing(int frames) {
		this.frames=frames;
	}
	
	@Override
	public List<ColorHandler> morph(ClusterColorHandler colorHandlerB) {
		clusterResultB=colorHandlerB.getClusterResult();
		this.colorHandlerB=colorHandlerB;
		results=new ArrayList<>();
		newWords=new ArrayList<>(clusterResultB.getWords());
		
		for(int i=0;i<frames;i++) results.add(new DynamicColorHandler(colorNewWords(i),newWords,clusterResultB));
		results.add(colorHandlerB);
		
		return results;
	}

	@Override
	public List<ColorHandler> morph(ClusterColorHandler colorHandlerA,ClusterColorHandler colorHandlerB) {
		this.colorHandlerA=colorHandlerA;
		this.colorHandlerB=colorHandlerB;
		clusterResultA=colorHandlerA.getClusterResult();
		clusterResultB=colorHandlerB.getClusterResult();
		results=new ArrayList<>();

		initCommonWords(); 
		initDisappearingwords();
		initNewWords();
		
		for(int i=0;i<frames;i++) results.add(new DynamicColorHandler(colorNewWords(i),colorDisappearingWords(i),
								colorCommonWords(i),commonWords,disappearingWords,newWords,clusterResultA,clusterResultB));	
		results.add(colorHandlerB);
		
		return results;
	}
	
	private Color[][] colorCommonWords(int iter) {
		
		Color[] colorSequence = colorHandlerA.getColorSequence();
		int length = colorSequence.length;
		Color[][] colorMatrix = new Color[length+1][length+1];
		
		for(int i=0;i<length;i++) {
			int oldRedA = colorSequence[i].getRed();
			int oldGreenA = colorSequence[i].getGreen();
			int oldBlueA = colorSequence[i].getBlue();
			
			for(int j=0;j<length;j++) {
				int oldRedB = colorSequence[j].getRed();
				int oldGreenB = colorSequence[j].getGreen();
				int oldBlueB = colorSequence[j].getBlue();
				
				int r = checkColor(iter,oldRedA,oldRedB);
				int g = checkColor(iter,oldGreenA,oldGreenB);
				int b = checkColor(iter,oldBlueA,oldBlueB);	
				
				colorMatrix[i][j] = new Color(r,g,b);
			}
		}
		return colorMatrix;
	}

	private Color[] colorDisappearingWords(int iter) {
		
		Color[] colorSequence = colorHandlerA.getColorSequence();
		int length = colorSequence.length;
		Color[] newSequence = new Color[length+1];
		
		for(int i=0;i<length;i++) {
			int oldRed = colorSequence[i].getRed();
			int oldGreen = colorSequence[i].getGreen();
			int oldBlue = colorSequence[i].getBlue();

			int r = checkColor(iter,oldRed,255);
			int g = checkColor(iter,oldGreen,255);
			int b = checkColor(iter,oldBlue,255);

			newSequence[i]=new Color(r,g,b);
		}
		return newSequence;
	}

	private Color[] colorNewWords(int iter) {
		
		Color[] colorSequence = colorHandlerB.getColorSequence();
		int length = colorSequence.length;
		Color[] newSequence = new Color[length+1];
		
		for(int i=0;i<length;i++) {
			int r = checkColor(iter,255,colorSequence[i].getRed());
			int g = checkColor(iter,255,colorSequence[i].getGreen());
			int b = checkColor(iter,255,colorSequence[i].getBlue()); 
			
			newSequence[i] = new Color(r,g,b);
		}
		return newSequence;
	}
	
	private int checkColor(int iter,double a,double b) {
		double res=a;		
		if(a<b) res = (int)(a+(iter+1)*(b-a)/(frames+1));
		else if(a>b) res = (int)(a-(iter+1)*(a-b)/(frames+1));

		return (int) Math.ceil(res);
	}
	
	private void initCommonWords() {
		commonWords=new ArrayList<>();
		commonWords.addAll(clusterResultB.getWords());
		commonWords.retainAll(clusterResultA.getWords()); 
	}
	private void initDisappearingwords() {disappearingWords=difference(clusterResultA.getWords(),clusterResultB.getWords());}
	private void initNewWords() {newWords=difference(clusterResultB.getWords(),clusterResultA.getWords());}
	
	private List<Word> difference(List<Word> wordsA,List<Word> wordsB) {
		List<Word> difference=new ArrayList<>(wordsA);
		difference.removeAll(wordsB);
		return difference;
	}
}
