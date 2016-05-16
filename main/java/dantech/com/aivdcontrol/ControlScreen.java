package dantech.com.aivdcontrol;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import java.io.IOException;

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

    private Menu startViewMenu;

    private MainActivity mainActivity;

    private RecordData dataRecorder;

    public ControlScreen(MainActivity activity){
        mainActivity = activity;
        dataRecorder = new RecordData();

        squareHalfSide = (ViewContainer.viewWidth-40)/2;
        centerCircleX = 20+squareHalfSide;
        centerCircleY = ViewContainer.viewHeight-20-squareHalfSide;
        swivelViewYCent = ViewContainer.viewHeight-40-squareHalfSide*2;

        gps = new GPSTracker(mainActivity);
        gps.getLocation();
        if(!gps.canGetLocation())
            gps.showSettingsAlert();

        comp = new CompassDirection();
        comp.getDirection(mainActivity);

        //OrientationSensor ord = new OrientationSensor(activity, null);
        //ord.Register(activity);
        //view.addDrawable(ord);

        startViewMenu = new Menu((int)ViewContainer.densViewWidth-50,(int)ViewContainer.densViewHeight-50);

        setButtons();

        this.addMenu(startViewMenu);
    }

    private void setButtons(){
        Button b1 = new Button(-1,30,"Update GPS");
        b1.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                gps.getLocation();
            }
        });
        startViewMenu.addButton(b1);

        Button b2 = new Button(-1,100,"Unstick");
        b2.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("0,0,0");
                } catch (IOException e) {
                    System.out.println("Error "+e);
                }
            }
        });
        startViewMenu.addButton(b2);

        final Button b3 = new Button(-1,170,"Play");
        b3.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                if(!isPlaying && !isRecording){
                    b3.setText("Stop");
                    isPlaying = true;
                    dataRecorder.startPlayback();
                } else if(isPlaying) {
                    b3.setText("Play");
                    isPlaying = false;
                }
            }
        });
        startViewMenu.addButton(b3);

        final Button b4 = new Button(-1,240,"Record");
        b4.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                if(!isRecording && !isPlaying){
                    b4.setText("Stop");
                    isRecording = true;
                    dataRecorder.startRecord();
                } else if(isRecording){
                    b4.setText("Record");
                    isRecording = false;
                    dataRecorder.finishRecording();
                }
            }
        });
        startViewMenu.addButton(b4);
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
        paint.setTextSize(21*density);
        String[] lines = verbose.split("\n");
        for(int i = 0; i < lines.length; i++)
            canvas.drawText(lines[i], 30, swivelViewYCent-10-35*i, paint);

        canvas.drawText("Latitude: "+String.format("%.8f",gps.getLatitude()), 30, swivelViewYCent-10-35*(lines.length), paint);
        canvas.drawText("longitude: "+String.format("%.8f",gps.getLongitude()), 30, swivelViewYCent-10-35*(lines.length+1), paint);
        canvas.drawText("Heading: "+String.format("%.8f",comp.getBearing()), 30, swivelViewYCent-10-35*(lines.length+2), paint);

        paint.setColor(Color.RED);
        paint.setTextSize(25*density);
        if(isRecording)
            canvas.drawText("Recording", 30, swivelViewYCent-10-35*(lines.length+3), paint);
        else if(isPlaying)
            canvas.drawText("Playing Recording", 30, swivelViewYCent-10-35*(lines.length+3), paint);
        else
            canvas.drawText("Ready to play/record", 30, swivelViewYCent-10-35*(lines.length+3), paint);

        startViewMenu.drawElements(canvas, paint, density);
    }

    @Override
    public void setupView(int thisInd, int curInd){
        mainActivity.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void touchEvent(MotionEvent event) {

        if(startViewMenu != null && event.getAction() == MotionEvent.ACTION_DOWN)
            startViewMenu.touchEvent(event);

        if(startViewMenu == null || !startViewMenu.isMenuOpen()){
            if(event.getAction() == MotionEvent.ACTION_UP){
                knobXDisp = 0;
                knobYDisp = 0;
            }
            else {
                int touchX = (int) event.getX();
                int touchY = (int) event.getY();
                if (!isPlaying) {
                    if (touchY > ViewContainer.viewHeight - ViewContainer.viewWidth) {
                        if (touchX < squareHalfSide * 0.15)
                            touchX = (int) (squareHalfSide * 0.15);
                        if (touchX > ViewContainer.viewWidth - squareHalfSide * 0.15)
                            touchX = (int) (ViewContainer.viewWidth - squareHalfSide * 0.15);
                        if (touchY < (ViewContainer.viewHeight - ViewContainer.viewWidth) + squareHalfSide * 0.15)
                            touchY = (int) ((ViewContainer.viewHeight - ViewContainer.viewWidth) + squareHalfSide * 0.15);
                        if (touchY > ViewContainer.viewHeight - squareHalfSide * 0.15)
                            touchY = (int) (ViewContainer.viewHeight - squareHalfSide * 0.15);
                        knobXDisp = touchX - centerCircleX;
                        knobYDisp = touchY - centerCircleY;
                    } else {
                        knobXDisp = 0;
                        knobYDisp = 0;
                    }
                }
            }

        }
    }

    @Override
    public void recievedBTMessage(String message){
        verbose = message;
        if(isPlaying){
            int[] point = dataRecorder.getPoint();
            if(point[0] != Integer.MIN_VALUE) {
                knobXDisp = point[0];
                knobYDisp = point[1];
            }
            else{
                isPlaying = false;
                knobXDisp = 0;
                knobYDisp = 0;
                mainActivity.toastMessage("Finished playback");
            }
        }

        try {
            MainActivity.btHandler.sendData("0,"+(int)Functions.map(knobXDisp,-squareHalfSide,squareHalfSide,-200,200)+","+(int)Functions.map(knobYDisp,-squareHalfSide,squareHalfSide,-200,200));
        } catch (IOException e) {e.printStackTrace();}

        if(isRecording)
            dataRecorder.recordPoint(knobXDisp, knobYDisp);
    }
}
