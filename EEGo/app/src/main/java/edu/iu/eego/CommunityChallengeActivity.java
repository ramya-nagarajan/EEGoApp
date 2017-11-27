package edu.iu.eego;

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

public class CommunityChallengeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_challenge);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        int total_points = 5000;
        int points_earned = 400;
        int points_prev = 3000;
        int total_minus_points_earned = total_points - points_earned -points_prev;
        LayerDrawable community = (LayerDrawable) getResources().getDrawable(R.drawable.community);
        GradientDrawable black = (GradientDrawable) community.findDrawableByLayerId(R.id.item1);
        GradientDrawable darkOrange = (GradientDrawable) community.findDrawableByLayerId(R.id.item2);
        GradientDrawable lightOrange = (GradientDrawable) community.findDrawableByLayerId(R.id.item3);
        Resources r = getResources();
        float px1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, r.getDisplayMetrics());
        float px2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, r.getDisplayMetrics());
        community.setLayerInset(1,0,(int)px1,0,0);
        community.setLayerInset(2,0,(int)px2,0,0);
        ImageView imageView = (ImageView) findViewById(R.id.progress);
        imageView.setBackground(community);
        /*darkOrange.setBounds(0,30,0,0);
        lightOrange.setBounds(0,250,0,0);
        Item*/
    }

}
