package dantech.com.aivdcontrol;

import android.app.Activity;

/**
 * Created by Danweb on 5/15/16.
 */
public class InitSensors {

    public static Activity mainActivity;

    public static void setMainActivity(Activity a){
        mainActivity = a;
    }

    public static void initSensors(ViewClass view){
        final GPSTracker gps = new GPSTracker(mainActivity);
        gps.getLocation();
        if(!gps.canGetLocation())
            gps.showSettingsAlert();

        view.addDrawable(gps);

        CompassDirection comp = new CompassDirection();
        comp.getDirection(mainActivity);
        view.addDrawable(comp);

        //OrientationSensor ord = new OrientationSensor(activity, null);
        //ord.Register(activity);
        //view.addDrawable(ord);
    }
}
