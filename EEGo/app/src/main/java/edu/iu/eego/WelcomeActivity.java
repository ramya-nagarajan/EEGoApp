package edu.iu.eego;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.choosemuse.libmuse.MuseDataPacketType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Welcome to EEGo");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void showPlansActivity(View view) {
        TextView textView = (TextView) view;
        Intent intent = new Intent(getApplicationContext(), DisplayPlansActivity.class);
        intent.putExtra("buttonSelected",textView.getText()+"");
        startActivity(intent);
    }

    public void showSessionActivity(View v) {
        Intent intent = new Intent(getApplicationContext(), SessionActivity.class);
        intent.putExtra("activeAlphaThreshold",0);
        intent.putExtra("calmAlphaThreshold",0);
        intent.putExtra("activeBetaThreshold",0);
        intent.putExtra("calmBetaThreshold",0);
        intent.putExtra("activeThetaThreshold",0);
        intent.putExtra("calmThetaThreshold",0);
        intent.putExtra("noOfMinutes",0+"");
        intent.putExtra("currentMood","happy");
        intent.putExtra("museName", "Muse-3B50");
        startActivity(intent);
    }

    public void showCommunityChallengeActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), CommunityChallengeActivity.class);
        intent.putExtra("calmPoints", "100");
        startActivity(intent);
    }

    public void showFeedbackActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_progress) {

        } else if (id == R.id.nav_plans) {

        } else if (id == R.id.nav_progress) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
