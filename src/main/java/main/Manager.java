package main.java.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import main.java.layout.*;
import main.java.layout.morphing.MorphingStrategy;
import main.java.layout.morphing.SimpleMorphing;
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
		try{
			new Manager().run();
		}catch (Exception e){e.printStackTrace();}	
	}
	
	private TokenizerME tokenizer;
	private SentenceDetectionStrategy sentDetector;
	private List<String> stopWords;
	private RankingStrategy rankingStrategy;
	private SimilarityStrategy similarityStrategy;
	private LayoutStrategy layoutStrategy;
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
	private int frames;
	private static int words;
	
	private void run() throws IOException {
		
		// 1 compute elaboration of a document 	
		File sentModel = new File("src/main/resources/opennlp/en-sent.bin");
		setSentenceDetector(sentModel);
		File in = new File("src/main/resources/speeches/nucleardeal.srt");
		// Text parsing if the input file is a .srt file
		File out = File.createTempFile(in.getName(), "_cleaned");
		cleanFile(in,out);
		String text = TextUtils.readFileToString(out);

		// Tokenization, stop words removal and stemming of the text
		File inStopWords = new File("src/main/resources/opennlp/en-stopwords");
		stopWords = getStopWords(inStopWords);
		File tokenModel = new File("src/main/resources/opennlp/en-token.bin");
		tokenizer = getTokenizer(tokenModel);
				
		List<String> textParts=TextUtils.splitText(text,text.length()/6);
		
		setWords(30);
		setRankingStrategy(new TFIDFRanking());
		List<Document> docs=new ArrayList<>();
		String t="";
		for(int i=0;i<textParts.size();i++) {
			t=t + " " +textParts.get(i); 
			Document doc = computeDocument(t);
			docs.add(doc);
		}
		
		// 2 compute similarity of extracted words
		setSimilarityStrategy(new JaccardSimilarity());
		setLayoutStrategy(new StarForestStrategy());
		WordGraph wordGraph=null;
		layoutResults = new ArrayList<>();
		wordGraphs = new ArrayList<>();
		
		for(int i=0;i<docs.size();i++) {
			Map<WordPair,Double> similarity = computeSimilarities(docs.get(i));
			wordGraph = new WordGraph(docs.get(i).getWords(),similarity);
			wordGraphs.add(wordGraph);

			// 3 run a layout algorithm
			layoutResults.add(layout(wordGraph));}
		
		setFrames(300);
		setMorphingStrategy(new SimpleMorphing(frames));
		
		// execute morphing between wordclouds
		frameResults = new ArrayList<>();
		frameResults.addAll(morphingStrategy.morph(layoutResults.get(0)));
		for(int i=0;i<layoutResults.size()-1;i++) frameResults.addAll(morphingStrategy.morph(layoutResults.get(i),layoutResults.get(i+1)));
		
		// execute clustering of words
		clusterResults = new ArrayList<>();
		colorHandlers = new ArrayList<>();
		setClusterSimilarityStrategy(new ClusterJaccardSimilarityStrategy());
		
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
		
		setColorMorphingStrategy(new SimpleColorMorphing(frames));
		frameColorHandlers = new ArrayList<>();
		frameColorHandlers.addAll(colorMorphingStrategy.morph(colorHandlers.get(0)));
		for(int i=0;i<clusterResults.size()-1;i++) frameColorHandlers.addAll(colorMorphingStrategy.morph(colorHandlers.get(i),colorHandlers.get(i+1)));
		
		// 4 visualize wordcloud
		visualize(wordGraph,frameResults,frameColorHandlers); 
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
		try {
			sentenceModel = new SentenceModel(sentModel);
		} 
		catch (InvalidFormatException e) {e.printStackTrace();} 
	    catch (IOException e) {e.printStackTrace();}
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
		doc.parse();
		doc.rankFilter(words,rankingStrategy);
		return doc;
	}
	
	private Map<WordPair,Double> computeSimilarities(Document doc) {
		return similarityStrategy.computeSimilarity(doc);
	}
	
	private LayoutResult layout(WordGraph wordGraph) {
		return layoutStrategy.layout(wordGraph);
	}
	
	private void visualize(WordGraph wordGraph,List<LayoutResult> frameResults,List<ColorHandler> colorHandlers) {
		new WordCloudFrame(frameResults,colorHandlers,frames);
	}		
}