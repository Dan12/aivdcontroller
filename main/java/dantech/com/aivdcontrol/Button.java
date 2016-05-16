package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

/**
 * Created by Danweb on 5/14/16.
 */
public class Button implements Drawable{

    private int xPos, yPos, width, height;
    private String text;
    private TouchListener touchListener;
    private Paint shadowPaint;

    public Button(int x, int y, String t){
        xPos = x;
        yPos = y;
        width = 200;
        height = 50;
        text = t;

        if(xPos == -1)
            xPos = ((int)ViewContainer.densViewWidth-width)/2;
    }

    public void touchEvent(MotionEvent event){
        float x = event.getX();
        float y = event.getY();
        if(x > xPos && x < xPos+width && y > yPos && y < yPos+height){
            touchListener.onTouch();
        }
    }

    public void setTouchListener(TouchListener tl){
        touchListener = tl;
    }

    public void drawElements(Canvas canvas, Paint paint, float density) {
        if(shadowPaint == null){
            shadowPaint = new Paint();
            shadowPaint.setShadowLayer(10.0f, 0.0f, 2.0f*density, 0xFF000000);
        }

        canvas.drawRect(xPos*density,yPos*density,(xPos+width)*density,(yPos+height)*density, shadowPaint);
        paint.setColor(Color.BLUE);
        canvas.drawRect(xPos*density,yPos*density,(xPos+width)*density,(yPos+height)*density, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(34*density);
        canvas.drawText(text, (xPos+5)*density, (yPos+height-14)*density, paint);
    }
}