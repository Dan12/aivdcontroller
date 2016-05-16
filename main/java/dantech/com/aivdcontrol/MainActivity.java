package dantech.com.aivdcontrol;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Danweb on 5/14/16.
 */
public class MainActivity extends Activity {

    private ViewContainer view;
    public static BluetoothHandler btHandler;

    // basically constructor, called when app starts
    @Override
    protected void onCreate(Bundle savedInstanceStace){
        super.onCreate(savedInstanceStace);

        view = new ViewContainer(this);

        view.setBackgroundColor(Color.WHITE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        view.initDimensions(dm.density, dm.widthPixels, dm.heightPixels);

        view.addView(new ControlScreen(this));
        view.addView(new ScriptScreen(this));

        setContentView(view);

        btHandler = new BluetoothHandler(this);

    }

    public ViewContainer getViewContainer(){
        return view;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tX = event.getX();
        float tY = event.getY();

        view.touchEvent(event);


        return super.onTouchEvent(event);
    }

    public void toastMessage(final String m){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), m, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void onDestroy(){
        System.out.println("Destroyed");
        super.onDestroy();
        System.out.println(isFinishing());
        try {
            btHandler.closeBT();
        } catch (IOException e) {e.printStackTrace();}
        finish();
    }

}
