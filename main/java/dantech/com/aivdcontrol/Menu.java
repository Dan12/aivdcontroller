package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Danweb on 5/15/16.
 */
public class Menu implements Drawable {

    private ArrayList<Button> buttons = new ArrayList<>();
    private ArrayList<NumberSlider> sliders = new ArrayList<>();
    private boolean isOpen = false;
    private int openButtonX, openButtonY;
    private int openButtonWidth = 40;
    private int openButtonHeight = 40;
    float barHeight;


    public Menu(int x, int y){
        Button b = new Button(-1,(int)ViewContainer.densViewHeight-70,"Back");

        b.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                isOpen = false;
            }
        });

        buttons.add(b);

        openButtonX = x;
        openButtonY = y;
        barHeight = (openButtonHeight-20)/3;
    }

    public void addButton(Button b){
        buttons.add(b);
    }

    public void addSlider(NumberSlider n){
        sliders.add(n);
    }

    public void touchEvent(MotionEvent event){
        if(isOpen) {
            for (Button b : buttons)
                b.touchEvent(event);
            for(NumberSlider n : sliders)
                n.onTouch(event);
        }

        float tx = event.getX()/ViewContainer.density;
        float ty = event.getY()/ViewContainer.density;

        if(tx > openButtonX && tx < openButtonX+openButtonWidth && ty > openButtonY && ty < openButtonY+openButtonHeight)
            isOpen = true;
    }

    public boolean isMenuOpen(){
        return isOpen;
    }

    @Override
    public void drawElements(Canvas canvas, Paint paint, float density) {
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(openButtonX*density, openButtonY*density, (openButtonX+openButtonWidth)*density, (openButtonY+openButtonHeight)*density, paint);
        paint.setColor(Color.DKGRAY);
        for(int i = 0; i < 3; i++)
            canvas.drawRect((openButtonX+5)*density, (openButtonY+5+(barHeight+5)*i)*density, (openButtonX+openButtonWidth-5)*density, (openButtonY+5+(barHeight+5)*i+barHeight)*density, paint);

        if(isOpen){
            paint.setARGB(200, 100, 100, 100);
            canvas.drawRect(0,0,ViewContainer.viewWidth, ViewContainer.viewHeight, paint);
            for(Button b : buttons)
                b.drawElements(canvas, paint, density);
            for(NumberSlider n : sliders)
                n.drawElements(canvas, paint, density);
        }
    }
}
