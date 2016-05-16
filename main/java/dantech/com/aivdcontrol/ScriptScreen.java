package dantech.com.aivdcontrol;

import android.content.pm.ActivityInfo;

import java.io.IOException;

/**
 * Created by Danweb on 5/16/16.
 */
public class ScriptScreen extends  ViewClass {

    private MainActivity mainActivity;

    public ScriptScreen(MainActivity activity){

        mainActivity = activity;

        final GPSTracker gps = new GPSTracker(mainActivity);
        gps.getLocation();
        if(!gps.canGetLocation())
            gps.showSettingsAlert();

        addDrawable(gps);

        final CompassDirection comp = new CompassDirection();
        comp.getDirection(mainActivity);

        menu = new Menu((int)ViewContainer.densViewWidth-50,(int)ViewContainer.densViewHeight-50);
        Button m1 = new Button(-1,30,"Update GPS");
        m1.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                gps.getLocation();
            }
        });
        menu.addButton(m1);

        Button b1 = new Button(-1,30,"Run Alpha");
        b1.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("1");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addButton(b1);

        Button b2 = new Button(-1,100,"Run Beta");
        b2.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("2");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addButton(b2);

        Button b3 = new Button(-1,170,"Run Charlie");
        b3.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("3");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addButton(b3);

        Button b4 = new Button(-1,240,"Run Delta");
        b4.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("4");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addButton(b4);

        Button b5 = new Button(-1,310,"Run Echo");
        b5.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("5");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addButton(b5);

        Button b6 = new Button(-1,380,"Run Hotel");
        b6.setTouchListener(new TouchListener() {
            @Override
            void onTouch() {
                try {
                    MainActivity.btHandler.sendData("6,"+String.format("%3.8f,%3.8f,%3.8f",comp.getBearing(),gps.getLatitude(),gps.getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addButton(b6);
    }

    @Override
    public void setupView(){
        mainActivity.setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void recievedBTMessage(String message){
        mainActivity.toastMessage(message);
    }
}
