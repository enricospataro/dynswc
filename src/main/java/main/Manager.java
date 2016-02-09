package main.java.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.java.layout.*;
import main.java.layout.morphing.MorphingStrategy;
import main.java.layout.morphing.SimpleMorphing;
import main.java.metrics.CoherenceMetric;
import main.java.metrics.CombinationMetric;
import main.java.metrics.DistortionMetric;
import main.java.metrics.SpaceMetric;
import main.java.render.color.*;
import main.java.render.color.morphing.ColorMorphingStrategy;
import main.java.render.color.morphing.SimpleColorMorphing;
import main.java.semantics.ClusterResult;
import main.java.semantics.ClusterStrategy;
import main.java.semantics.similarity.ClusterJaccardSimilarityStrategy;
import main.java.semantics.similarity.ClusterSimilarityStrategy;
import main.java.ui.WordCloudFrame;
import main.java.utils.FileInputHandler;
import main.java.utils.FileOutputHandler;
import main.java.utils.SrtFileCleaner;
import main.java.utils.TextUtils;
import main.java.nlp.Document;
import main.java.nlp.Word;
import main.java.nlp.WordPair;
import main.java.nlp.sentencedetection.*;
import main.java.nlp.similarity.*;
import main.java.nlp.termranking.*;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;


public class Manager {
	
	public static void main(String[] args) throws IOException {
		File[] files = new File("src/main/resources/speeches/txt").listFiles();
		
		int n=files.length;
		long runningTime=0;

		for(int i=0;i<1;i++) {
			try{
				long startTime = System.nanoTime();
				new Manager().run(files[i+189]);
				long endTime = System.nanoTime();
				runningTime += (endTime-startTime);
			}catch (Exception e){e.printStackTrace();}	
		}
//		
//		double maxDiag = getMaxDiag();
//		
//		double maxDist = Double.MIN_VALUE;
//		double minDist = Double.MAX_VALUE;
//		double maxCoher = Double.MIN_VALUE;
//		double minCoher = Double.MAX_VALUE;
//		
//		for(Result r:results) {
//			r.test(maxDiag);
//			if(maxDist<r.distValue) maxDist = r.distValue;
//			if(minDist>r.distValue) minDist = r.distValue;
//			if(maxCoher<r.coherValue) maxCoher = r.coherValue;
//			if(minCoher>r.coherValue) minCoher = r.coherValue;
//		}		
//		long rankingRunningTime=0;
//		long similRunningTime=0;
//		long morphingRunningTime=0;
//		long clusteringRunningTime=0;
//		long colorMorphingRunningTime=0;
//		long layoutRunningTime=0;
//		long uiRunningTime=0;
//		double distValue=0;
//		double coherValue=0;
//		double bbValue=0; 
//		double chValue=0;
//		
//		for(Result r:results) {
//			r.distValue = normalize(r.distValue,maxDist,minDist);
//			r.coherValue = normalize(r.coherValue,maxCoher,minCoher);
//			distValue += r.distValue;
//			coherValue += r.coherValue;
//			bbValue += r.bbValue;
//			chValue += r.chValue;
//			rankingRunningTime += r.meanRankingRunningTime;
//			similRunningTime += r.meanSimilRunningTime;
//			layoutRunningTime += r.meanRunningTime;
//			morphingRunningTime += r.meanMorphingRunningTime;
//			clusteringRunningTime += r.meanClusteringRunningTime;
//			colorMorphingRunningTime += r.meanColorMorphingRunningTime;
//			uiRunningTime += r.uiRunningTime;
//		}
//		createResult(distValue/n,coherValue/n,bbValue/n,chValue/n,rankingRunningTime/n,similRunningTime/n,
//				layoutRunningTime/n,morphingRunningTime/n,clusteringRunningTime/n,colorMorphingRunningTime/n,
//				uiRunningTime/n,runningTime/n);
	}
	
	private static double normalize(double value, double maxValue, double minValue) {
		return (value-minValue)/(maxValue-minValue);
	}
	private static double getMaxDiag() {
		double max = Double.MIN_VALUE;
		for(Double d:diags) {
			if(max<d) max=d;
		}
		return max;
	}

	private TokenizerME tokenizer;
	private SentenceDetectionStrategy sentDetector;
	private List<String> stopWords;
	private static RankingStrategy rankingStrategy;
	private static SimilarityStrategy similarityStrategy;
	private static LayoutStrategy layoutStrategy;
	private MorphingStrategy morphingStrategy;
	private ClusterSimilarityStrategy clusterSimilarityStrategy;
	private ColorHandler colorHandler;
	private ColorMorphingStrategy colorMorphingStrategy;
	private List<LayoutResult> layoutResults;
	private List<LayoutResult> frameResults;
	private List<WordGraph> wordGraphs;
	private List<ClusterResult> clusterResults;
	private List<ClusterColorHandler> colorHandlers;
	private List<ColorHandler> frameColorHandlers;
	static int parts;
	private static int frames;
	private static int words; 
	private long rankingRunningTime;
	private long similRunningTime;
	private long layoutRunningTime;
	private long morphingRunningTime;
	private long colorMorphingRunningTime;
	private long clusteringRunningTime;
	private long uiRunningTime;
	private static List<Double> diags = new ArrayList<>();
	private static List<Result> results = new ArrayList<>();
	
