package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/**
 * Created by Danweb on 5/16/16.
 */
public class NumberSlider implements Drawable {


    private float val;
    private String name;
    private float minVal;
    private float maxVal;
    private boolean intValue;
    private int xPos;
    private int yPos;
    private int width;
    private int height;
    private int sliderX;
    private int padding = 4;
    private int textSize = 30;
    private TouchListener touchListener;

    public NumberSlider(String t, int mi, int ma, float def, boolean i, int x, int y, int w, int h){
        minVal = mi;
        maxVal = ma;
        if(i)
            ma++;
        if(def < minVal || def > maxVal-1)
            val = (minVal+maxVal)/2;
        else
            val = def;
        xPos = x;
        yPos = y;
        width = w;
        height = h;
        sliderX = (int) Functions.map(val, minVal, maxVal, xPos, xPos + width);
        name = t;
        intValue = i;
    }

    public void addTouchListener(TouchListener tl){
        touchListener = tl;
    }

    public boolean onTouch(MotionEvent e) {
        int touchX = (int)(e.getX()/ViewContainer.density);
        int touchY = (int)(e.getY()/ViewContainer.density);

        if(touchX >= xPos && touchX <= xPos+width && touchY >= yPos && touchY <= yPos+height){
            if(intValue)
                val = (int) Functions.map(e.getX(), xPos, xPos + width, minVal, maxVal);
            else
                val = (float) Functions.map(e.getX(), xPos, xPos + width, minVal, maxVal);
            sliderX = (int) e.getX();
            if(touchListener != null)
                touchListener.onTouch();
            return true;
        }
        return false;
    }

    public float getValue() {
        return val;
    }

    @Override
    public void drawElements(Canvas canvas, Paint paint, float density) {
        paint.setColor(Color.LTGRAY);
        canvas.drawRect((xPos-padding)*density, (yPos-padding)*density, (xPos+width+padding)*density, (yPos+height+padding)*density, paint);

        //slider arrow
        paint.setColor(Color.GRAY);
        Path sliderPath = new Path();
        sliderPath.moveTo((sliderX-height/2)*density, (yPos)*density);
        sliderPath.lineTo((sliderX+height/2)*density, (yPos)*density);
        sliderPath.lineTo((sliderX)*density, (yPos+height)*density);
        canvas.drawPath(sliderPath, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize((textSize/2)*density);
        canvas.drawText(name, (xPos)*density, (yPos-padding*2)*density, paint);
        paint.setTextSize((textSize)*density);
        canvas.drawText(""+val, (xPos+width/2)*density, (yPos+height+textSize)*density, paint);
    }
}
