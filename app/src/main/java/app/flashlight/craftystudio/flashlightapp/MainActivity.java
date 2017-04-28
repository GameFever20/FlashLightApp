package app.flashlight.craftystudio.flashlightapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    boolean hasFlashLight;
    public static Camera cam = Camera.open();// has to be static, otherwise onDestroy() destroys it
    public static Parameters p;
    boolean flashlightOn;
    Switch mySwitch;

    ImageButton btnSwitch;

    private static Camera camera;
    private boolean isFlashOn;
    //  private boolean hasFlash;
    Parameters params;


    //sensors declaration
    int count = 1;
    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
   // private float x1, x2, x3;
   // private static final float ERROR = (float) 7.0;
    private TextView counter;

    private float mAccelLast, mAccelCurrent;
    Button startService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySwitch = (Switch) findViewById(R.id.switch1);
        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.imagebtntorch);
        startService=(Button) findViewById(R.id.startService);

        hasFlashLight = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        // cam=Camera.open();
        //  cam.setParameters(p);


        if (camera == null) {
            getCamera();
        }

        isFlashOn = camera.getParameters().getFlashMode().equals("torch");
        toggleButtonImage();


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(new Intent(getApplicationContext(),MyService.class));


            }
        });

        /*

        if (!flashlightOn) {
            mySwitch.setChecked(false);
        } else {
            mySwitch.setChecked(true);
        }



        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mySwitch.isChecked()) {
                    if (hasFlashLight) {
                        flashLightOn();
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry.. You don't have flashlight", Toast.LENGTH_SHORT).show();
                    }

                    Toast.makeText(MainActivity.this, "Switch is currently ON", Toast.LENGTH_SHORT).show();
                } else {
                    if (hasFlashLight) {
                        flashLightOff();
                    } else {
                        Toast.makeText(MainActivity.this, "Sorry.. You don't have flashlight", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(MainActivity.this, "Switch is currently OFF", Toast.LENGTH_SHORT).show();

                }
            }
        });
        */


		/*
         * Switch button click event to toggle flash on/off
		 */
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    Log.d("turn off flash", "on click" + isFlashOn);
                    turnOffFlash();
                } else {
                    // turn on flash
                    Log.d("turn on flash", "on click" + isFlashOn);
                    turnOnFlash();
                }
            }
        });


        //sensor coding
        counter = (TextView) findViewById(R.id.textView_counter);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> listOfSensorsOnDevice = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        /*
        for (int i = 0; i < listOfSensorsOnDevice.size(); i++) {
            if (listOfSensorsOnDevice.get(i).getType() == Sensor.TYPE_ACCELEROMETER) {

                Toast.makeText(this, "ACCELEROMETER sensor is available on device", Toast.LENGTH_SHORT).show();


                init = false;

                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            } else {

                Toast.makeText(this, "ACCELEROMETER sensor is NOT available on device", Toast.LENGTH_SHORT).show();
            }
        }
        */


    }

    /*
     * Get the camera
	 */
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("CameraFailed to Open", e.getMessage());
            }
        }
    }

    /*
     * Turning On flash
     */
    private void turnOnFlash() {
        if (!isFlashOn) {
           /*
            if (camera == null || params == null) {
                return;
            }*/

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
            Log.d("turn on flash", "in method" + isFlashOn);

            // changing button/switch image
            toggleButtonImage();
        }

    }

    /*
     * Turning Off flash
     */
    private void turnOffFlash() {
        if (isFlashOn) {

          /*  if (camera == null || params == null) {
                return;
            }*/

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
            Log.d("turn off flash", "in method" + isFlashOn);

            // changing button/switch image
            toggleButtonImage();
        }
    }

    /*
     * Toggle switch button images
	 * changing image states to on / off
	 * */
    private void toggleButtonImage() {
        if (isFlashOn) {
            btnSwitch.setImageResource(R.drawable.btn_switch_on);
        } else {
            btnSwitch.setImageResource(R.drawable.btn_switch_off);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
       /* if (camera != null) {
            camera.release();
            camera = null;
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        getCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
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

        if (mAccel > 12) {
            Toast.makeText(getApplicationContext(),
                    "You have shaken your phone", Toast.LENGTH_SHORT).show();
            counter.setText("Shake Count : " + count);
            count = count + 1;
            turnOnFlash();
        }


        /*
        if (!init) {
            x1 = x;
            x2 = y;
            x3 = z;
            init = true;
        } else {

            float diffX = Math.abs(x1 - x);
            float diffY = Math.abs(x2 - y);
            float diffZ = Math.abs(x3 - z);

            //Handling ACCELEROMETER Noise
            if (diffX < ERROR) {

                diffX = (float) 0.0;
            }
            if (diffY < ERROR) {
                diffY = (float) 0.0;
            }
            if (diffZ < ERROR) {

                diffZ = (float) 0.0;
            }


            x1 = x;
            x2 = y;
            x3 = z;


            //Horizontal Shake Detected!
            if (diffX > diffY) {

                counter.setText("Shake Count : " + count);
                count = count + 1;
                Toast.makeText(MainActivity.this, "Shake Detected!", Toast.LENGTH_SHORT).show();
            }




        }

        */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void startService(View view) {

        startService(new Intent(getApplicationContext(),MyService.class));
    }
}
