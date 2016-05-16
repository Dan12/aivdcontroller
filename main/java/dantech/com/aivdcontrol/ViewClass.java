package dantech.com.aivdcontrol;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Danweb on 5/14/16.
 */
public class ViewClass {

    private ArrayList<Drawable> drawables = new ArrayList<>();
    private ArrayList<Touchable> touchables = new ArrayList<>();
    private Menu menu;

    public ViewClass() {}

    public void drawElements(Canvas canvas, Paint paint, float density) {

        for(Drawable d : drawables)
            d.drawElements(canvas, paint, density);

        if(menu != null)
            menu.drawElements(canvas, paint, density);
    }

    public void addMenu(Menu m){
        menu = m;
    }

    public void addTouchable(Touchable t){
        touchables.add(t);
    }

    public void touchEvent(MotionEvent event){
        if(menu == null || !menu.isMenuOpen())
            for(Touchable t : touchables)
                t.onTouch(event);

        if(menu != null)
            menu.touchEvent(event);
    }

    public void addDrawable(Drawable d){
        drawables.add(d);
    }

    public void recievedBTMessage(String message){

    }
}
