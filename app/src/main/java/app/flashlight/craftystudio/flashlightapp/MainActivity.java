package app.flashlight.craftystudio.flashlightapp;

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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySwitch = (Switch) findViewById(R.id.switch1);
        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.imagebtntorch);

        hasFlashLight = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        // cam=Camera.open();
        //  cam.setParameters(p);


        if (camera==null){
            getCamera();
        }

        isFlashOn = camera.getParameters().getFlashMode().equals("torch");
        toggleButtonImage();


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




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
                    Log.d("turn off flash","on click"+isFlashOn);
                    turnOffFlash();
                } else {
                    // turn on flash
                    Log.d("turn on flash","on click"+isFlashOn);
                    turnOnFlash();
                }
            }
        });


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
            Log.d("turn on flash","in method"+isFlashOn);

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
            Log.d("turn off flash","in method"+isFlashOn);

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
}
