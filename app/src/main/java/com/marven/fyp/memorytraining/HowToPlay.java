package com.marven.fyp.memorytraining;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

public class HowToPlay extends BaseActivity{
    private Button buttonNext;
    private TextView bottomTextView;
    Intent i;
    String howToPlayString;


    private static final String TAG = "OCVSample::Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        i = new Intent(this, SelectDifficulty.class);
        DataHolder.setLevel(1); //set level one by default

        buttonNext = (Button) findViewById(R.id.buttonNext);        //initial the button for sending the data
        bottomTextView = (TextView) findViewById(R.id.bottomTextView);

        if (DataHolder.getGameSelected() == 1)
         howToPlayString = "Memorise the colour and position of the lights which will appear for a few seconds and disappear afterwards. To win, place the same colour piece at the same place as it was shown!";
        if (DataHolder.getGameSelected() == 2)
          howToPlayString = "Copy Cat will make a random sequence. All you have to do is repeat the pattern with your fingers. Hit the wrong sequence and BZZZ! It's game over!";
        if (DataHolder.getGameSelected() == 3)
          howToPlayString = "Memorise the colour and position of the lights which will appear, and disappear only after you press start. Try to be fast because your score is based on the time taken to complete.";
        if (DataHolder.getGameSelected() == 4)
         howToPlayString = "how to play for game 4";

        bottomTextView.setText(howToPlayString);

        buttonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //i.putExtra("GameSelected",gameSelected);
                //i.putExtra("Level",level);
                startActivity(i);
            }
        });
    }






//    protected void onResume() {
//        super.onResume();
//        System.out.println("BlUNOActivity onResume from HowToPlay");
//    }

//
//    //spinner onitemselected listener
//    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
//
//        switch (position) {
//            case 0:
//                 DataHolder.setMode(DataHolder.Mode.EASY);
//                Log.e(TAG, "easy mode selected");
//                // Whatever you want to happen when the first item gets selected
//                break;
//            case 1:
//                DataHolder.setMode(DataHolder.Mode.MEDIUM);
//                Log.e(TAG, "medium mode selected");
//                // Whatever you want to happen when the second item gets selected
//                break;
//            case 2:
//                DataHolder.setMode(DataHolder.Mode.HARD);
//                Log.e(TAG, "hard mode selected");
//                // Whatever you want to happen when the thrid item gets selected
//                break;
//
//        }
//    }
//
//    //spinner on nothing selected listener
//    @Override
//    public void onNothingSelected(AdapterView<?> arg0) {;}


}