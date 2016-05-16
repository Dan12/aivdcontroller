package dantech.com.aivdcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.List;

/**
 * Created by Danweb on 5/15/16.
 */
public class CompassDirection implements Drawable{

    private SensorManager mSensorManager;
    private float compassBearing;
    private float templeBearing = 0;

    public CompassDirection(){}

    public void getDirection(Context context) {
        mSensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> mySensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if(mySensors.size() > 0){
            mSensorManager.registerListener(mySensorEventListener, mySensors.get(0), SensorManager.SENSOR_DELAY_UI);
        }
        else{
            System.out.println("String not found");

        }
    }

    private SensorEventListener mySensorEventListener = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
            System.out.println("Accuracy changed:"+accuracy);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            compassBearing = (float)event.values[0];
            float bearing;
            bearing = compassBearing - templeBearing;
            if (bearing < 0)
                bearing = 360 + bearing;
            //System.out.println("New bearing: "+bearing);
        }
    };

    public float getBearing(){
        return this.compassBearing;
    }

    public void drawElements(Canvas canvas, Paint paint, float density) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(24*density);
        canvas.drawText("Bearing: " + compassBearing, 5, 200, paint);
    }
}
