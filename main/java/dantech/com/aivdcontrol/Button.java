package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

        if(xPos == -1) {
            if(ViewContainer.densViewHeight > ViewContainer.densViewWidth || text.equals("Back"))
                xPos = ((int) ViewContainer.densViewWidth - width) / 2;
            else {
                xPos = 30;
                if(yPos > 210){
                    yPos-=210;
                    xPos+=250;
                }
            }
        }
    }

    public void touchEvent(MotionEvent event){
        float x = event.getX()/ViewContainer.density;
        float y = event.getY()/ViewContainer.density;
        if(x > xPos && x < xPos+width && y > yPos && y < yPos+height){
            touchListener.onTouch();
        }
    }

    public void setTouchListener(TouchListener tl){
        touchListener = tl;
    }

    public void setText(String t){
        this.text = t;
    }

    public String getText(){
        return this.text;
    }

    public void drawElements(Canvas canvas, Paint paint, float density) {
        if(shadowPaint == null){
            shadowPaint = new Paint();
            shadowPaint.setShadowLayer(10.0f, 0.0f, 2.0f*density, 0xFF000000);
        }

        int xPos = this.xPos;
        int yPos = this.yPos;
        canvas.drawRect(xPos*density,yPos*density,(xPos+width)*density,(yPos+height)*density, shadowPaint);
        paint.setColor(Color.BLUE);
        canvas.drawRect(xPos*density,yPos*density,(xPos+width)*density,(yPos+height)*density, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(34*density);
        float textWidth = paint.measureText(text);
        canvas.drawText(text, (xPos+5)*density+(width*density-textWidth)/2, (yPos+height-14)*density, paint);
    }
}
