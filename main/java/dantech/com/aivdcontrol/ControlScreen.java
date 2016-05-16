package dantech.com.aivdcontrol;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

/**
 * Created by Danweb on 5/15/16.
 */
public class ControlScreen extends ViewClass{

    private String verbose = "Output\nNewLine";
    private int swivelViewYCent = 0;

    private Path knobPath = new Path();

    private int squareHalfSide;
    private int centerCircleX;
    private int centerCircleY;

    private boolean isRecording = false;
    private boolean isPlaying = false;

    private int knobXDisp = 0;
    private int knobYDisp = 0;

    private GPSTracker gps;
    private CompassDirection comp;

    public ControlScreen(Activity mainActivity){
        squareHalfSide = (ViewContainer.viewWidth-40)/2;
        centerCircleX = 20+squareHalfSide;
        centerCircleY = ViewContainer.viewWidth-20-squareHalfSide;
        swivelViewYCent = ViewContainer.viewWidth-40-squareHalfSide*2;

        gps = new GPSTracker(mainActivity);
        gps.getLocation();
        if(!gps.canGetLocation())
            gps.showSettingsAlert();

        comp = new CompassDirection();
        comp.getDirection(mainActivity);

        //OrientationSensor ord = new OrientationSensor(activity, null);
        //ord.Register(activity);
        //view.addDrawable(ord);

        Menu startViewMenu = new Menu((int)ViewContainer.densViewWidth-50,(int)ViewContainer.densViewHeight-50);

        Button b1 = new Button(-1,30,"Update GPS");
        b1.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                gps.getLocation();
            }
        });
        startViewMenu.addButton(b1);

        this.addMenu(startViewMenu);
    }

    @Override
    public void drawElements(Canvas canvas, Paint paint, float density) {
        //big square
        paint.setColor(Color.BLACK);
        canvas.drawRect(centerCircleX - squareHalfSide, centerCircleY - squareHalfSide, centerCircleX + squareHalfSide, centerCircleY + squareHalfSide, paint);
        paint.setColor(Color.WHITE);
        canvas.drawRect(centerCircleX + 4 - squareHalfSide, centerCircleY + 4 - squareHalfSide, centerCircleX + squareHalfSide - 4, centerCircleY + squareHalfSide - 4, paint);
        //small circle
        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerCircleX, centerCircleY, (float) (squareHalfSide *0.2), paint);
        //knob circle
        paint.setColor(Color.BLACK);
        canvas.drawCircle(centerCircleX +knobXDisp, centerCircleY +knobYDisp, (float) (squareHalfSide *0.15), paint);
        //knob connector line
        double knobAngle = Math.atan2(knobXDisp,-knobYDisp) * (180/Math.PI) + 180;
        knobAngle = Math.toRadians(knobAngle);
        knobPath.reset();
        knobPath.moveTo((float) (centerCircleX +(squareHalfSide *0.2*Math.cos(knobAngle))), (float) (centerCircleY +(squareHalfSide *0.2*Math.sin(knobAngle))));
        knobPath.lineTo((float) (centerCircleX +knobXDisp), centerCircleY +knobYDisp);
        //knobPath.lineTo((float) (centerCircleX+knobXDisp+squareHalfSide*0.15), (centerCircleY)+knobYDisp);
        knobPath.lineTo((float) (centerCircleX +(squareHalfSide *0.2*Math.cos(knobAngle+Math.PI))), (float) (centerCircleY +(squareHalfSide *0.2*Math.sin(knobAngle+Math.PI))));
        canvas.drawPath(knobPath, paint);
        //knob
        paint.setColor(Color.BLUE);
        canvas.drawCircle(centerCircleX +knobXDisp, centerCircleY +knobYDisp, (float) (squareHalfSide *0.2)-4, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        paint.setColor(Color.WHITE);
        canvas.drawRect(10, 10, ViewContainer.viewWidth-10, swivelViewYCent+10, paint);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(4);
        canvas.drawRect(20, 20, ViewContainer.viewWidth-20, swivelViewYCent, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.BLACK);
        paint.setTextSize(32);
        String[] lines = verbose.split("\n");
        for(int i = 0; i < lines.length; i++)
            canvas.drawText(lines[i], 30, swivelViewYCent-10-35*i, paint);

        paint.setColor(Color.RED);
        paint.setTextSize(38);
        if(isRecording)
            canvas.drawText("Recording", 30, swivelViewYCent-10-35*lines.length, paint);
        else if(isPlaying)
            canvas.drawText("Playing Recording", 30, swivelViewYCent-10-35*lines.length, paint);
        else
            canvas.drawText("Ready to play/record", 30, swivelViewYCent-10-35*lines.length, paint);
    }

    @Override
    public void touchEvent(MotionEvent event) {
        int touchX = (int)event.getX();
        int touchY = (int)event.getY();
        if(!isPlaying){
            if(touchY > ViewContainer.viewHeight-ViewContainer.viewWidth){
                if(touchX < squareHalfSide*0.15)
                    touchX = (int) (squareHalfSide*0.15);
                if(touchX > ViewContainer.viewWidth-squareHalfSide*0.15)
                    touchX = (int) (ViewContainer.viewWidth-squareHalfSide*0.15);
                if(touchY < (ViewContainer.viewHeight-ViewContainer.viewWidth)+squareHalfSide*0.15)
                    touchY = (int) ((ViewContainer.viewHeight-ViewContainer.viewWidth)+squareHalfSide*0.15);
                if(touchY > ViewContainer.viewHeight-squareHalfSide*0.15)
                    touchY = (int) (ViewContainer.viewHeight-squareHalfSide*0.15);
                knobXDisp = touchX - centerCircleX;
                knobYDisp = touchY - centerCircleY;
            }
            else{
                knobXDisp = 0;
                knobYDisp = 0;
            }
        }
    }
}
