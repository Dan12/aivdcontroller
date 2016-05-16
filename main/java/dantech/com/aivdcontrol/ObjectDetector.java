package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Danweb on 5/16/16.
 */
public class ObjectDetector {

    public static int resolution = 4;
    public static int tolerance = 35;
    public static int minShapeDim = 4;
    public static float minShapeDensity = 0.5f;
    public static int shapeDensCheck = 10;

    private ProcessedImage pimg;
    private int[] target = new int[]{151, 53, 42};
    private int targetRow = -1;
    private int targetCol = -1;
    private ArrayList<ShapeRectangle> shapes;
    private ShapeRectangle bestRect;
    private int width;
    private int height;
    private int[] primaryColor;

    public ObjectDetector(int[] c){
        shapes = new ArrayList<ShapeRectangle>();
        primaryColor = new int[]{c[0], c[1], c[2]};
    }

    public void setDims(int w, int h){
        width = w;
        height = h;
    }

    public void updateRoutine(int[] rgb){
        long st = System.nanoTime();
        if(targetCol != -1 && targetRow != -1) {
            target = Functions.getRGB(rgb, targetRow, targetCol, width);
            targetRow = -1;
            targetCol = -1;
        }
        shapes = EdgeDetect.runRoutine(rgb, target, width, height);
        setBestRect();

        long et = System.nanoTime();
        if(FoxScreen.verbose)
            System.out.println("Edge Detect run in "+((et-st)/1000000)+"ms");
    }

    // TODO: incorporate previous position in fitness function
    private void setBestRect(){
        if(!shapes.isEmpty()) {
            bestRect = shapes.get(0);
            for (int i = 1; i < shapes.size(); i++)
                if (shapes.get(i).getFitness() > bestRect.getFitness())
                    bestRect = shapes.get(i);
            bestRect.best();
        }else{
            bestRect = null;
        }
    }

    public void drawObjectDetector(Canvas canvas){
        for(ShapeRectangle s : shapes)
            s.drawSquare(canvas, primaryColor);
    }

    public void touchDown(MotionEvent e){
        if(e.getX() <= width*ObjectDetector.resolution && e.getY() <= height*ObjectDetector.resolution){
            targetCol = ((int)e.getX())/ObjectDetector.resolution;
            targetRow = ((int)e.getY())/ObjectDetector.resolution;
        }
    }

    public ShapeRectangle getBestShapeRect(){
        return bestRect;
    }
}
