package app.flashlight.craftystudio.flashlightapp;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aisha on 4/28/2017.
 */

public class MyService extends Service implements SensorEventListener {


    private float mAccelLast, mAccelCurrent;
    Camera camera;
    Camera.Parameters params;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
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


        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        Log.d("in service call","sensor changed");
        if (mAccel > 12) {
             turnOnFlash();
        }


    }
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("CameraFailed to Open", e.getMessage());
            }

            Log.d("in service call","camera call");

        }
    }

    private void turnOnFlash() {
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        Log.d("in service call","torch on");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
