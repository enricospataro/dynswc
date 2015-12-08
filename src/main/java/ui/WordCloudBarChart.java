package main.java.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import main.java.layout.LayoutResult;
import main.java.main.Manager;
import main.java.nlp.Word;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class WordCloudBarChart {
	
	List<Word> rankedWords;
	int length;
	DefaultCategoryDataset dataset;
	JFreeChart chart;
	ChartPanel chartPanel;
	
	public WordCloudBarChart(LayoutResult result) {
		
		rankedWords = result.getWords();
		Collections.sort(rankedWords,Comparator.reverseOrder());

		this.length=Manager.getWords();
		if(length>10) length=10;
		
        CategoryDataset dataset = createDataset();
        chart = createChart(dataset);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(700,300));
    }
	
	private CategoryDataset createDataset() {
		dataset = new DefaultCategoryDataset();
		for(int i=0;i<length;i++) {
			dataset.addValue(rankedWords.get(i).getScore(), "", rankedWords.get(i).getWord());
		}
		return dataset;
	}
	
	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart("","","",dataset,PlotOrientation.HORIZONTAL,false,false,false);
		
		CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248,248,255));
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(139,137,137));
        plot.getDomainAxis().setTickLabelFont(new Font("Calibri",Font.BOLD,12));
        
        ValueAxis range = plot.getRangeAxis();
        range.setLabel("Score");
        range.setTickMarksVisible(false);
        ((NumberAxis)range).setTickUnit(new NumberTickUnit(0.5));  
        range.setUpperBound(5.0);
        range.setLowerBound(0.0);

		// disable bar outlines
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false); 
        renderer.setSeriesPaint(0,new GradientPaint(0.0f, 0.0f, Color.BLUE,0.0f, 0.0f, Color.LIGHT_GRAY));
       
		return chart;
	}
	
	public ChartPanel getChartPanel() {return chartPanel;}
}