package test.java.utils;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import main.java.utils.FileInputHandler;
import main.java.utils.OutputHandler;
import main.java.utils.SrtFileCleaner;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestSrtFileCleaner {

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
	public void testClean() {
		//Set-up
		String inputString = "1\n00:00:27,689 --> 00:00:34,689\nIt is a rare honor in this life to follow\none of your heroes. And John Lewis is one\n2\n00:00:40,001 --> 00:00:47,001\nof my heroes.\nNow, I have to imagine that when a younger";
		String expected = "It is a rare honor in this life to follow one of your heroes. And John Lewis is one of my heroes. Now, I have to imagine that when a younger ";
		String actual = "";
		OutputHandler out = new TestOutputHandler(actual);

//		SrtFileCleaner cleaner = new SrtFileCleaner(new FileInputHandler(){
//			BufferedReader inRead = new BufferedReader(new StringReader(inputString));
//			
//			@Override
//			public String readLine() {
//				String s=null;
//				try {
//					s=inRead.readLine();
//				} catch (IOException e) {e.printStackTrace();}
//				return s; 
//			}
//
//			@Override
//			public void close() {}
//
//			@Override
//			public int read() {return 0;} 
//		}, out);
//		
//		//Exercise
//		cleaner.clean();
//		actual=out.toString();
//		//Verify
//		assertEquals(expected,actual);
//		
//		
	}

}
