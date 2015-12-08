package main.java.graph;

import main.java.nlp.Word;

public class Vertex extends Word {
	
	private Graph g;

    public Vertex(String word,double weight,Graph g) {
        super(word,weight);
        this.g=g;
    }
    public int getDegree() {return this.g.degreeOf(this);}
}