	private void run(File in) throws IOException { 

		// 1 compute elaboration of a document 	
		File sentModel = new File("src/main/resources/opennlp/en-sent.bin");
//		// Text parsing if the input file is a .srt file
//		File out = File.createTempFile(in.getName(), "_cleaned");
//		cleanFile(in,out);
		
		String text = TextUtils.readFileToString(in);
		
		// Tokenization, stop words removal and stemming of the text
		setSentenceDetector(sentModel);
		File inStopWords = new File("src/main/resources/opennlp/en-stopwords");
		stopWords = getStopWords(inStopWords);
		File tokenModel = new File("src/main/resources/opennlp/en-token.bin");
		tokenizer = getTokenizer(tokenModel);
		
		parts=4;
		List<String> textParts=TextUtils.splitText(text,text.length()/parts);
		
		setRankingStrategy(new TFRanking());
		List<Document> docs=new ArrayList<>();
		String t="";
		for(int i=0;i<textParts.size();i++) {
			t=t + " " +textParts.get(i);
			setWords(120);
			Document doc = computeDocument(t);
			docs.add(doc);
		}

		// 2 compute similarity of extracted words
		setSimilarityStrategy(new ExtendedJaccardSimilarity());
		setLayoutStrategy(new CycleCoverStrategy());
		WordGraph wordGraph=null;
		layoutResults = new ArrayList<>();
		wordGraphs = new ArrayList<>();
		
		for(int i=0;i<docs.size();i++) {
			long startTime = System.nanoTime();
			Map<WordPair,Double> similarity = computeSimilarities(docs.get(i));
			long endTime = System.nanoTime();
			similRunningTime += (endTime-startTime);
			wordGraph = new WordGraph(docs.get(i).getWords(),similarity);
			wordGraphs.add(wordGraph);
			
			// 3 run the layout algorithm
	        long startTime2 = System.nanoTime();
			layoutResults.add(layout(wordGraph));
	        long endTime2 = System.nanoTime();
	        layoutRunningTime += (endTime2 - startTime2); 
		}		
		setFrames(150);
		setMorphingStrategy(new SimpleMorphing(frames));
		
		// execute morphing between wordclouds
		frameResults = new ArrayList<>();
		morph();
		
		// execute clustering of words
		clusterResults = new ArrayList<>();
		colorHandlers = new ArrayList<>();
		setClusterSimilarityStrategy(new ClusterJaccardSimilarityStrategy());
		cluster();

		//execute color morphing
		setColorMorphingStrategy(new SimpleColorMorphing(frames));
		frameColorHandlers = new ArrayList<>();
		morphColors();

		// 4 visualize wordcloud
		long startTime = System.nanoTime();
		visualize(wordGraph,frameResults,frameColorHandlers); 	
		long endTime = System.nanoTime();
		uiRunningTime += (endTime-startTime);
		
		computeBoundingBoxes();
		results.add(new Result(wordGraphs,layoutResults,rankingRunningTime,similRunningTime,layoutRunningTime,
				morphingRunningTime,clusteringRunningTime,colorMorphingRunningTime,uiRunningTime));
	}

	private static void createResult(double dist, double coher,	double bbValue, double chValue,	
			long rankingRunningTime,long similRunningTime,long layoutRunningTime,
			long morphingRunningTime,long clusteringRunningTime,long colorMorphingRunningTime, 
			long uiRunningTime, long runningTime) throws IOException {
		File path = new File("src/main/resources/results");
		File result = File.createTempFile("Test_2_",".txt",path);
		BufferedWriter bw = new BufferedWriter(new FileWriter(result));
		bw.write("Ranking: " + rankingStrategy.toString() + "\r\n" + "Similarity: " + 
				similarityStrategy.toString() + "\r\n" + "Layout: " + layoutStrategy.toString() + "\r\n");
		bw.write("Words: " + getWords() + "\r\n");
		bw.write("DistortionMetric: " + dist + "\r\n");
		bw.write("CoherenceMetric: " + coher + "\r\n");
		bw.write("SpaceMetric BoundingBox: " + bbValue + "\r\n");
		bw.write("SpaceMetric ConvexHull: " + chValue + "\r\n");
		bw.write("Processing RunningTime: " + rankingRunningTime + "\r\n");
		bw.write("Simil RunningTime: " + similRunningTime + "\r\n");
		bw.write("Layout RunningTime: " + layoutRunningTime + "\r\n");
		bw.write("Morphing RunningTime: " + morphingRunningTime + "\r\n");
		bw.write("Clustering RunningTime: " + clusteringRunningTime + "\r\n");
		bw.write("ColorMorphing RunningTime: " + colorMorphingRunningTime + "\r\n");
		bw.write("UI RunningTime: " + uiRunningTime + "\r\n");
		bw.write("Total RunningTime: " + runningTime + "\r\n");
		bw.flush();
		bw.close();
	}

