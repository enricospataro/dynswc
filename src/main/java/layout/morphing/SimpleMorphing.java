package main.java.layout.morphing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.geometry.BoundingBox;
import main.java.geometry.Rectangle;
import main.java.layout.ForceDirectedOverlapRemoval;
import main.java.layout.ForceDirectedUniformity;
import main.java.layout.LayoutResult;
import main.java.nlp.Word;

public class SimpleMorphing implements MorphingStrategy {
	
	private LayoutResult resultA;
	private LayoutResult resultB;
	private int frames;
	private BoundingBox boundingBox;
	private List<Word> commonWords;
	private List<Word> disappearingWords;
	private List<Word> newWords;
	private List<LayoutResult> results;
	
	public SimpleMorphing(int frames) {
		this.frames=frames;
		boundingBox=new BoundingBox();
	}
	
	// initial morphing(only new words appear)
	@Override
	public List<LayoutResult> morph(LayoutResult resultB) {
		results=new ArrayList<>();
		this.resultB=resultB;
		newWords=new ArrayList<>(resultB.getWords());
		
		for(int i=0;i<frames;i++) results.add(new LayoutResult(morphNewWords(i)));
		results.add(resultB);
		
		return results;
	}
	
	@Override
	public List<LayoutResult> morph(LayoutResult resultA,LayoutResult resultB) {
		results=new ArrayList<>();
		this.resultA=resultA;
		this.resultB=resultB;
		initCommonWords(); 
		initDisappearingwords();
		initNewWords();
		
		for(int i=0;i<frames;i++) results.add(run(i));	
		results.add(resultB);
		
		return results;
	}
	private LayoutResult run(int iter) {
		Map<Word,Rectangle> result=new HashMap<>();		
		result.putAll(morphCommonWords(iter));	
		result.putAll(morphDisappearingWords(iter));
		result.putAll(morphNewWords(iter));
//		List<Rectangle> rec = new ArrayList<>();
//		for(Rectangle r:result.values()) rec.add(r);
//		new ForceDirectedOverlapRemoval<Rectangle>(0.01).run(rec);
//		new ForceDirectedUniformity<Rectangle>(0.2).run(rec);
		return new LayoutResult(result);
	}
	private Map<Word,Rectangle> morphCommonWords(int iter) {
		Map<Word,Rectangle> positionsResult=new HashMap<>();
		for(Word cw:commonWords) {
			Word cwA=resultA.getWordPositionsMap().keySet().stream().filter(w -> w.getStem().equals(cw.getStem())).findFirst().get();
			Rectangle rectA=resultA.getWordPosition(cwA);
			Rectangle rectB=resultB.getWordPosition(cw);
			Word cloneA=(Word)cwA.clone();

			double initialScore=cwA.getScore();
			double finalScore=cw.getScore();
			if(initialScore<finalScore)	cloneA.incrementScore((iter+1) * (finalScore-initialScore)/(frames+1));
			if(initialScore>finalScore) cloneA.decrementScore((iter+1) * (initialScore-finalScore)/(frames+1));

			Rectangle rect = handleRects(iter,rectA,rectB,cloneA);
			positionsResult.put(cloneA,rect);
		}
		return positionsResult;
	}

	private Rectangle handleRects(int iter,Rectangle rectA,Rectangle rectB,Word cloneA) {
		
		Rectangle rect=boundingBox.getBoundingBox(cloneA);
		
		double eps=0.1;
		if(Rectangle.positionX(rectA,rectB,eps) && Rectangle.positionY(rectA,rectB,eps)) {
			rect.setCenter(rectA.getCenterX()+((iter+1) * (rectB.getCenterX()-rectA.getCenterX())/(frames+1)),
						   rectA.getCenterY()+((iter+1) * (rectB.getCenterY()-rectA.getCenterY())/(frames+1)));
		}
		if(Rectangle.positionX(rectA,rectB,eps) && !Rectangle.positionY(rectA,rectB,eps)) {
			rect.setCenter(rectA.getCenterX()+((iter+1) * (rectB.getCenterX()-rectA.getCenterX())/(frames+1)),
						   rectA.getCenterY()-((iter+1) * (rectA.getCenterY()-rectB.getCenterY())/(frames+1)));
		}
		if(!Rectangle.positionX(rectA,rectB,eps) && Rectangle.positionY(rectA,rectB,eps)) {
			rect.setCenter(rectA.getCenterX()-((iter+1) * (rectA.getCenterX()-rectB.getCenterX())/(frames+1)),
						   rectA.getCenterY()+((iter+1) * (rectB.getCenterY()-rectA.getCenterY())/(frames+1)));
		}
		if(!Rectangle.positionX(rectA,rectB,eps) && !Rectangle.positionY(rectA,rectB,eps)) {
			rect.setCenter(rectA.getCenterX()-((iter+1) * (rectA.getCenterX()-rectB.getCenterX())/(frames+1)),
						   rectA.getCenterY()-((iter+1) * (rectA.getCenterY()-rectB.getCenterY())/(frames+1)));
		}
		return rect;
	}
	private Map<Word,Rectangle> morphDisappearingWords(int iter) {
		Map<Word,Rectangle> positionsResult=new HashMap<>();
		for(Word dw:disappearingWords) {
			double initialScore=dw.getScore();
			Word clone = (Word)dw.clone();
			clone.decrementScore((iter+1) * initialScore/(frames+1));
			
			double centerX=resultA.getWordPosition(dw).getCenterX();
			double centerY=resultA.getWordPosition(dw).getCenterY();
			Rectangle rect=boundingBox.getBoundingBox(clone);
			rect.setCenter(centerX,centerY);
			
			positionsResult.put(clone,rect);
		}
		return positionsResult;
	}

	private Map<Word,Rectangle> morphNewWords(int iter) {
		Map<Word,Rectangle> positionsResult=new HashMap<>();
		for(Word nw:newWords) {
			double finalScore=nw.getScore();
			Word clone = (Word)nw.clone();
			clone.setScore(0);
			clone.incrementScore((iter+1) * finalScore/(frames+1));
			
			double centerX=resultB.getWordPosition(nw).getCenterX();
			double centerY=resultB.getWordPosition(nw).getCenterY();
			Rectangle rect=boundingBox.getBoundingBox(clone);
			rect.setCenter(centerX,centerY);
			
			positionsResult.put(clone,rect);
		}
		return positionsResult;
	}
	
	private void initCommonWords() {
		commonWords=new ArrayList<>();
		commonWords.addAll(resultB.getWords());
		commonWords.retainAll(resultA.getWords());
	}
	private void initDisappearingwords() {disappearingWords=difference(resultA.getWords(),resultB.getWords());}
	private void initNewWords() {newWords=difference(resultB.getWords(),resultA.getWords());}
	
	private List<Word> difference(List<Word> wordsA,List<Word> wordsB) {
		List<Word> difference=new ArrayList<>(wordsA);
		difference.removeAll(wordsB);
		return difference;
	}
	public Rectangle getBoundingBox(Word word) {return boundingBox.getBoundingBox(word);}
}