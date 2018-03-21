package com.marven.fyp.memorytraining;

import android.app.ProgressDialog;
import android.hardware.Camera;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SelectDifficulty extends BaseActivity {
    private Button buttonEasy;
    private Button buttonMedium;
    private Button buttonHard;
    Intent i;
    private static final String TAG = "OCVSample::Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");

        i = new Intent (this, CameraCheckChipsRemoved.class);

        buttonEasy = (Button) findViewById(R.id.buttonEasy);					//initial the button for scanning the BLE device
        buttonEasy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e(TAG, "Easy Mode Selected");
                DataHolder.setMode(DataHolder.Mode.EASY);
                startActivity(i);
            }
        });

        buttonMedium = (Button) findViewById(R.id.buttonMedium);
        buttonMedium.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e(TAG, "Medium Mode Selected");
                DataHolder.setMode(DataHolder.Mode.MEDIUM);
                startActivity(i);
            }
        });


        buttonHard = (Button) findViewById(R.id.buttonHard);
        buttonHard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.e(TAG, "Hard Mode Selected");
                DataHolder.setMode(DataHolder.Mode.HARD);
                startActivity(i);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // level 1 - board size 3, level 3 - board size 4, level 9 - board size 5, level 17 - board size 6, level 27 - board size 7, level 39 - board size 8
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.difficulty_settings: {
                i = new Intent(this, DifficultySettings.class);
                startActivity(i);
                break;
            }

            case R.id.size3x3: {
                Log.e(TAG, "3x3");
                //set level to level one
                DataHolder.setLevel(1);
                break;
            }

            case R.id.size4x4: {
                Log.e(TAG, "4x4");
                //set level to level two
                DataHolder.setLevel(3);
                break;
            }

            case R.id.size5x5: {
                Log.e(TAG, "5x5");
                //set level to level three
                DataHolder.setLevel(9);
                break;
            }

            case R.id.size6x6: {
                Log.e(TAG, "6x6");
                //set level to level three
                DataHolder.setLevel(17);
                break;
            }

            case R.id.size7x7: {
                Log.e(TAG, "7x7");
                //set level to level three
                DataHolder.setLevel(27);
                break;
            }

            case R.id.size8x8: {
                Log.e(TAG, "8x8");
                //set level to level three
                DataHolder.setLevel(39);
                break;
            }

            case R.id.offSound: {
                DataHolder.setNaturalReaderSoundStatus(false);
                break;
            }

            case R.id.onSound: {
                DataHolder.setNaturalReaderSoundStatus(true);
                break;
            }

            case R.id.offMusic: {
                DataHolder.setBackgroundMusicStatus(false);
                mServ.pauseMusic();
                doUnbindService();
                stopService(music);
                break;
            }




        }
        return false;
    }



}