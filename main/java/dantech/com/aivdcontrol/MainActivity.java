package dantech.com.aivdcontrol;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

/**
 * Created by Danweb on 5/14/16.
 */
public class MainActivity extends Activity {

    private ViewContainer view;

    // basically constructor, called when app starts
    @Override
    protected void onCreate(Bundle savedInstanceStace){
        super.onCreate(savedInstanceStace);

        view = new ViewContainer(this);

        view.setBackgroundColor(Color.WHITE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        view.initDimensions(dm.density, dm.widthPixels, dm.heightPixels);

        InitSensors.setMainActivity(this);

        SetViews.setViews(view);

        setContentView(view);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tX = event.getX();
        float tY = event.getY();

        view.touchEvent(event);


        return super.onTouchEvent(event);
    }


}
