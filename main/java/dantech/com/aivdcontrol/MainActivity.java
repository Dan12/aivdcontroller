package dantech.com.aivdcontrol;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        view = new ViewContainer(this);

        view.setBackgroundColor(Color.WHITE);

        setDisplayMetrics();

        view.addView(new ControlScreen(this));
        view.addView(new ScriptScreen(this));
        FoxScreen foxScreen = new FoxScreen(this, this);
        view.addView(foxScreen.getViewClass());
        // second instance will be follwo screen
        view.addView(foxScreen.getViewClass());

        RelativeLayout mRelativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        // add foxScreen to layout to call surface create
        mRelativeLayout.addView(foxScreen);

        mRelativeLayout.addView(view);
        setContentView(mRelativeLayout);

        //setContentView(view);

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

    public void setOrientation(int orientation){
        // view selection menu closes when this is called because setDisplayMetrics reinitialized the menu
        setRequestedOrientation(orientation);
        setDisplayMetrics();
    }

    public void setDisplayMetrics(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        view.initDimensions(dm.density, dm.widthPixels, dm.heightPixels);
    }

}
