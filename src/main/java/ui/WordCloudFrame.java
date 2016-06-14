package main.java.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.render.RenderedWord;
import main.java.render.color.ColorHandler;

public class WordCloudFrame extends JFrame {

	private static final long serialVersionUID = -2059887037035620200L;
	
	private List<LayoutResult> results;
	private List<ColorHandler> colorHandlers;
	private int frames;
	
	public WordCloudFrame(List<LayoutResult> results,List<ColorHandler> colorHandlers,int frames) {
		super("WordCloud");
		this.results=results;
		this.colorHandlers=colorHandlers;
		this.frames=frames;
		initUI(results,colorHandlers);
	}

	private void initUI(List<LayoutResult> results,List<ColorHandler> colorHandlers) {
		 
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
		createLayout(results,colorHandlers);
		
		setVisible(true); 
//		dispose();
	}
	
	static Dimension getScreenDimension() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		return dim;
	}
	
	private void createLayout(List<LayoutResult> results,List<ColorHandler> colorHandlers) {		
		Dimension dim = getScreenDimension();
		double width=dim.getWidth();
		double height=dim.getHeight();
		
		List<WordCloudPanel> panels = new ArrayList<>();
		List<WordCloudBarChart> charts = new ArrayList<>();
		
		double minX=extractMinX();
		double minY=extractMinY();
		double maxX=extractMaxX();
		double maxY=extractMaxY();
		
		for(int i=0;i<results.size();i++) {
			LayoutResult res = results.get(i);
			List<RenderedWord> rw = RenderedWord.renderWords(res.getWords(),res,colorHandlers.get(i));

			WordCloudPanel panel = new WordCloudPanel(rw,width,height,minX,minY,maxX,maxY);
//			WordCloudBarChart barChart = new WordCloudBarChart(res);
		
			panels.add(panel);
//			charts.add(barChart);
		}
		setJMenuBar(new WordCloudMenuBar(charts));
		WordCloudsContPanel cardsPanel = new WordCloudsContPanel(panels,charts,frames); 
		add(cardsPanel);
 
	}
	private double extractMinX() {
		double min=Double.MAX_VALUE;
		for(int i=0;i<results.size();i++) {

			double newMin;
			List<Rectangle> allRects = new ArrayList<>();
			List<RenderedWord> words = RenderedWord.renderWords(results.get(i).getWords(),results.get(i),colorHandlers.get(i));
			for(RenderedWord rw:words) {
	            Rectangle rect = rw.getBoundingBox();
	            allRects.add(rect);
	        }
			newMin=getMinX(allRects);
			if(newMin<min) min=newMin;
		}
		return min;
	}
	private double extractMaxX() {
		double max=Double.MIN_VALUE;
		for(int i=0;i<results.size();i++) {
			double newMax;
			List<Rectangle> allRects = new ArrayList<>();
			List<RenderedWord> words = RenderedWord.renderWords(results.get(i).getWords(),results.get(i),colorHandlers.get(i));
			for(RenderedWord rw:words) {
	            Rectangle rect = rw.getBoundingBox();
	            allRects.add(rect);
	        }
			newMax=getMaxX(allRects);
			if(newMax>max) max=newMax;
		}
		return max;
	}
	private double extractMinY() {
		double min=Double.MAX_VALUE;
		for(int i=0;i<results.size();i++) {
			double newMin;
			List<Rectangle> allRects = new ArrayList<>();
			List<RenderedWord> words = RenderedWord.renderWords(results.get(i).getWords(),results.get(i),colorHandlers.get(i));
			for(RenderedWord rw:words) {
	            Rectangle rect = rw.getBoundingBox();
	            allRects.add(rect);
	        }
			newMin=getMinY(allRects);
			if(newMin<min) min=newMin;
		}
		return min;
	}
	private double extractMaxY() {
		double max=Double.MIN_VALUE;
		for(int i=0;i<results.size();i++) {
			double newMax;
			List<Rectangle> allRects = new ArrayList<>();
			List<RenderedWord> words = RenderedWord.renderWords(results.get(i).getWords(),results.get(i),colorHandlers.get(i));
			for(RenderedWord rw:words) {
	            Rectangle rect = rw.getBoundingBox();
	            allRects.add(rect);
	        }
			newMax=getMaxY(allRects);
			if(newMax>max) max=newMax;
		}
		return max;
	}
	private double getMinX(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMinX()).min().getAsDouble();
	}
	private double getMinY(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMinY()).min().getAsDouble();
	}
	private double getMaxX(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMaxX()).max().getAsDouble();
	}
	private double getMaxY(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMaxY()).max().getAsDouble();
	}
}