package main.java.layout;

import main.java.geometry.Rectangle;
import main.java.nlp.WordPair;

import java.util.stream.IntStream;

/**
 * @author spupyrev
 * May 15, 2013
 */
public class SinglePathStrategy extends BaseLayoutStrategy {
	
    public SinglePathStrategy(){}

    @Override
    public void execute() {
        //remove the lightest edge
        generatePath();

        //create rectangles
        createBoundingBoxes();

        wordPositions.get(0).move(0, 0);

        // -> 0
        // down 1
        // <- 2
        // up 3
        int prevDir = 3;
        for(int i=1;i<words.size();i++) {
            int dir = prevDir + 1;
            while(true){
                if(tryLayout((dir + 4) % 4, i))  break;
                dir--;

                //if smth goes wrong..
                //do an old (safe) strategy
                if(dir<4) {
//                    Logger.log("can't layout the path with length = " + words.size());
                    LayoutResult singleCycleLayout = new SingleCycleStrategy().layout(wordGraph);
                    IntStream.range(0,words.size()).forEach(index -> wordPositions.add(singleCycleLayout.getWordPosition(words.get(index))));

                    return;
                }
            }
            prevDir = dir;
        }
    }

    private boolean tryLayout(int dir, int now) {
        int MAX = 10;

        Rectangle prevRec = wordPositions.get(now - 1);

        if(dir==0) {
            for(int i=0;i<MAX;i++) {
                double x = prevRec.getMaxX();
                double y = prevRec.getMinY() + i * prevRec.getHeight() / MAX;
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }

            for(int i=MAX-1;i>=0;i--) {
                double x = prevRec.getMaxX();
                double y = prevRec.getMinY() + i * prevRec.getHeight() / MAX - wordPositions.get(now).getHeight();
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }
        } else if(dir==1) {
            for(int i=0;i<MAX;i++) {
                double x = prevRec.getMinX() + i * prevRec.getWidth() / MAX;
                double y = prevRec.getMaxY();
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }

            for(int i=MAX-1;i>=0;i--) {
                double x = prevRec.getMinX() + i * prevRec.getWidth() / MAX - wordPositions.get(now).getWidth();
                double y = prevRec.getMaxY();
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }
        } else if(dir==2) {
            for(int i=0;i<MAX;i++) {
                double x = prevRec.getMinX() - wordPositions.get(now).getWidth();
                double y = prevRec.getMinY() + i * prevRec.getHeight() / MAX;
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }

            for(int i=MAX-1;i>=0;i--) {
                double x = prevRec.getMinX() - wordPositions.get(now).getWidth();
                double y = prevRec.getMinY() + i * prevRec.getHeight() / MAX - wordPositions.get(now).getHeight();
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }
        } else if(dir==3)  {
            for(int i=0;i<MAX;i++) {
                double x = prevRec.getMinX() + i * prevRec.getWidth() / MAX;
                double y = prevRec.getMinY() - wordPositions.get(now).getHeight();
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }

            for(int i=MAX-1;i>=0;i--) {
                double x = prevRec.getMinX() + i * prevRec.getWidth() / MAX - wordPositions.get(now).getWidth();
                double y = prevRec.getMinY() - wordPositions.get(now).getHeight();
                if(canPlace(now,x,y)) {
                    wordPositions.get(now).move(x, y);
                    return true;
                }
            }
        }
        
        return false;
    }

    private boolean canPlace(int now, double x, double y) {
        Rectangle r = new Rectangle(x,y,wordPositions.get(now).getWidth(),wordPositions.get(now).getHeight());
        for(int i=0;i<now;i++)
           if(wordPositions.get(i).intersection(r,0.0)) return false;
        return true;
    }

    private void generatePath()  {
        int bestIndex = -1;
        double minWeight = Double.MAX_VALUE;

        int n = words.size();
        if(n<=2) return;

        for(int i=0;i<n;i++) {
        	WordPair wp = new WordPair(words.get(i),words.get((i+1) % n));
            double weight = similarity.get(wp);
            if(bestIndex == -1 || weight < minWeight) {
                minWeight = weight;
                bestIndex = i;
            }
        }

        assert(bestIndex != -1);

        wordGraph.reorderWords(bestIndex);
//        words = wordGraph.convertWordsToArray();
//        similarity = wordGraph.convertSimilarityToArray();
        words = wordGraph.getWords();
        similarity = wordGraph.getSimilarity();
    }
}
