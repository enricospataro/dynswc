package main.java.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import main.java.geometry.Rectangle;
import main.java.main.Manager;
import main.java.utils.AWTFont;;

public class WordCloudRenderer {
	
	private static final double offset = 100;
	private double scaleFactor;
	private double shiftX;
	private double shiftY;
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	private List<RenderedWord> words;
    private double actualWidth;
    private double actualHeight;
    private double width;
    private double height;
    private boolean showRectangles;
	private boolean showText;
	
    public WordCloudRenderer(List<RenderedWord> words,double screenWidth,double screenHeight,double minX,double minY,double maxX,double maxY) {
        this.words=words;
        this.width=screenWidth;
        this.height=screenHeight;
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
        this.showRectangles=false;
        this.showText=true;
    }
    
    public void render(Graphics2D g2d) {
    	 if(words.isEmpty()) return;
    	 computeShiftAndStretchFactors();
    	 renderWords(g2d);
    }  
    public void renderWords(Graphics2D g2d) {
    	if(words.isEmpty()) return;
    	List<Rectangle> rectangles = new ArrayList<>();
    	for(RenderedWord rw:words) {
    		Rectangle positionOnScreen = transformRect(rw.getBoundingBox());
            rectangles.add(positionOnScreen);
    		if(showRectangles) {
    			Rectangle2D rect2D = new Rectangle2D.Double(positionOnScreen.getX(),positionOnScreen.getY(),positionOnScreen.getWidth(),positionOnScreen.getHeight());
    			g2d.setColor(Color.white);
    			g2d.fill(rect2D);
    			g2d.setColor(Color.black);
    			g2d.draw(rect2D);
    		}
    		if(showText) drawText(g2d,rw.getText(),rw.getColor(),positionOnScreen);
    	}
    	setActualWidth(getMaxX(rectangles)-getMinX(rectangles)); 
    	setActualHeight(getMaxY(rectangles)-getMinY(rectangles)); 
    }
	private void drawText(Graphics2D g2d,String text,Color color,Rectangle positionOnScreen) {
		//same as getBoundingBox() in class AWTFont
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		Font font = AWTFont.getInstance("Calibri").getFont();
		FontRenderContext frc = g2d.getFontRenderContext();
		GlyphVector gv = font.createGlyphVector(frc,text);
		Rectangle2D bb = gv.getPixelBounds(frc,0,0);
		
        double xFont = fontSizeX(positionOnScreen,bb);
        double yFont = fontSizeY(positionOnScreen,bb);
        double x = getNewX(positionOnScreen,bb,xFont);
        double y = getNewY(positionOnScreen,bb,yFont);

        //preparing font
        AffineTransform at = new AffineTransform(xFont,0,0,yFont,0,0);
        Font deriveFont = font.deriveFont(at);
        g2d.setFont(deriveFont);
		g2d.setColor(color);
		g2d.drawString(text,(float)x,(float)y);	
	}
	private void computeShiftAndStretchFactors() {
        List<Rectangle> allRects = new ArrayList<>();
        for (RenderedWord rw:words) {
            Rectangle rect = rw.getBoundingBox();
            allRects.add(rect);
        }
      
      double panelWidth = width - 2*offset;
      double panelHeight = height - 2*offset;
        
      double sigmoid =  1/(1+Math.exp(-0.2*Manager.getWords()));
      scaleFactor = sigmoid*Math.min(panelWidth/(maxX-minX),panelHeight/(maxY-minY));
      
      shiftX = -1*minX;
      shiftY = -1*minY;
      shiftX = -1*minX + offset/scaleFactor;
      shiftY = -1*minY + offset/scaleFactor;
    }
	
	private double fontSizeX(Rectangle newRect,Rectangle2D oldRect) {return newRect.getWidth()/oldRect.getWidth();}
	private double fontSizeY(Rectangle newRect,Rectangle2D oldRect) {return newRect.getHeight()/oldRect.getHeight();}
	private double getNewX(Rectangle newRect,Rectangle2D oldRect,double fontSize) {return newRect.getX() - oldRect.getX()*fontSize;}
	private double getNewY(Rectangle newRect,Rectangle2D oldRect,double fontSize) {return newRect.getY() - oldRect.getY()*fontSize;}
	
	private Rectangle transformRect(Rectangle rect) {
		Rectangle ret = new Rectangle();
	    ret.setRect(transformX(rect.getX()), transformY(rect.getY()), scaleFactor*rect.getWidth(), scaleFactor*rect.getHeight());
	    return ret;
	}
	private double transformX(double x) {return scaleFactor*(shiftX + x);}
	private double transformY(double y)	{return scaleFactor*(shiftY + y);}
	
	private double getMaxX(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMaxX()).max().getAsDouble();
	}
	private double getMaxY(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMaxY()).max().getAsDouble();
	}
	private double getMinX(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMinX()).min().getAsDouble();
	}
	private double getMinY(List<Rectangle> rectangles) {
		return rectangles.stream().mapToDouble(r -> r.getMinY()).min().getAsDouble();
	}
	public void setShowRectangles(boolean showRectangles){this.showRectangles=showRectangles;}
    public void setShowText(boolean showText){this.showText=showText;}
    public boolean isShowRectangles(){return showRectangles;}
    public boolean isShowText(){return showText;}

	public double getActualWidth() {return actualWidth;}
	public void setActualWidth(double actualWidth) {this.actualWidth = actualWidth;}
	public double getActualHeight() {return actualHeight;}
	public void setActualHeight(double actualHeight) {this.actualHeight = actualHeight;}

	public double getWidth() {return width;}
	public double getHeight() {return height;}
}