	private static void cleanFile(File in,File out) {
		SrtFileCleaner cleaner = new SrtFileCleaner(new FileInputHandler(in),new FileOutputHandler(out));		
		cleaner.clean();
	}
	
	private static TokenizerME getTokenizer(File tokenModel) {
		TokenizerModel tokenizerModel = null;
		try {
			tokenizerModel = new TokenizerModel(tokenModel);
		} catch (InvalidFormatException e) {e.printStackTrace();} 
		  catch (IOException e) {e.printStackTrace();}
		return new TokenizerME(tokenizerModel);
	}
	
	private static List<String> getStopWords(File inStopWords) {
		FileInputHandler fin = new FileInputHandler(inStopWords);
		List<String> stopWords = new ArrayList<>();
		String line;
		while((line=fin.readLine())!=null) {
			stopWords.add(line);
		}
		return stopWords;
	}
	
	public int getFrames() {return frames;}
	public void setFrames(int frames) {this.frames=frames;}
	
	public static int getWords() {return words;}
	public static void setWords(int words) {Manager.words=words;}
	
	public void setSentenceDetector(File sentModel) {
		SentenceModel sentenceModel = null;
		try {sentenceModel = new SentenceModel(sentModel);} 
		catch(InvalidFormatException e) {e.printStackTrace();} 
	    catch(IOException e) {e.printStackTrace();}
		sentDetector = new ApacheSentenceDetector(sentenceModel);
	}
	
	public void setSentenceDetector() {sentDetector = new SimpleSentenceDetector();}
	public void setRankingStrategy(RankingStrategy ranking) {rankingStrategy=ranking;}
	public void setSimilarityStrategy(SimilarityStrategy similarity) {similarityStrategy=similarity;}
	public void setLayoutStrategy(LayoutStrategy layout) {layoutStrategy=layout;}
	public void setMorphingStrategy(MorphingStrategy morphing) {morphingStrategy=morphing;}
	public void setColorHandler(ColorHandler handler) {colorHandler=handler;}
	public void setClusterSimilarityStrategy(ClusterSimilarityStrategy clusterSimilarity) {clusterSimilarityStrategy=clusterSimilarity;}
	public void setColorMorphingStrategy(SimpleColorMorphing simpleColorMorphing) {colorMorphingStrategy=simpleColorMorphing;}

	private Document computeDocument(String text) {		
		Document doc = new Document(text,sentDetector,tokenizer,new PorterStemmer(),stopWords);
		long startTime = System.nanoTime();
		doc.parse();
		doc.rankFilter(words,rankingStrategy);
		long endTime = System.nanoTime();
		rankingRunningTime += (endTime-startTime); 
		return doc;
	}
	
	private Map<WordPair,Double> computeSimilarities(Document doc) {
		return similarityStrategy.computeSimilarity(doc);
	}
	
	private LayoutResult layout(WordGraph wordGraph) {
		return layoutStrategy.layout(wordGraph);
	}
	
	private void morphColors() {
		long startTime = System.nanoTime();
		frameColorHandlers.addAll(colorMorphingStrategy.morph(colorHandlers.get(0)));
		for(int i=0;i<clusterResults.size()-1;i++) frameColorHandlers.addAll(colorMorphingStrategy.morph(colorHandlers.get(i),colorHandlers.get(i+1)));
		long endTime = System.nanoTime();
		morphingRunningTime += (endTime-startTime);
	}
	
	private void cluster() {
		long startTime = System.nanoTime();
		ClusterColorHandler ch = new ClusterColorHandler(ColorHandlerConstants.colorbrewer_1,clusterSimilarityStrategy);
		ch.initialize(wordGraphs.get(0),null);
		colorHandlers.add(ch);
		clusterResults.add(ch.getClusterResult()); 
		for(int i=1;i<wordGraphs.size();i++) {
			ClusterColorHandler ch1 = new ClusterColorHandler(ColorHandlerConstants.colorbrewer_1,clusterSimilarityStrategy);
			ch1.initialize(wordGraphs.get(i),clusterResults.get(i-1));
			colorHandlers.add(ch1);
			clusterResults.add(ch1.getClusterResult());
		}
		long endTime = System.nanoTime();
		clusteringRunningTime += (endTime-startTime);
	}
	
	private void morph() {
		long startTime = System.nanoTime();
		frameResults.addAll(morphingStrategy.morph(layoutResults.get(0)));
		for(int i=0;i<layoutResults.size()-1;i++) frameResults.addAll(morphingStrategy.morph(layoutResults.get(i),layoutResults.get(i+1)));
		long endTime = System.nanoTime();
		colorMorphingRunningTime += (endTime-startTime);
	}

	private void computeBoundingBoxes() {
		double maxDiag = Double.MIN_VALUE;
		for(LayoutResult l:layoutResults) {
			double temp=l.computeBoundingBox().getDiagonal();
			if(maxDiag<temp) maxDiag=temp;					
		}
		diags.add(maxDiag);
	}
	
	private void visualize(WordGraph wordGraph,List<LayoutResult> frameResults,List<ColorHandler> colorHandlers) {
		new WordCloudFrame(frameResults,colorHandlers,frames);
	}		
}