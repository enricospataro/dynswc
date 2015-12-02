package main.java.graph;

import main.java.nlp.Word;

public class Vertex extends Word {
    private Graph graph;

    public Vertex(String word,double score,Graph graph) {
        super(word,score);
        this.graph=graph;
    }
    public int getDegree() {return graph.degreeOf(this);} 
}
