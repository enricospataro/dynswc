package main.java.layout;

import main.java.geometry.Rectangle;
import main.java.graph.Edge;
import main.java.graph.Graph;
import main.java.graph.Vertex;
import main.java.metrics.AdjacenciesMetric;
import main.java.nlp.Word;
import main.java.nlp.WordPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.knapsack.core.KnapsackInstance;
import main.java.knapsack.strategies.KnapsackFPTAS;

public class SingleStarStrategy extends BaseLayoutStrategy {
    private static final double BETA = 1.01;
    private static final double EPS = 0.01;
    private static final double SIZE_SCALING = 4000;
    private static final double VALUE_SCALING = 400000;

    private Set<Vertex> boxesLeft;
    private Set<Vertex> boxesRight;
    private Set<Vertex> boxesTop;
    private Set<Vertex> boxesBottom;
    private Set<Vertex> boxesNotRealized;

    private Vertex center;
    private Graph graph;

    private Map<Word,Integer> wordIndex;

    public SingleStarStrategy() {}

    public void setGraph(Graph g) {this.graph = g;}

    public double getRealizedWeight() {
        double ret = 0.0;

        for(int i=0;i<words.size();i++)
            for(int j=i+1;j<words.size();j++) {
                if(!AdjacenciesMetric.close(wordPositions.get(i),wordPositions.get(j))) continue;

                Edge edge = graph.getEdge((Vertex)words.get(i),(Vertex)words.get(j));
                ret += graph.getEdgeWeight(edge);
            }
        return ret;
    }

    public List<Vertex> getRealizedVertices() {
        List<Vertex> result = new ArrayList<>();
        
        for(Vertex v:graph.vertexSet()) {
            if(v.equals(center)) {
                result.add(center);
                continue;
            }
            if(!graph.containsEdge(center,v)) continue;
            
            result.add(v);
        }
        return result;
    }

    @Override
    protected void execute() {
        wordIndex = new HashMap<>();
        for(int i=0;i<words.size();i++) wordIndex.put(words.get(i),i);

        createBoundingBoxes();
        
        computeKnapsacks();
        // get the four corner boxes
        Iterator<Edge> it = graph.weightOrderedEdgeIterator(true);
        int found = 0;
        Vertex[] cornerVertices = new Vertex[4];
        while(found<4 && it.hasNext()) {
            Edge e = it.next();
            Vertex child = graph.getOtherSide(e,center);

            if(boxesNotRealized.contains(child)) {
                cornerVertices[found++] = child;
                boxesNotRealized.remove(child);
            }
        }
        
        // center box, position 0,0
        Rectangle centerRect = wordPositions.get(wordIndex.get(center));     
        wordPositions.set(wordIndex.get(center),centerRect);
        
        //sides
        layoutTop(centerRect,found,cornerVertices);
        layoutBottom(centerRect,found,cornerVertices);
        layoutRight(centerRect,found,cornerVertices);
        layoutLeft(centerRect,found,cornerVertices);
        
        //checkSanity();
    }

    private void layoutLeft(Rectangle centerRect,int found,Vertex[] cornerVertices) {
        double taken = 0;
        for(Vertex v:sortBoxes(boxesLeft)) {
            Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            // translate to left and up
            wordRect.move(-1 * wordRect.getWidth(), taken);
            taken += wordRect.getHeight();
        }
        assert(taken <= centerRect.getHeight());
    }

    private void layoutRight(Rectangle centerRect,int found,Vertex[] cornerVertices) {
        double taken = 0;
        for(Vertex v:sortBoxes(boxesRight))  {
        	Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            // translate to right and up
            wordRect.move(centerRect.getWidth(), taken);
            taken += wordRect.getHeight();
        }
        assert(taken <= centerRect.getHeight());
    }

