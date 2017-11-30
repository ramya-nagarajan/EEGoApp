package edu.iu.eego;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ramya on 11/22/2017.
 */


public class UserProfileActivity extends Fragment {
    EEGDatabaseHelper eegDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile1, container, false);
        eegDatabaseHelper = new EEGDatabaseHelper(getActivity().getApplicationContext());
        ArrayList<Profile> profileList = eegDatabaseHelper.fetchProfilesListFromDB();

        int i = 0;
        int totalCalmSeconds = 0;
        int totalRecoveries = 0;
        int totalSessions = 0;
        int totalSecondsFinal = 0;
        int totalDays = 0;
        double avg_min_per_session = 3;

        for(Profile p: profileList) {
            if(null != p.getCalmSeconds()) {
                totalCalmSeconds += Integer.parseInt(p.getCalmSeconds());
            } else {
                totalCalmSeconds += 40;
            }
            if(null != p.getRecoveries()) {
                totalRecoveries += Integer.parseInt(p.getRecoveries());
            } else {
                totalRecoveries += 1;
            }

            totalSessions++;
            totalDays++;
            if(null != p.getSessionLength()) {
                totalSecondsFinal += Integer.parseInt(p.getSessionLength()) * 180;
            } else {
                totalSecondsFinal += 3 * 180;
            }


        }
        double totalCalmTime = (((double)totalCalmSeconds / (totalSecondsFinal)) * 100);

        avg_min_per_session =  (totalSecondsFinal)/(totalSessions*60);
        Log.i("DB", "length:" + i);
        Log.i("DB", "totalCalmSeconds:" + totalCalmSeconds);
        Log.i("DB", "totalRecoveries:" + totalRecoveries);
        Log.i("DB", "avg_min_per_session:" + avg_min_per_session / totalSessions);
        Log.i("DB", "totalSessions:" + totalSessions);
        Log.i("DB", "totalDays:" + totalDays);
        Log.i("DB", "avg_min_per_session:" + avg_min_per_session);
        Log.i("DB", "totalCalmTime:" + totalCalmTime);
        Log.i("DB", "totalSecondsFinal:" + totalSecondsFinal);

        TextView totalMinutes = (TextView) rootView.findViewById(R.id.totalMinutes);
        TextView avgMinutesPerSession = (TextView) rootView.findViewById(R.id.avgMinutesPerSession);
        TextView recoveries = (TextView) rootView.findViewById(R.id.recoveries);
        TextView days = (TextView) rootView.findViewById(R.id.days);
        TextView progress = (TextView) rootView.findViewById(R.id.progress);
        TextView totalSessionsView = (TextView) rootView.findViewById(R.id.totalSessions);
        totalMinutes.setText(totalSessions*3+"");
        avgMinutesPerSession.setText(String.format("%.2g%n",(avg_min_per_session/(60))));
        recoveries.setText(totalRecoveries+"");
        days.setText(totalSessions+"");
        progress.setText(String.format("%.2g%n",(totalCalmTime)));
        totalSessionsView.setText(totalSessions+"");
        return rootView;
    }
}
