package edu.iu.eego;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.Toast;


public class PlanInformationActivity extends AppCompatActivity {

    public static int scrollX = 0;
    public static int scrollY = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_information);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);

    }

    public void showConnectionActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ConnectionActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onPause()
    {

        super.onPause();

    }

    public void showBorders(View view){

        /*HorizontalScrollView hsv = (HorizontalScrollView)findViewById(R.id.dayScroll);
        scrollX = hsv.getScrollX();
        scrollY = hsv.getScrollY();*/
        //setContentView(R.layout.activity_plan_information);
        GradientDrawable gd = new GradientDrawable();

        gd.setColor(0xFFF5A623);
        gd.setCornerRadius(5);
        gd.setStroke(1, 0xFFF5A623);

        findViewById(R.id.day1).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day2).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day3).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day4).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day5).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day6).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day7).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day8).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day9).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day10).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day11).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day12).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day13).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day14).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day15).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day16).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day17).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day18).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day19).setBackgroundResource(android.R.drawable.btn_default);
        findViewById(R.id.day20).setBackgroundResource(android.R.drawable.btn_default);


        switch(view.getId()){
            case R.id.day1:
                findViewById(R.id.day1).setBackground(gd);
                //hsv.computeScroll();
                break;
            case R.id.day2:
                findViewById(R.id.day2).setBackground(gd);
                break;
            case R.id.day3:
                findViewById(R.id.day3).setBackground(gd);
                break;
            case R.id.day4:
                findViewById(R.id.day4).setBackground(gd);
                break;
            case R.id.day5:
                findViewById(R.id.day5).setBackground(gd);
                //hsv.computeScroll();
                break;
            case R.id.day6:
                findViewById(R.id.day6).setBackground(gd);
                //hsv.scrollTo(scrollX,scrollY);
                break;
            case R.id.day7:
                findViewById(R.id.day7).setBackground(gd);
                break;
            case R.id.day8:
                findViewById(R.id.day8).setBackground(gd);
                break;
            case R.id.day9:
                findViewById(R.id.day9).setBackground(gd);
                break;
            case R.id.day10:
                findViewById(R.id.day10).setBackground(gd);
                break;
            case R.id.day11:
                findViewById(R.id.day11).setBackground(gd);
                break;
            case R.id.day12:
                findViewById(R.id.day12).setBackground(gd);
                break;
            case R.id.day13:
                findViewById(R.id.day13).setBackground(gd);
                break;
            case R.id.day14:
                findViewById(R.id.day14).setBackground(gd);
                break;
            case R.id.day15:
                findViewById(R.id.day15).setBackground(gd);
                break;
            case R.id.day16:
                findViewById(R.id.day16).setBackground(gd);
                break;
            case R.id.day17:
                findViewById(R.id.day17).setBackground(gd);
                break;
            case R.id.day18:
                findViewById(R.id.day18).setBackground(gd);
                break;
            case R.id.day19:
                findViewById(R.id.day19).setBackground(gd);
                break;
            case R.id.day20:
                findViewById(R.id.day20).setBackground(gd);
                break;
            default:
                Toast.makeText(PlanInformationActivity.this,"default case", Toast.LENGTH_SHORT).show();
                break;
        }
//        Button btn = (Button) findViewById(R.id.day1);
//        btn.setBackground(gd);

        Button start = (Button) findViewById(R.id.startDay);
        start.setVisibility(view.VISIBLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
    }
}