    private void layoutBottom(Rectangle centerRect,int found,Vertex[] cornerVertices) {
        double taken = 0;
        for(Vertex v:sortBoxes(boxesBottom)) {
        	Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            // translate to bottom and right
            wordRect.move(taken, -1 * wordRect.getHeight());
            taken += wordRect.getWidth();
        }
        assert(taken <= centerRect.getWidth());

        // bottom-right
        if(found>3) {
            Vertex v = cornerVertices[3];
            Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            wordRect.move(taken, -1 * wordRect.getHeight());
        }

        // bottom-left
        if(found>0) {
            Vertex v = cornerVertices[0];
            Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            wordRect.move(-1 * wordRect.getWidth(), -1 * wordRect.getHeight());
        }
    }

    private void layoutTop(Rectangle centerRect,int found,Vertex[] cornerVertices) {
        double taken = 0;
        for(Vertex v:sortBoxes(boxesTop)){
        	Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            // translate to top and right
            wordRect.move(taken, centerRect.getHeight());
            taken += wordRect.getWidth();

            assert (wordRect.getWidth()>0);
        }
        assert (taken <= centerRect.getWidth());

        // top-right
        if(found>2) {
            Vertex v = cornerVertices[2];
            Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            wordRect.move(taken, centerRect.getHeight());
        }

        // top-left
        if(found>1) {
            Vertex v = cornerVertices[1];
            Rectangle wordRect = wordPositions.get(wordIndex.get(v));
            wordRect.move(-1 * wordRect.getWidth(), centerRect.getHeight());
        }
    }

    /**
     * find an optimal order of boxes 
     * Aug 29, 2013 
     */
    private List<Vertex> sortBoxes(Set<Vertex> boxes)  {
        List<Vertex> list = new ArrayList<Vertex>(boxes);
        int n = list.size();
        if(n <= 2)  return list;

        if(n >= 8)  return list;

        //just try all possible permutations and choose the best
        int[] perm = new int[n];
        for(int i=0;i<n;i++) perm[i] = i;

        bestWeight = -1;
        shuffleBoxes(0,perm,0,list);

        assert(bestWeight >= 0);

        List<Vertex> result = new ArrayList<Vertex>();
        for(int i=0;i<n;i++)  result.add(list.get(bestOrder[i]));

        return result;
    }

    private double bestWeight;
    private int[] bestOrder;

    private void shuffleBoxes(int curIndex, int[] curOrder, double curWeight, List<Vertex> list) {
        if(curIndex >= list.size()) {
            if(bestWeight >= curWeight) return;

            bestOrder = Arrays.copyOf(curOrder, curOrder.length);
            bestWeight = curWeight;
            return;
        }

        for(int i=curIndex;i<list.size();i++) {
            swap(curOrder, i, curIndex);
            double deltaWeight = 0;
            if(curIndex>0)  {
                WordPair wp = new WordPair(list.get(curOrder[curIndex - 1]),list.get(curOrder[curIndex]));
                deltaWeight += wordGraph.getSimilarity().get(wp);
            }
            shuffleBoxes(curIndex + 1,curOrder,curWeight+deltaWeight,list);
            swap(curOrder, i, curIndex);
        }
    }

