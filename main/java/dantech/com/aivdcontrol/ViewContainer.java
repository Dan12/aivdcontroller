package dantech.com.aivdcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Danweb on 5/15/16.
 */
public class ViewContainer extends View{

    private ArrayList<ViewClass> views = new ArrayList<>();
    private Menu viewNavigation;
    private Paint paint;
    public static float density;
    public static int viewWidth;
    public static int viewHeight;
    public static float densViewWidth;
    public static float densViewHeight;
    private int viewOn = 0;

    public ViewContainer(Context context) {
        super(context);
        paint = new Paint();
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void initButtons(){
        Button b1 = new Button(-1,30,"Control");
        b1.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                if(views.size() > 0) {
                    viewOn = 0;
                    views.get(viewOn).setupView();
                }
            }
        });
        viewNavigation.addButton(b1);

        Button b2 = new Button(-1,100,"Run Script");
        b2.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                if(views.size() > 1) {
                    viewOn = 1;
                    views.get(viewOn).setupView();
                }
            }
        });
        viewNavigation.addButton(b2);

        Button b3 = new Button(-1,170,"Run Foxtrot");
        b3.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                if(views.size() > 2) {
                    viewOn = 2;
                    views.get(viewOn).setupView();
                }
            }
        });
        viewNavigation.addButton(b3);

        Button b4 = new Button(-1,240,"Color Follow");
        b4.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                if(views.size() > 3) {
                    viewOn = 3;
                    views.get(viewOn).setupView();
                }
            }
        });
        viewNavigation.addButton(b4);

        Button b5 = new Button(-1,310,"Connect");
        b5.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                MainActivity.btHandler.findBT();
            }
        });
        viewNavigation.addButton(b5);
    }

    public void addView(ViewClass v){
        views.add(v);
    }


    @Override
    public void onDraw(Canvas canvas) {

        if(views.isEmpty()) {
            paint.setTextSize(34*density);
            canvas.drawText("No Views", (50) * density, (200) * density, paint);
        }
        else
            views.get(viewOn).drawElements(canvas, paint, density);

        viewNavigation.drawElements(canvas, paint, density);

        //call repaint
        invalidate();
    }

    public void initDimensions(float dens, int width, int height){
        density = dens;
        viewWidth = width;
        viewHeight = height;
        densViewWidth = width/density;
        densViewHeight = height/density;

        viewNavigation = new Menu(10,(int)densViewHeight-50);

        initButtons();
    }

    public void touchEvent(MotionEvent event){
        viewNavigation.touchEvent(event);

        if(!viewNavigation.isMenuOpen())
            if(!views.isEmpty())
                views.get(viewOn).touchEvent(event);
    }

    public void recievedBTMessage(String message){
        views.get(viewOn).recievedBTMessage(message);
    }

}
