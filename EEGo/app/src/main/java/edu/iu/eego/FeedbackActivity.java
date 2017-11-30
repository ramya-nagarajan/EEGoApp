package edu.iu.eego;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);

        SeekBar mood = (SeekBar) findViewById(R.id.currentMoodFeedback);
        mood.setProgress(0);
        mood.setMax(9);
        final TextView currentMoodView = (TextView)findViewById(R.id.currentMood);
        currentMoodView.setText("Happy");
        mood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0) {
                    currentMoodView.setText("Loving");
                } else if(progress == 1) {
                    currentMoodView.setText("Excited");
                } else if(progress == 2) {
                    currentMoodView.setText("Happy");
                } else if(progress == 3) {
                    currentMoodView.setText("Calm");
                } else if(progress == 4) {
                    currentMoodView.setText("Neutral");
                } else if(progress == 5) {
                    currentMoodView.setText("Sad");
                } else if(progress == 6) {
                    currentMoodView.setText("Anxious");
                } else if(progress == 7) {
                    currentMoodView.setText("Angry");
                } else if(progress == 8) {
                    currentMoodView.setText("Depressed");
                }

            }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
        });
    }


}