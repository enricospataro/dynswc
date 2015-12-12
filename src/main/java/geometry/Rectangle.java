package main.java.geometry;

public class Rectangle {
	
	private double x;
	private double y;
	private double width;
	private double height;
	
	public Rectangle(double x,double y,double width,double height) {
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
	}
	public Rectangle(){
		this.setX(0);
		this.setY(0);
		this.setWidth(0);
		this.setHeight(0);
	}

	public double getX() {return x;}
	public void setX(double x) {this.x = x;}

	public double getY() {return y;}
	public void setY(double y) {this.y = y;}

	public double getWidth() {return width;}
	public void setWidth(double width) {this.width = width;}

	public double getHeight() {return height;}
	public void setHeight(double height) {this.height = height;}
	
	public void setRect(double x,double y,double width,double height) {
		setX(x);
		setY(y);
		setWidth(width);
		setHeight(height);
	}
	
	public void scale(double s) {
    	setWidth(width*s);
    	setHeight(height*s);
    }
	
	public double getArea(){return width*height;}
	
	public double getMaxX() {return x+width;}
    public double getMinX() {return x;} // {getX()}???

    public double getMaxY() {return y+height;}
    public double getMinY() {return y;}

    public double getCenterX() {return x + (width/2.0);}
    public double getCenterY() {return y + (height/2.0);}
    public Point getCenter() {return new Point(getCenterX(),getCenterY());}
    public void setCenter(double x, double y) {
        setX(x-(width/2));
        setY(y-(height/2));
    }
    public void incrementCenter(double x,double y) {setCenter(getCenterX()+x,getCenterY()+y);}
    
    public void add(Rectangle rect) {
        double x1 = Math.min(getMinX(),rect.getMinX());
        double y1 = Math.min(getMinY(),rect.getMinY());
        double x2 = Math.max(getMaxX(),rect.getMaxX());
        double y2 = Math.max(getMaxY(),rect.getMaxY());

        setRect(x1,y1,x2-x1,y2-y1);
    }
    
    public void move(double dx,double dy) {setRect(x+dx,y+dy,width,height);}
    public void moveTo(double nx,double ny) {setRect(nx,ny,width,height);}
    
    // returns true if this rectangle intersects rect on both axes of a quantity more than eps
    public boolean intersection(Rectangle rect,double eps) {
    	boolean x = (this.x + eps < rect.x + rect.width) && (rect.x + eps < this.x + this.width);
    	boolean y = (this.y + eps < rect.y + rect.height) && (rect.y + eps < this.y + this.height);
    	return (x && y);
    }	
	// returns true if rectA is on the left with respect to rectB, false otherwise
	public static boolean positionX(Rectangle rectA, Rectangle rectB,double eps) {
		return (rectA.getCenterX() + eps < rectB.getCenterX());
	}
	// returns true if rectA is lower with respect to rectB, false otherwise
	public static boolean positionY(Rectangle rectA, Rectangle rectB,double eps) {
		return (rectA.getCenterY() + eps < rectB.getCenterY());
	}
    public String toString() {
    	return "x = " + x + ", " + "y = " + y + ", " + "width = " + width + ", " + "height = " + height;
    }
}
