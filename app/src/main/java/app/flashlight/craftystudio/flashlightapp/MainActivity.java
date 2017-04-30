package app.flashlight.craftystudio.flashlightapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
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


public class MainActivity extends AppCompatActivity {

    boolean hasFlashLight;
    public static Camera cam;// has to be static, otherwise onDestroy() destroys it
    public static Parameters p;
    boolean flashlightOn;
    Switch mySwitch;

    ImageView btnSwitch;

    public static Camera camera;
    private boolean isFlashOn;
    public static Parameters params;


    //sensors declaration
    int count = 1;
    private boolean init;
    private Sensor mAccelerometer;
    private SensorManager mSensorManager;
    private TextView counter;

    private float mAccelLast, mAccelCurrent;
    Button startService;


    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySwitch = (Switch) findViewById(R.id.switch1);
        // flash switch button
        btnSwitch = (ImageView) findViewById(R.id.imagebtntorch);
        startService = (Button) findViewById(R.id.startService);
        counter = (TextView) findViewById(R.id.textView_counter);

        hasFlashLight = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        //calling for permission
        checkForGivenPermission();

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stopService(new Intent(getApplicationContext(), MyService.class));


            }
        });





		/*
         * Switch button click event to toggle flash on/off
		 */
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (permissionGranted == true) {
                    if (isFlashOn) {
                        // turn off flash
                        Log.d("turn off flash", "on click" + isFlashOn);
                        turnOffFlash();
                    } else {
                        // turn on flash
                        Log.d("turn on flash", "on click" + isFlashOn);
                        turnOnFlash();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    openDialog();
                }


            }
        });


    }

    private void openDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Request Permission")
                .setMessage("do you want to request for permission? ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkForGivenPermission();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    permissionGranted = true;
                    initialCall();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    permissionGranted = false;
                    btnSwitch.setImageResource(R.drawable.btn_switch_off);
                    Toast.makeText(MainActivity.this, "Permission denied to use camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public void checkForGivenPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
                initialCall();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);

            }
        } else {
            permissionGranted = true;
            initialCall();
        }

    }

    public void initialCall() {

        if (camera == null) {
            getCamera();

            Toast.makeText(this, "camera is null ", Toast.LENGTH_SHORT).show();
        }
        isFlashOn = camera.getParameters().getFlashMode().equals("torch");
        toggleButtonImage();

        //starting service
        startService(new Intent(getApplicationContext(), MyService.class));


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

            Toast.makeText(this, "Torch on", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Torch off", Toast.LENGTH_SHORT).show();

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


    public void startService(View view) {

        startService(new Intent(getApplicationContext(), MyService.class));
    }
}