    private void swap(int[] array,int i,int j) {
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public void computeKnapsacks() {
        Vertex center = null;

        for(Vertex v:graph.vertexSet()) {
            if(graph.edgesOf(v).size()>1) {
                if(center==null) {
                    // found the center
                    center = v; 
                }
//                else {
//                    // TODO remove
//                    Logger.log("Star\n");
//                    for (Edge e : graph.edgeSet())
//                    {
//                        Logger.print("(" + graph.getEdgeSource(e).word + ")");
//                        Logger.print("-");
//                        Logger.print("(" + graph.getEdgeTarget(e).word + ")");
//                        Logger.print("\n");
//                    }
//                    Logger.log("=========");
//
//                    throw new IllegalArgumentException("Graph is not a star");
//                }
            }
        }
        
        if(center==null) {
            // pair or single vertex
            center = graph.vertexSet().iterator().next();
        }
        
        this.center = center;

        Rectangle centerRect = wordPositions.get(wordIndex.get(center)); 

        final int horizontalSize = (int)Math.round(centerRect.getWidth() * (SIZE_SCALING));
        final int verticalSize = (int)Math.round(centerRect.getHeight() * (SIZE_SCALING));

        assert(horizontalSize>0);
        assert(verticalSize>0);

        final HashSet<Vertex> left = new HashSet<>();
        final HashSet<Vertex> right = new HashSet<>();
        final HashSet<Vertex> top = new HashSet<>();
        final HashSet<Vertex> bottom = new HashSet<>();

        int valueLeft = 0, valueRight = 0, valueTop = 0, valueBottom = 0;

        final ArrayList<Vertex> children = new ArrayList<>(graph.vertexSet().size());
        //		HashMap<Vertex,Integer> heights = new HashMap<Vertex,Integer>();
        //		HashMap<Vertex,Integer> widths = new HashMap<Vertex,Integer>();
        final HashMap<Vertex, Integer> value = new HashMap<>();

        final int[] heights = new int[graph.vertexSet().size()-1];
        final int[] widths = new int[graph.vertexSet().size()-1];

        int k = 0;
        for(Vertex v:graph.vertexSet()) {
            if(v==center) continue;

            Rectangle bb = wordPositions.get(wordIndex.get(v));

            heights[k] = (int) (Math.round(bb.getHeight() * (SIZE_SCALING)) + 1);
            widths[k] = (int) (Math.round(bb.getWidth() * (SIZE_SCALING)) + 1);

            //Logger.log("Putting width: " + widths[k]);
            //Logger.log("Pixel width: " + this.bbGenerator.getBoundingBox(v, v.weight).getWidth());

            assert(heights[k]>0);
            assert(widths[k]>0);

            if(graph.getEdgeWeight(graph.getEdge(v,center))>0) {
                value.put(v, (int)Math.round(graph.getEdgeWeight(graph.getEdge(v, center)) * (VALUE_SCALING)));
                //Logger.log("Putting value: " + (int) Math.round(graph.getEdgeWeight(graph.getEdge(v, center)) * (VALUE_SCALING)));
            }
            else {
                // also try to pack 
                value.put(v,1);
            }
            children.add(v);
            k++;
        }

        class KnapsackSolver {
            KnapsackFPTAS solveFor(Set<Vertex> solveSet, boolean horizontal) {
                KnapsackInstance instance;

                int[] marginalValues = new int[children.size()];

                for(int j=0;j<children.size();j++) {
                    Vertex v = children.get(j);

                    if((left.contains(v)) || (right.contains(v)) || (bottom.contains(v)) || (top.contains(v)))  {
                        // element contained. value only, if in current set.
                        if(solveSet.contains(v)) marginalValues[j] = value.get(v);
                        else marginalValues[j] = 0; // no advantage repacking it                     
                    }
                    else {
                        // element is not contained. Full value.
                        marginalValues[j] = value.get(v);
                    }
                }

                if(horizontal) {
                    int[] widthCopy = widths.clone();
                    instance = new KnapsackInstance(0, children.size(), horizontalSize, widthCopy, marginalValues);
                }
                else  {
                	int[] heightCopy = heights.clone();
                    instance = new KnapsackInstance(0, children.size(), verticalSize, heightCopy, marginalValues);
                }

                KnapsackFPTAS fptas;
                try {
                    fptas = new KnapsackFPTAS(instance, (int)Math.round((1 / (BETA - 1))));
                } catch (CloneNotSupportedException e)  {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                fptas.run();

                return fptas;
            }
        }

        long iterations = Math.round((1.0 / BETA) * graph.vertexSet().size() * Math.log(1/EPS));
        //Logger.log("" + ((double) 1.0 / BETA) * graph.vertexSet().size());
        //Logger.log("Solver iterations: " + iterations + " (" + graph.vertexSet().size() + " elements)");
        KnapsackSolver solver = new KnapsackSolver();
        for(long i=0;i<iterations;i++) {
            // =========
            // top
            // =========
            double topDelta;

            KnapsackFPTAS topFPTAS = solver.solveFor(top, true);
            topDelta = topFPTAS.getKnapsackCost() - valueTop;

            // =========
            // bottom
            // =========
            double bottomDelta;

            KnapsackFPTAS bottomFPTAS = solver.solveFor(bottom, true);
            bottomDelta = bottomFPTAS.getKnapsackCost() - valueBottom;

            // =========
            // left
            // =========
            double leftDelta;

            KnapsackFPTAS leftFPTAS = solver.solveFor(left, false);
            leftDelta = leftFPTAS.getKnapsackCost() - valueLeft;

            // =========
            // right
            // =========
            double rightDelta;

            KnapsackFPTAS rightFPTAS = solver.solveFor(right, false);
            rightDelta = rightFPTAS.getKnapsackCost() - valueRight;

            // Let's see who won!
            double maxDelta = Math.max(Math.max(topDelta, bottomDelta), Math.max(leftDelta, rightDelta));
            if(maxDelta<=0) {
                // No improvement!
                //Logger.log("Top value: " + topFPTAS.getKnapsackCost());
                //Logger.log("Bottom value: " + bottomFPTAS.getKnapsackCost());
                //Logger.log("Left value: " + leftFPTAS.getKnapsackCost());
                //Logger.log("Right value: " + rightFPTAS.getKnapsackCost());
                //Logger.log("Quitting because of lack of improvement.");
                break;
            }

            HashSet<Vertex> winningSet = new HashSet<>();
            int[] winningElements;
            HashSet<Vertex> setToReplace;

            if(maxDelta==topDelta) {
                // top won!
                setToReplace = top;
                valueTop = (int) topFPTAS.getKnapsackCost();
                winningElements = topFPTAS.getKnapsackConfiguration();
            }
            else if(maxDelta==bottomDelta)  {
                setToReplace = bottom;
                valueBottom = bottomFPTAS.getKnapsackCost();
                winningElements = bottomFPTAS.getKnapsackConfiguration();
            }
            else if (maxDelta==leftDelta)   {
                setToReplace = left;
                valueLeft = leftFPTAS.getKnapsackCost();
                winningElements = leftFPTAS.getKnapsackConfiguration();
            }
            else if(maxDelta==rightDelta)   {
                setToReplace = right;
                valueRight = rightFPTAS.getKnapsackCost();
                winningElements = rightFPTAS.getKnapsackConfiguration();
            }
            else {throw new IllegalStateException("Something very strange happened");}

            for(int j=0;j<winningElements.length;j++) {
                winningSet.add(children.get(winningElements[j]));
            }

            //Logger.log(" Winning set size: " + winningSet.size());

            top.removeAll(winningSet);
            bottom.removeAll(winningSet);
            left.removeAll(winningSet);
            right.removeAll(winningSet);

            setToReplace.clear(); // this empties the set;
            setToReplace.addAll(winningSet);
        }

        boxesBottom = bottom;
        boxesLeft = left;
        boxesRight = right;
        boxesTop = top;

        boxesNotRealized = new HashSet<Vertex>();

        for(Vertex v:graph.vertexSet()) {
            if(boxesBottom.contains(v) || boxesLeft.contains(v) || boxesRight.contains(v) || boxesTop.contains(v))
                continue;

            if(center.equals(v))
                continue;

            boxesNotRealized.add(v);
        }
    }

    @Override
    protected LayoutResult createResult() {
        return new LayoutResult(words, wordPositions) {
            @Override
            public Rectangle getWordPosition(Word w)  {
                if (boxesNotRealized.contains(w)) return null;

                return super.getWordPosition(w);
            }
        };
    }

}