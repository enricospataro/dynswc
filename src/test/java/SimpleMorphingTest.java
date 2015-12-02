package test.java;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import main.java.geometry.BoundingBox;
import main.java.geometry.Rectangle;
import main.java.layout.LayoutResult;
import main.java.layout.morphing.SimpleMorphing;
import main.java.nlp.Word;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleMorphingTest {

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
	public void testMorph() {
		BoundingBox bb = new BoundingBox();
		List<Word> wordsA = new ArrayList<>();
		Word w0 = new Word("America",4.1); w0.setStem("america");wordsA.add(w0);
		Word w1 = new Word("New York",3.9); w1.setStem("new york");wordsA.add(w1);
		Word w2 = new Word("Texas",2.1); w2.setStem("texas");wordsA.add(w2);
		Word w3 = new Word("Italy",4.5); w3.setStem("italy");wordsA.add(w3);
		Word w4 = new Word("Germany",1.8); w4.setStem("germany");wordsA.add(w4);
		Word w5 = new Word("Munich",3.2); w5.setStem("bayern");wordsA.add(w5);
		
		List<Rectangle> rectanglesA = new ArrayList<>();
		Rectangle r0 = bb.getBoundingBox(w0);r0.setRect(10,15,r0.getWidth(),r0.getHeight());rectanglesA.add(r0);
		Rectangle r1 = bb.getBoundingBox(w1);r1.setRect(20,35,r1.getWidth(),r1.getHeight());rectanglesA.add(r1);
		Rectangle r2 = bb.getBoundingBox(w2);r2.setRect(100,55,r2.getWidth(),r2.getHeight());rectanglesA.add(r2);
		Rectangle r3 = bb.getBoundingBox(w3);r3.setRect(102,125,r3.getWidth(),r3.getHeight());rectanglesA.add(r3);
		Rectangle r4 = bb.getBoundingBox(w4);r4.setRect(110,115,r4.getWidth(),r4.getHeight());rectanglesA.add(r4);
		Rectangle r5 = bb.getBoundingBox(w5);r5.setRect(60,95,r5.getWidth(),r5.getHeight());rectanglesA.add(r5);
		LayoutResult resultA = new LayoutResult(wordsA,rectanglesA);
		
		List<Word> wordsB = new ArrayList<>();
		Word w3b = new Word("Italy",2.5); w3b.setStem("italy");wordsB.add(w3b);
		Word w4b = new Word("Germany",5.8); w4b.setStem("germany");wordsB.add(w4b);
		Word w5b = new Word("Munich",1.2); w5b.setStem("bayern");wordsB.add(w5b);
		Word w6 = new Word("England",5.0); w6.setStem("uk"); wordsB.add(w6);
		Word w7 = new Word("London",2.1); w7.setStem("london"); wordsB.add(w7);
		Word w8 = new Word("Rome",1.2); w8.setStem("rm"); wordsB.add(w8);
		
		List<Rectangle> rectanglesB = new ArrayList<>();
		Rectangle r3b = bb.getBoundingBox(w3b);r3b.setRect(15,25,r3b.getWidth(),r3b.getHeight());rectanglesB.add(r3b);
		Rectangle r4b = bb.getBoundingBox(w4b);r4b.setRect(55,25,r4b.getWidth(),r4b.getHeight());rectanglesB.add(r4b);
		Rectangle r5b = bb.getBoundingBox(w5b);r3b.setRect(75,55,r5b.getWidth(),r5b.getHeight());rectanglesB.add(r5b);
		Rectangle r6 = bb.getBoundingBox(w6);r3b.setRect(50,125,r6.getWidth(),r6.getHeight());rectanglesB.add(r6);
		Rectangle r7 = bb.getBoundingBox(w7);r3b.setRect(100,5,r7.getWidth(),r7.getHeight());rectanglesB.add(r7);
		Rectangle r8 = bb.getBoundingBox(w8);r3b.setRect(215,225,r8.getWidth(),r8.getHeight());rectanglesB.add(r8);
		LayoutResult resultB = new LayoutResult(wordsB,rectanglesB);
		
		List<Word>commonWords=new ArrayList<>();
		commonWords.addAll(resultB.getWords());
		commonWords.retainAll(resultA.getWords()); System.out.println(commonWords);
		SimpleMorphing sm = new SimpleMorphing(1);
		
		List<LayoutResult> results=sm.morph(resultA,resultB);
		
		for(LayoutResult res:results) System.out.println(res.getWordPositionsMap());	
		System.out.println(results.get(0).getWordPosition(w2).getCenterX());System.out.println(results.get(1).getWordPosition(w2).getCenterX());
	}
}
