package test.java.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.java.utils.FileOutputHandler;
import main.java.utils.TextUtils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTextUtils {

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
	public void testSplitText() {
		//setup
		int k1=50;
		String text = "This test should detect the first k1 bytes of a text, eventually until the end of a string or until the end of the text.";
		List<String> expected = new ArrayList<>();
		String expectedFirst = "This test should detect the first k1 bytes of a text,";
		String expectedSecond = " eventually until the end of a string or until the";
		String expectedThird = " end of the text.";
		expected.add(expectedFirst);
		expected.add(expectedSecond);
		expected.add(expectedThird);
		
		//exercise
		List<String> actual = TextUtils.splitText(text,k1);
		//verify
		assertThat(actual, is(expected));
	}
	
	@Test
	public void testReadFileToString() throws IOException {
		//setup
		String expected="This is a test string.";
		File textFile = new File("/home/enricospataro/workspace/tesi/src/test/resources/testTextUtils");
		FileOutputHandler out = new FileOutputHandler(textFile);
		out.write(expected);
		out.close();
		String actual;
		
		//exercise
		actual=TextUtils.readFileToString(textFile);
		
		//verify
		System.out.println(actual);
		assertEquals(expected,actual);
	}

}
