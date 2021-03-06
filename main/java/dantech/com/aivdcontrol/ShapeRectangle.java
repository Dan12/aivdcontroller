package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * Created by Danweb on 5/16/16.
 */
public class ShapeRectangle {

    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private Random rnd;
    private float density;
    private Paint paint = new Paint();
    private boolean isBest = false;

    private double densWeight = 20;
    private double sizeWeight = 0.0001;
    private double simWeight = 0.0005;
    private double fitness = 0;

    public ShapeRectangle(){
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        rnd = new Random();
        density = 0;
    }

    public ShapeRectangle(int ix, int iy, int ax, int ay, double f){
        minX = ix;
        minY = iy;
        maxX = ax;
        maxY = ay;
        rnd = new Random();
        density = 0;
        fitness = f;
    }

    public void drawSquare(Canvas canvas, int[] bestColor){
        if(!isBest)
            paint.setARGB(100, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
        else
            paint.setARGB(100, bestColor[0], bestColor[1], bestColor[2]);
        canvas.drawRect(minX*ObjectDetector.resolution, minY*ObjectDetector.resolution, (maxX + 1) * ObjectDetector.resolution, (maxY + 1) * ObjectDetector.resolution, paint);
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        canvas.drawText(String.format("%.2f", getDensity()), minX*ObjectDetector.resolution+4, maxY*ObjectDetector.resolution-4, paint);
    }

    public void newPoint(int[] p){
        setMinX(p[1]);
        setMinY(p[0]);
        setMaxX(p[1]);
        setMaxY(p[0]);
    }

    public void best(){ isBest = true; }

    private float getDensity(){
        //return ((float)density)/getArea();
        return density;
    }

    public void setFitness(ShapeRectangle prevBest){
        this.fitness = sizeWeight*getArea()+densWeight*density+simWeight*similarity(prevBest);
    }

    public double getFitness(){
        return fitness;
    }

    public double similarity(ShapeRectangle other){
        double distSim = (other.getCenterX()-getCenterX())*(other.getCenterX()-getCenterX())+(other.getCenterY()-getCenterY())*(other.getCenterY()-getCenterY());
        return (ViewContainer.viewWidth*ViewContainer.viewWidth)+(ViewContainer.viewHeight*ViewContainer.viewHeight)-distSim;
    }

    private int getArea(){
        return (maxX-minX)*(maxY-minY)*ObjectDetector.resolution*ObjectDetector.resolution;
    }

    public boolean isValid(){
        return (maxX-minX > ObjectDetector.minShapeDim && maxY-minY > ObjectDetector.minShapeDim && getDensity() > ObjectDetector.minShapeDensity);
    }

    private void setMinX(int m){
        if(m < minX)
            minX = m;
    }

    private void setMinY(int m){
        if(m < minY)
            minY = m;
    }

    private void setMaxX(int m){
        if(m > maxX)
            maxX = m;
    }

    private void setMaxY(int m){
        if(m > maxY)
            maxY = m;
    }

    public void setDensity(float d){
        density = d;
    }

    public int getMinX(){
        return minX;
    }

    public int getMinY(){
        return minY;
    }

    public int getMaxX(){
        return maxX;
    }

    public int getMaxY(){
        return maxY;
    }

    public int getWidth(){
        return maxX-minX;
    }

    public int getHeight(){
        return maxY-minY;
    }

    public int getCheckHeight(){
        return (getHeight()/ObjectDetector.shapeDensCheck)+1;
    }

    public int getCheckWidth(){
        return (getWidth()/ObjectDetector.shapeDensCheck)+1;
    }

    public int getCenterX(){
        return (minX+(maxX-minX)/2)*ObjectDetector.resolution;
    }

    public int getCenterY(){
        return (minX+(maxX-minX)/2)*ObjectDetector.resolution;
    }
}
