package com.marven.fyp.memorytraining;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DifficultySettings extends BaseActivity implements AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {
    private Button buttonApply, buttonExit;
    private Spinner spinner;
    private static final String[] paths = {"EASY", "MEDIUM", "HARD"};
    Intent i;

    private TextView seekBarTextViewLevel1NumLED, seekBarTextViewLevel2NumLED, seekBarTextViewLevel3NumLED;
    private TextView seekBarTextViewLevel1Time, seekBarTextViewLevel2Time, seekBarTextViewLevel3Time;
    private TextView seekBarTextViewLevel1NumColour, seekBarTextViewLevel2NumColour, seekBarTextViewLevel3NumColour;
    private SeekBar seekBarLevel1NumLED, seekBarLevel2NumLED, seekBarLevel3NumLED;
    private SeekBar seekBarLevel1Time, seekBarLevel2Time, seekBarLevel3Time;
    private SeekBar seekBarLevel1NumColour, seekBarLevel2NumColour, seekBarLevel3NumColour;
    int level1NumLEDprogress, level2NumLEDprogress, level3NumLEDprogress, level1Timeprogress, level2Timeprogress, level3Timeprogress, level1NumColourprogress, level2NumColourprogress, level3NumColourprogress;
    int maxNumLED = 64, minNumLED = 1, maxTime = 20, minTime = 3, maxNumColour = 4, minNumColour = 1;


    private static final String TAG = "OCVSample::Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty_settings);

        //for spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DifficultySettings.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        seekBarLevel1NumLED = (SeekBar) findViewById(R.id.seekBarLevel1NumLED);
        seekBarLevel2NumLED = (SeekBar) findViewById(R.id.seekBarLevel2NumLED);
        seekBarLevel3NumLED = (SeekBar) findViewById(R.id.seekBarLevel3NumLED);
        seekBarLevel1Time = (SeekBar) findViewById(R.id.seekBarLevel1Time);
        seekBarLevel2Time = (SeekBar) findViewById(R.id.seekBarLevel2Time);
        seekBarLevel3Time = (SeekBar) findViewById(R.id.seekBarLevel3Time);
        seekBarLevel1NumColour = (SeekBar) findViewById(R.id.seekBarLevel1NumColour);
        seekBarLevel2NumColour = (SeekBar) findViewById(R.id.seekBarLevel2NumColour);
        seekBarLevel3NumColour = (SeekBar) findViewById(R.id.seekBarLevel3NumColour);

        seekBarLevel1NumLED.setOnSeekBarChangeListener(this);
        seekBarLevel2NumLED.setOnSeekBarChangeListener(this);
        seekBarLevel3NumLED.setOnSeekBarChangeListener(this);
        seekBarLevel1Time.setOnSeekBarChangeListener(this);
        seekBarLevel2Time.setOnSeekBarChangeListener(this);
        seekBarLevel3Time.setOnSeekBarChangeListener(this);
        seekBarLevel1NumColour.setOnSeekBarChangeListener(this);
        seekBarLevel2NumColour.setOnSeekBarChangeListener(this);
        seekBarLevel3NumColour.setOnSeekBarChangeListener(this);

        seekBarTextViewLevel1NumLED = (TextView) findViewById(R.id.seekBarTextViewLevel1NumLED);
        seekBarTextViewLevel2NumLED = (TextView) findViewById(R.id.seekBarTextViewLevel2NumLED);
        seekBarTextViewLevel3NumLED = (TextView) findViewById(R.id.seekBarTextViewLevel3NumLED);
        seekBarTextViewLevel1Time = (TextView) findViewById(R.id.seekBarTextViewLevel1Time);
        seekBarTextViewLevel2Time = (TextView) findViewById(R.id.seekBarTextViewLevel2Time);
        seekBarTextViewLevel3Time = (TextView) findViewById(R.id.seekBarTextViewLevel3Time);
        seekBarTextViewLevel1NumColour = (TextView) findViewById(R.id.seekBarTextViewLevel1NumColour);
        seekBarTextViewLevel2NumColour = (TextView) findViewById(R.id.seekBarTextViewLevel2NumColour);
        seekBarTextViewLevel3NumColour = (TextView) findViewById(R.id.seekBarTextViewLevel3NumColour);


        seekBarLevel1NumLED.setMax(maxNumLED - minNumLED);
        seekBarLevel2NumLED.setMax(maxNumLED - minNumLED);
        seekBarLevel3NumLED.setMax(maxNumLED - minNumLED);
        seekBarLevel1Time.setMax(maxTime - minTime);
        seekBarLevel2Time.setMax(maxTime - minTime);
        seekBarLevel3Time.setMax(maxTime - minTime);
        seekBarLevel1NumColour.setMax(maxNumColour - minNumColour);
        seekBarLevel2NumColour.setMax(maxNumColour - minNumColour);
        seekBarLevel3NumColour.setMax(maxNumColour - minNumColour);


        buttonApply = (Button) findViewById(R.id.buttonApply);        //initial the button for sending the data
        buttonApply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), "Changes Applied",
                        Toast.LENGTH_LONG).show();

                //if apply for easy
                if (spinner.getSelectedItem().toString().equals(paths[0])) {
                    Log.e(TAG, "Applied for easy mode");
                    DataHolder.setEasyLevel1NumLED(level1NumLEDprogress);
                    DataHolder.setEasyLevel2NumLED(level2NumLEDprogress);
                    DataHolder.setEasyLevel3NumLED(level3NumLEDprogress);
                    DataHolder.setEasyLevel1Time(level1Timeprogress);
                    DataHolder.setEasyLevel2Time(level2Timeprogress);
                    DataHolder.setEasyLevel3Time(level3Timeprogress);
                    DataHolder.setEasyLevel1NumColour(level1NumColourprogress);
                    DataHolder.setEasyLevel2NumColour(level2NumColourprogress);
                    DataHolder.setEasyLevel3NumColour(level3NumColourprogress);
                }


                //if apply for medium
                else if (spinner.getSelectedItem().toString().equals(paths[1])) {
                    Log.e(TAG, "Applied for medium mode");
                    DataHolder.setMediumLevel1NumLED(level1NumLEDprogress);
                    DataHolder.setMediumLevel2NumLED(level2NumLEDprogress);
                    DataHolder.setMediumLevel3NumLED(level3NumLEDprogress);
                    DataHolder.setMediumLevel1Time(level1Timeprogress);
                    DataHolder.setMediumLevel2Time(level2Timeprogress);
                    DataHolder.setMediumLevel3Time(level3Timeprogress);
                    DataHolder.setMediumLevel1NumColour(level1NumColourprogress);
                    DataHolder.setMediumLevel2NumColour(level2NumColourprogress);
                    DataHolder.setMediumLevel3NumColour(level3NumColourprogress);
                }
                //if apply for hard
                else if (spinner.getSelectedItem().toString().equals(paths[2])) {
                    Log.e(TAG, "Applied for hard mode");
                    DataHolder.setHardLevel1NumLED(level1NumLEDprogress);
                    DataHolder.setHardLevel2NumLED(level2NumLEDprogress);
                    DataHolder.setHardLevel3NumLED(level3NumLEDprogress);
                    DataHolder.setHardLevel1Time(level1Timeprogress);
                    DataHolder.setHardLevel2Time(level2Timeprogress);
                    DataHolder.setHardLevel3Time(level3Timeprogress);
                    DataHolder.setHardLevel1NumColour(level1NumColourprogress);
                    DataHolder.setHardLevel2NumColour(level2NumColourprogress);
                    DataHolder.setHardLevel3NumColour(level3NumColourprogress);
                }
            }
        });

        i = new Intent(this, SelectDifficulty.class);
        buttonExit = (Button) findViewById(R.id.buttonExit);        //initial the button for sending the data
        buttonExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(i);
            }
        });

    }

