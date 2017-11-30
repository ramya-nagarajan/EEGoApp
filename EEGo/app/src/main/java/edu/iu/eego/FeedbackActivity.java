package edu.iu.eego;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity {

    private String calmPoints = "0";
    private String calmSeconds = "0";
    private String totalSeconds = "0";
    private String recoveries = "0";
    ArrayList<Double> alphaList = new ArrayList<Double>();
    private String planSelected = "";
    private int minutes = 1;
    private String beforeMood = "Happy";

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
        Intent intent = getIntent();
        calmSeconds = intent.getStringExtra("calmSeconds");
        recoveries = intent.getStringExtra("recoveries");
        totalSeconds = intent.getStringExtra("totalSeconds");
        calmPoints = intent.getStringExtra("calmPoints");
        alphaList = (ArrayList<Double>) intent.getSerializableExtra("alphaList");
        planSelected = intent.getStringExtra("planSelected");
        minutes = intent.getIntExtra("noOfMinutes", 3);
        beforeMood = intent.getStringExtra("currentMood");
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


    public void showProgressActivity() {
        EditText moodDesc = (EditText) findViewById(R.id.moodDescription);
        TextView currentMood = (TextView) findViewById(R.id.currentMood);
        Intent intent = new Intent(getApplicationContext(), OverallProgressActivity.class);
        intent.putExtra("calmSeconds", calmSeconds+"");
        intent.putExtra("recoveries", recoveries+"");
        intent.putExtra("totalSeconds", totalSeconds+"");
        intent.putExtra("calmPoints", calmPoints+"");
        intent.putExtra("alphaList", alphaList);
        intent.putExtra("", planSelected);
        intent.putExtra("moodDesc", moodDesc.getText()+"");
        intent.putExtra("afterMood", currentMood.getText());
        intent.putExtra("beforeMood", beforeMood);
        intent.putExtra("minutes", minutes);
        startActivity(intent);
    }

    public void addToDB(View v) {

//        Toast.makeText(getApplicationContext(), "mood:"+ currentMood.getText(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getApplicationContext(), "moodDesc:"+ moodDesc.getText(), Toast.LENGTH_SHORT).show();
        showProgressActivity();
    }


}