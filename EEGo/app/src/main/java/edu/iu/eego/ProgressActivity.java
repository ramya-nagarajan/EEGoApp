package edu.iu.eego;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProgressActivity extends Fragment{

    private String calmPoints = "0";

    private String calmSeconds = "";
    private String recoveries = "";
    private String totalSeconds = "180";
    private String planName = "";
    private String sessionNumber = "3";
    private String moodBefore = "";
    private String sessionLength = "";
    private String afterMood = "";
    private String moodDesc = "";
    EEGDatabaseHelper eegDatabaseHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overall_progress, container, false);

        eegDatabaseHelper = new EEGDatabaseHelper(getActivity().getApplicationContext());

        Intent intent = getActivity().getIntent();
        calmSeconds= intent.getStringExtra("calmSeconds");
        recoveries = intent.getStringExtra("recoveries");
        totalSeconds = "180";
        calmPoints = intent.getStringExtra("calmPoints");
        planName = intent.getStringExtra("planSelected");
        sessionNumber = "3";
        moodBefore = intent.getStringExtra("beforeMood");
        sessionLength = "3";
        afterMood = intent.getStringExtra("afterMood");
        moodDesc = intent.getStringExtra("moodDesc");

        ArrayList<Double> alphaList = (ArrayList<Double>) intent.getSerializableExtra("alphaList");

        TextView calmSecondsTextView = (TextView) rootView.findViewById(R.id.calmSecVal);
        calmSecondsTextView.setText(calmSeconds);
        TextView recoveriesTextView = (TextView) rootView.findViewById(R.id.numRecoveriesVal);
        recoveriesTextView.setText(recoveries);
        TextView totalSecondsTextView = (TextView) rootView.findViewById(R.id.totalSecVal);
        totalSecondsTextView.setText(totalSeconds);
        TextView heartRateTextView = (TextView) rootView.findViewById(R.id.avgHeartRateVal);
        heartRateTextView.setText("80");
        Button b = (Button) rootView.findViewById(R.id.addToCommunity);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommunityChallengeActivity();
            }
        });
//        Profile profile = new Profile("Life of Calm", "1", "sad", "180", "happy", "Hello I was sad. Now I am happy!",
//                "100", "1000", "4", "80");
        Profile profile = new Profile(planName, sessionNumber, moodBefore, sessionLength, afterMood,
                moodDesc,
                calmSeconds, calmPoints, recoveries, "80");
        eegDatabaseHelper.addSessionInfoToDB(profile);

        return rootView;
    }

    public void showCommunityChallengeActivity() {
        Intent intent = new Intent(getActivity().getApplicationContext(), CommunityChallengeActivity.class);
        intent.putExtra("calmPoints", calmPoints);
        startActivity(intent);
    }
}