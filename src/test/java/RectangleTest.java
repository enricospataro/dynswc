package test.java;

import static org.junit.Assert.*;
import main.java.geometry.Rectangle;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RectangleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIntersection() {
		//setup
		Rectangle r1 = new Rectangle(1,1,5,4);
		Rectangle r2 = new Rectangle(2,2,3,4);
		Rectangle r3 = new Rectangle(1.5,1.5,2,3);
		Rectangle r4 = new Rectangle(5.5,1,2,4);
		Rectangle r5 = new Rectangle(5,8,3,1);
		Rectangle r6 = new Rectangle(-1,-1,2.01,2.01);
		Rectangle r7 = new Rectangle(-5,-5,4.02,4.02);
		
		// exercise and verify
		Assert.assertTrue(r1.intersection(r2,1.0));
		Assert.assertTrue(r1.intersection(r3,1.0));
		Assert.assertFalse(r1.intersection(r4,1.0));
		Assert.assertFalse(r1.intersection(r4,0.5));
		Assert.assertFalse(r2.intersection(r5,1.0));
		Assert.assertFalse(r1.intersection(r2,5.01));
		Assert.assertTrue(r2.intersection(r3,0));
		Assert.assertFalse(r1.intersection(r6,0.01));
		Assert.assertTrue(r7.intersection(r6,0.01));
	}
	
	@Test
	public void testPositionX() {
		//setup
		Rectangle r1 = new Rectangle(1,1,5,4);
		Rectangle r2 = new Rectangle(2,2,3,4);
		Rectangle r3 = new Rectangle(1.5,1.5,2,3);
		Rectangle r4 = new Rectangle(5.5,1,2,4);
		double eps=0.1;
		// exercise and verify
		Assert.assertFalse(Rectangle.positionX(r1,r2,eps));
		Assert.assertTrue(Rectangle.positionX(r1,r4,eps));
		Assert.assertFalse(Rectangle.positionX(r2,r3,eps));
		Assert.assertTrue(Rectangle.positionX(r3,r1,eps));
	}
	
	@Test
	public void testPositionY() {
		//setup
		Rectangle r1 = new Rectangle(1,1,5,4);
		Rectangle r2 = new Rectangle(2,2,3,4);
		Rectangle r3 = new Rectangle(1.5,1.5,2,3);
		Rectangle r4 = new Rectangle(5.5,1,2,4);
		double eps=0.1;
		// exercise and verify
		Assert.assertTrue(Rectangle.positionY(r1,r2,eps));
		Assert.assertFalse(Rectangle.positionY(r4,r3,eps));
		Assert.assertFalse(Rectangle.positionY(r2,r3,eps));
		Assert.assertFalse(Rectangle.positionY(r3,r1,eps));
	}
}
