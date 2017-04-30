package app.flashlight.craftystudio.flashlightapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Aisha on 4/28/2017.
 */

public class MyService extends Service implements SensorEventListener {


    private float mAccelLast, mAccelCurrent;
  //  private static Camera camera;
  //  Camera.Parameters params;

    private Sensor mGyroscope;
    private SensorManager mSensorManager;

    boolean flashOn;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        getCamera();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Get x,y and z values
        float x, y, z, mAccel = (float) 0.0;
        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
        z = sensorEvent.values[2];


        /*
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        */

        Log.d("in service call","sensor changed");
        if (z > 8) {
            if (!flashOn){
                turnOnFlash();

            }else{
                turnOffFlash();
            }
        }


    }

    private void getCamera() {
        if (MainActivity.camera == null) {
            try {
                MainActivity.camera = Camera.open();
                MainActivity.params = MainActivity.camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("CameraFailed to Open", e.getMessage());
            }

            Log.d("in service call","camera call");

        }
    }

    private void turnOnFlash() {
        MainActivity.params =  MainActivity.camera.getParameters();
        MainActivity.params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        MainActivity.camera.setParameters(MainActivity.params);
        MainActivity.camera.startPreview();
        Log.d("in service call","torch on");
        flashOn=true;

    }

    private void turnOffFlash() {
          /*  if (camera == null || params == null) {
                return;
            }*/

        MainActivity.params =  MainActivity.camera.getParameters();
        MainActivity.params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        MainActivity.camera.setParameters(MainActivity.params);
        MainActivity.camera.stopPreview();
        flashOn=false;

    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
