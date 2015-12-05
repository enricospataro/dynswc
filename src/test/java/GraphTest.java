package test.java;

import static org.junit.Assert.*;
import main.java.graph.Graph;
import main.java.graph.Vertex;

import org.junit.Test;


public class GraphTest {

	@Test
	public void test() {

		Graph g = new Graph();
		
		Vertex a = new Vertex("A",0.6,g);
		Vertex b = new Vertex("B",0.53,g);
		Vertex c = new Vertex("C",0.15,g);
		Vertex d = new Vertex("D",0.25,g);

		g.addVertex(a);
		g.addVertex(b);
		g.addVertex(c);
		g.addVertex(d);
		
		g.addEdge(a,b);
		g.addEdge(c,d);
		g.addEdge(a,d);
		g.addEdge(b,c);
		
		System.out.println(g);
	}

}
