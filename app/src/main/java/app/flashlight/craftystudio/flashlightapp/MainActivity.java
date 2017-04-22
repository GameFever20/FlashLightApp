package app.flashlight.craftystudio.flashlightapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    boolean hasFlashLight;
    public static Camera cam = Camera.open();// has to be static, otherwise onDestroy() destroys it
    public static Parameters p;
    boolean flashlightOn;
    Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySwitch = (Switch) findViewById(R.id.switch1);
        hasFlashLight = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

          //cam=Camera.open();
       //
       //  cam.setParameters(p);

        flashlightOn = cam.getParameters().getFlashMode().equals("torch");
        if (!flashlightOn) {
            mySwitch.setChecked(false);
        } else {
            mySwitch.setChecked(true);
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




            }
        });

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

    public void flashLightOn() {

        try {
            if (hasFlashLight) {
                cam = Camera.open();
                p = cam.getParameters();
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff() {
        try {
            if (hasFlashLight) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onStop() {
        //cam.release();
        //cam = null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
