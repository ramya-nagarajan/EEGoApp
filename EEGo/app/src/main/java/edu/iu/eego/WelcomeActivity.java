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

    public void showConnectionActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), ConnectionActivity.class);
        startActivity(intent);
    }

    public void showPlansActivity(View view) {
        TextView textView = (TextView) view;
        Intent intent = new Intent(getApplicationContext(), DisplayPlansActivity.class);
        intent.putExtra("buttonSelected",textView.getText()+"");
        startActivity(intent);
    }

    public void showCommunityChallengeActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), CommunityChallengeActivity.class);
        startActivity(intent);
    }

    public void showProgressActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), OverallProgressActivity.class);
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

//    public void writeFile(View v) {
//        List<List<Double>> alphaList = new ArrayList<>();
//        List<Double> alphabuffer = new ArrayList();
//        alphabuffer.add(5.0);
//        alphabuffer.add(10.0);
//        alphabuffer.add(20.0);
//        alphabuffer.add(30.0);
//        alphaList.add(alphabuffer);
//        alphabuffer = new ArrayList();
//        alphabuffer.add(35.0);
//        alphabuffer.add(40.0);
//        alphabuffer.add(45.0);
//        alphabuffer.add(50.0);
//        alphaList.add(alphabuffer);
////        File sdCard = Environment.getExternalStorageDirectory();
////        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        // Get the directory for the user's public pictures directory.
//        File dir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS), "muse");
//        if(!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        File file = new File(dir, "test.txt");
////        if(!file.exists()) {
////            try {
////                file.createNewFile();
////            } catch(Exception e) {
////                e.printStackTrace();
////            }
////        }
//        try {
//            FileOutputStream f = new FileOutputStream(file);
//            f.write("10.0, 20.0,30.0".getBytes());
//            f.close();
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
//        /*final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        final File file = new File(dir, "alpha_values.csv" );
//            *//*if (file.exists()) {
//                file.delete();
//            }*//*
//        FileWriter fileWriter = null;
//        try {
//            fileWriter = new FileWriter(file);
//            for (List l : alphaList) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(l.get(0) + "," + l.get(1) + ","+l.get(2)+","+l.get(3));
//                sb.append("\n");
//                fileWriter.append(sb.toString());
//            }
//        } catch(Exception e) {
//            Toast.makeText(getApplicationContext(), "Cannot store data", Toast.LENGTH_SHORT).show();
//        }
//        try {
//            fileWriter.close();
//        } catch (Exception e2) {
//            Toast.makeText(getApplicationContext(), "Error occured", Toast.LENGTH_SHORT).show();
//        }*/
//
//    }
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
