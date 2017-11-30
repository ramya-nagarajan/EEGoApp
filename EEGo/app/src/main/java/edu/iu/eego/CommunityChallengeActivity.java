package edu.iu.eego;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommunityChallengeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_challenge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        int total_points = 50000;
        Intent intent = getIntent();
        String calmPoints = intent.getStringExtra("calmPoints");
        int points_earned = Integer.parseInt(calmPoints);
        points_earned = 300;
        int points_prev = 30000;
        int new_total = points_earned + points_prev;
        int total_minus_points_earned = total_points - new_total;
        int total_minus_prev_earned_points = total_points - points_prev;
        Toast.makeText(getApplicationContext(), "total_minus_prev_earned_points: " + ((total_minus_prev_earned_points*250)/total_points), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "total_minus_points_earned: " + ((total_minus_points_earned*250)/total_points), Toast.LENGTH_SHORT).show();
        TextView secondsProgress = (TextView) findViewById(R.id.secondsProgress);
        secondsProgress.setText("+"+points_earned);
        LayerDrawable community = (LayerDrawable) getResources().getDrawable(R.drawable.community);
        Resources r = getResources();
        float px1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,((total_minus_points_earned*250)/total_points) , r.getDisplayMetrics());
        float px2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ((total_minus_prev_earned_points*250)/total_points), r.getDisplayMetrics());
        community.setLayerInset(1,0,(int)px1,0,0);
        community.setLayerInset(2,0,(int)px2,0,0);
        ImageView imageView = (ImageView) findViewById(R.id.progress);
        imageView.setBackground(community);

    }

}
