package dantech.com.aivdcontrol;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Danweb on 5/16/16.
 */
public class FoxScreen extends ViewClass{

    private MainActivity mainActivity;

    public FoxScreen(MainActivity activity){
        mainActivity = activity;
    }

    @Override
    public void setupView(){
        //mainActivity.setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mainActivity.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void drawElements(Canvas canvas, Paint paint, float density) {
        paint.setColor(Color.BLACK);
        canvas.drawRect(50,100,150,200,paint);
        canvas.drawRect(50,300,150,400,paint);
    }
}
