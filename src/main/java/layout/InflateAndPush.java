package main.java.layout;

import java.util.stream.IntStream;

import main.java.geometry.BoundingBox;
import main.java.geometry.Rectangle;

/**
 * @author spupyrev
 * May 13, 2013
 */
public class InflateAndPush extends BaseLayoutStrategy
{
    public InflateAndPush()
    {
    }

    @Override
    protected void execute()
    {
        double scale = 0.01;
        initialPlacement(scale);

        while (scale < 1.0)
        {
            //grow by 5%
            double delta = 0.1;
            double newScale = Math.min(scale + delta, 1.0);
            inflateRectangles(newScale / scale);
            scale = newScale;

            //remove overlaps
            new ForceDirectedOverlapRemoval<>(0.3).run(wordPositions);
            
        }
        new ForceDirectedOverlapRemoval<>(0.3).run(wordPositions);
        new ForceDirectedUniformity<>(5000).run(wordPositions);
    }

    private void initialPlacement(double scale)
    {
        //find initial placement by mds layout
        MDSStrategy algo = new MDSStrategy();
        algo.setBoundingBox(new BoundingBox(scale));
        LayoutResult initialLayout = algo.layout(wordGraph);

        IntStream.range(0, words.size()).forEach(i -> wordPositions.add(initialLayout.getWordPosition(words.get(i))));
    }

    private void inflateRectangles(double scaleFactor)
    {
        for (int i = 0; i < wordPositions.size(); i++)
        {
            Rectangle rect = wordPositions.get(i);
            double newWidth = rect.getWidth() * scaleFactor;
            double newHeight = rect.getHeight() * scaleFactor;
            Rectangle newRect = new Rectangle(rect.getX(), rect.getY(), newWidth, newHeight);
            newRect.setCenter(rect.getCenterX(), rect.getCenterY());

            wordPositions.get(i).setX(newRect.getX());
            wordPositions.get(i).setY(newRect.getY());
            wordPositions.get(i).setWidth(newRect.getWidth());
            wordPositions.get(i).setHeight(newRect.getHeight());
        }
    }
}
