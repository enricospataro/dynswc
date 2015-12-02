package main.java.utils;

import main.java.geometry.Rectangle;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class AWTFont{
	
	public static final int DEFAULT_FONT_SIZE = 256;
	private static AWTFont instance;
    private BufferedImage buffImage;
    private Font font;
	    	
   	private AWTFont(String name) {
    	font=new Font("Calibri",Font.BOLD,DEFAULT_FONT_SIZE);
    	buffImage=new BufferedImage(1024,1024,BufferedImage.TYPE_3BYTE_BGR);
    }
   	public static AWTFont getInstance(String name) {
   		if(instance==null) instance=new AWTFont(name);
   		return instance;
   	}
   	public Font getFont() {return font;}
    public Rectangle getBoundingBox(String text) {	    		 
    	FontRenderContext frc = ((Graphics2D)buffImage.getGraphics()).getFontRenderContext();
    	GlyphVector gv = font.createGlyphVector(frc,text);
    	Rectangle2D rect = gv.getPixelBounds(frc,0,0);
    	return new Rectangle(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight());
    }
}
