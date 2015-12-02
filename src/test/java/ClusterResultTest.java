package test.java;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.java.nlp.Word;
import main.java.semantics.ClusterResult;
import main.java.semantics.similarity.ClusterPair;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClusterResultTest {

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
	public void testUpdateClusters() {
		Map<Word,Integer> clusters = new HashMap<>();
		Word w1 = new Word("walt",2.0);
		Word w2 = new Word("jesse",2.0);
		Word w3 = new Word("saul",2.1);
		Word w4 = new Word("rust",2.0);
		Word w5 = new Word("marty",2.0);
		Word w6 = new Word("malvo",2.0);
		Word w7 = new Word("lou",2.0);
		Word w8 = new Word("louisiana",2.0);
		Word w9 = new Word("abq",2.0);

		clusters.put(w1,1);	
		clusters.put(w2,1);
		clusters.put(w3,1);
		clusters.put(w4,2);
		clusters.put(w5,2);
		clusters.put(w6,0);
		clusters.put(w7,0);
		clusters.put(w8,2);
		clusters.put(w9,1);
		
		List<Word> medoids = new ArrayList<>();
		ClusterResult res = new ClusterResult(3,medoids, clusters,1);
		
		List<ClusterPair> bestPairs = new ArrayList<>();
		ClusterPair cp1 = new ClusterPair(0,1);
		ClusterPair cp2 = new ClusterPair(2,0);
		bestPairs.add(cp1);
		bestPairs.add(cp2);
	
		// exercise and verify
		System.out.println(res.getClusters());
		res.updateClusters(bestPairs);
		System.out.println(res.getClusters());

		Assert.assertTrue(res.getClusters().get(w1).equals(0));
		Assert.assertTrue(res.getClusters().get(w2).equals(0));
		Assert.assertFalse(res.getClusters().get(w3).equals(1));
		Assert.assertTrue(res.getClusters().get(w6).equals(2));
		Assert.assertTrue(res.getClusters().get(w7).equals(2));
		Assert.assertFalse(res.getClusters().get(w4).equals(0));
	}

}