//    protected void onResume() {
//        super.onResume();
//        System.out.println("BlUNOActivity onResume from HowToPlay");
//    }


    //spinner onitemselected listener
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.blue));
        //   ((TextView) parent.getChildAt(0)).setTextSize(5);

        switch (position) {
            case 0:
                //DataHolder.setMode(DataHolder.Mode.EASY);
                Log.e(TAG, "easy mode selected");
                seekBarLevel1NumLED.setProgress(DataHolder.getEasyLevel1NumLED() - minNumLED);
                seekBarLevel2NumLED.setProgress(DataHolder.getEasyLevel2NumLED() - minNumLED);
                seekBarLevel3NumLED.setProgress(DataHolder.getEasyLevel3NumLED() - minNumLED);
                seekBarLevel1Time.setProgress(DataHolder.getEasyLevel1Time() - minTime);
                seekBarLevel2Time.setProgress(DataHolder.getEasyLevel2Time() - minTime);
                seekBarLevel3Time.setProgress(DataHolder.getEasyLevel3Time() - minTime);
                seekBarLevel1NumColour.setProgress(DataHolder.getEasyLevel1NumColour() - minNumColour);
                seekBarLevel2NumColour.setProgress(DataHolder.getEasyLevel2NumColour() - minNumColour);
                seekBarLevel3NumColour.setProgress(DataHolder.getEasyLevel3NumColour() - minNumColour);
                break;
            case 1:
                Log.e(TAG, "medium mode selected");
                //DataHolder.setMode(DataHolder.Mode.MEDIUM);
                //get dataholder and set progress accordingly
                seekBarLevel1NumLED.setProgress(DataHolder.getMediumLevel1NumLED() - minNumLED);
                seekBarLevel2NumLED.setProgress(DataHolder.getMediumLevel2NumLED() - minNumLED);
                seekBarLevel3NumLED.setProgress(DataHolder.getMediumLevel3NumLED() - minNumLED);
                seekBarLevel1Time.setProgress(DataHolder.getMediumLevel1Time() - minTime);
                seekBarLevel2Time.setProgress(DataHolder.getMediumLevel2Time() - minTime);
                seekBarLevel3Time.setProgress(DataHolder.getMediumLevel3Time() - minTime);
                seekBarLevel1NumColour.setProgress(DataHolder.getMediumLevel1NumColour() - minNumColour);
                seekBarLevel2NumColour.setProgress(DataHolder.getMediumLevel2NumColour() - minNumColour);
                seekBarLevel3NumColour.setProgress(DataHolder.getMediumLevel3NumColour() - minNumColour);
                break;
            case 2:
                Log.e(TAG, "hard mode selected");
                //DataHolder.setMode(DataHolder.Mode.HARD);
                //get dataholder and set progress accordingly
                seekBarLevel1NumLED.setProgress(DataHolder.getHardLevel1NumLED() - minNumLED);
                seekBarLevel2NumLED.setProgress(DataHolder.getHardLevel2NumLED() - minNumLED);
                seekBarLevel3NumLED.setProgress(DataHolder.getHardLevel3NumLED() - minNumLED);
                seekBarLevel1Time.setProgress(DataHolder.getHardLevel1Time() - minTime);
                seekBarLevel2Time.setProgress(DataHolder.getHardLevel2Time() - minTime);
                seekBarLevel3Time.setProgress(DataHolder.getHardLevel3Time() - minTime);
                seekBarLevel1NumColour.setProgress(DataHolder.getHardLevel1NumColour() - minNumColour);
                seekBarLevel2NumColour.setProgress(DataHolder.getHardLevel2NumColour() - minNumColour);
                seekBarLevel3NumColour.setProgress(DataHolder.getHardLevel3NumColour() - minNumColour);
                break;

        }
    }

    //spinner on nothing selected listener
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        Log.e(TAG, "show easy mode configuration by default");
        seekBarLevel1NumLED.setProgress(DataHolder.getEasyLevel1NumLED() - minNumLED);
        seekBarLevel2NumLED.setProgress(DataHolder.getEasyLevel2NumLED() - minNumLED);
        seekBarLevel3NumLED.setProgress(DataHolder.getEasyLevel3NumLED() - minNumLED);
        seekBarLevel1Time.setProgress(DataHolder.getEasyLevel1Time() - minTime);
        seekBarLevel2Time.setProgress(DataHolder.getEasyLevel2Time() - minTime);
        seekBarLevel3Time.setProgress(DataHolder.getEasyLevel3Time() - minTime);
        seekBarLevel1NumColour.setProgress(DataHolder.getEasyLevel1NumColour() - minNumColour);
        seekBarLevel2NumColour.setProgress(DataHolder.getEasyLevel2NumColour() - minNumColour);
        seekBarLevel3NumColour.setProgress(DataHolder.getEasyLevel3NumColour() - minNumColour);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {


        switch (seekBar.getId()) {

            case R.id.seekBarLevel1NumLED:
                //progress by default starts from 0, so need to add minimum so that will start at minimum.
                level1NumLEDprogress = progress + minNumLED;
                seekBarTextViewLevel1NumLED.setText("" + level1NumLEDprogress);

                break;
            case R.id.seekBarLevel2NumLED:
                level2NumLEDprogress = progress + minNumLED;
                seekBarTextViewLevel2NumLED.setText("" + level2NumLEDprogress);
                break;
            case R.id.seekBarLevel3NumLED:
                level3NumLEDprogress = progress + minNumLED;
                seekBarTextViewLevel3NumLED.setText("" + level3NumLEDprogress);
                break;

            case R.id.seekBarLevel1Time:
                level1Timeprogress = progress + minTime;
                seekBarTextViewLevel1Time.setText("" + level1Timeprogress );
                break;
            case R.id.seekBarLevel2Time:
                level2Timeprogress = progress + minTime;
                seekBarTextViewLevel2Time.setText("" + level2Timeprogress);
                break;
            case R.id.seekBarLevel3Time:
                level3Timeprogress = progress + minTime;
                seekBarTextViewLevel3Time.setText("" + level3Timeprogress);
                break;

            case R.id.seekBarLevel1NumColour:
                level1NumColourprogress = progress + minNumColour;
                seekBarTextViewLevel1NumColour.setText("" + level1NumColourprogress);
                break;
            case R.id.seekBarLevel2NumColour:
                level2NumColourprogress = progress + minNumColour;
                seekBarTextViewLevel2NumColour.setText("" +  level2NumColourprogress);
                break;
            case R.id.seekBarLevel3NumColour:
                level3NumColourprogress = progress + minNumColour;
                seekBarTextViewLevel3NumColour.setText("" +  level3NumColourprogress);
                break;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}