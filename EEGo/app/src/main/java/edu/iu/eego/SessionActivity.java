package edu.iu.eego;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileFactory;
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SessionActivity extends AppCompatActivity {

    private List<Double> betaBuffer = new ArrayList<Double>();
    private List<Double> alphaBuffer = new ArrayList<Double>();
    private List<Double> thetaBuffer = new ArrayList<Double>();
    private List<Double> accelBuffer = new ArrayList<Double>();
    private final List<Double> hsiPrecisionBuffer = new ArrayList<Double>();

    List<List<Double>> betaList = new ArrayList<>();
    List<List<Double>> alphaList = new ArrayList<>();
    List<List<Double>> thetaList = new ArrayList<>();

    List<Double> alphaPerSecondList = new ArrayList<>();
    List<Double> betaPerSecondList = new ArrayList<>();
    List<Double> thetaPerSecondList = new ArrayList<>();

    List<Double> alphaSecondsList = new ArrayList<>();
    List<Double> betaSecondsList = new ArrayList<>();
    List<Double> thetaSecondsList = new ArrayList<>();

    private boolean hsiPrecisionStale;

    private boolean hasSessionStarted = false;

    final Context context = this;

    private MuseManagerAndroid manager;

    private Muse muse;

    private SessionActivity.DataListener dataListener;

    private final Handler handler = new Handler();

    private Double activeAlphaThreshold = 0.0;
    private Double calmAlphaThreshold = 0.0;
    private Double activeBetaThreshold = 0.0;
    private Double calmBetaThreshold = 0.0;
    private Double activeThetaThreshold = 0.0;
    private Double calmThetaThreshold = 0.0;



    private CountDownTimer countDownTimerSession = new CountDownTimer(180000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            //
            Log.i("SessionActivity", "alphaPerSecondList : " + alphaPerSecondList.size() );
            Log.i("SessionActivity", "betaPerSecondList : " + betaPerSecondList.size() );
            Log.i("SessionActivity", "thetaPerSecondList : " + thetaPerSecondList.size() );
            Double avg_alpha_per_second = 0.0;
            for(Double value: alphaPerSecondList) {
                avg_alpha_per_second+=value;
            }
            avg_alpha_per_second = avg_alpha_per_second/alphaPerSecondList.size();
            Double avg_beta_per_second = 0.0;
            for(Double value: betaPerSecondList) {
                avg_beta_per_second+=value;
            }
            avg_beta_per_second = avg_beta_per_second/betaPerSecondList.size();

            Double avg_theta_per_second = 0.0;
            for(Double value: thetaPerSecondList) {
                avg_theta_per_second+=value;
            }
            avg_theta_per_second = avg_theta_per_second/thetaPerSecondList.size();
            Log.i("SessionActivity", "avg_alpha_per_second : " + avg_alpha_per_second );
            Log.i("SessionActivity", "avg_beta_per_second : " + avg_beta_per_second );
            Log.i("SessionActivity", "avg_theta_per_second : " + avg_theta_per_second );
            Log.i("SessionActivity", "----------------------------------------------------" );
            alphaSecondsList.add(avg_alpha_per_second);
            betaSecondsList.add(avg_beta_per_second);
            thetaSecondsList.add(avg_theta_per_second);
            thetaPerSecondList.clear();
            betaPerSecondList.clear();
            alphaPerSecondList.clear();

            SeekBar progressIndicator = (SeekBar) findViewById(R.id.progressIndicator);
            progressIndicator.incrementProgressBy(1);
        }

        @Override
        public void onFinish() {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.session_completed);
            mp.start();
            mp.release();
            timeRemaining.setText(String.format("%d:%02d", 0, 0));
            hasSessionStarted = false;
            performAnalysis();
            //            final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//            final File file = new File(dir, "alpha_values.csv" );
//            /*if (file.exists()) {
//                file.delete();
//            }*/
//            FileWriter fileWriter = null;
//            try {
//                fileWriter = new FileWriter(file);
//                for (List l : alphaList) {
//                    StringBuilder sb = new StringBuilder();
//                    sb.append(l.get(0) + "," + l.get(1) + ","+l.get(2)+","+l.get(3));
//                    sb.append("\n");
//                    fileWriter.append(sb.toString());
//                }
//            } catch(Exception e) {
//                Toast.makeText(context, "Cannot store data", Toast.LENGTH_SHORT).show();
//            }
//            try {
//                fileWriter.close();
//            } catch (Exception e2) {
//                Toast.makeText(context, "Error occured", Toast.LENGTH_SHORT).show();
//            }


        }
    };

    public void performAnalysis() {
        Double calmSeconds = 0.0;
        Double numRecoveryTimes = 0.0;
        Double calmPoints = 0.0;
        Double neutralSeconds = 0.0;
        Double activeSeconds = 0.0;
        boolean theta_swapped = true;
        int countActive = 0;
        int countNeutral = 0;
        int countCalm = 0;
        if (calmThetaThreshold < activeThetaThreshold) {
            for (int i = 0; i < alphaSecondsList.size(); i++) {
                Double alpha_val = alphaSecondsList.get(i);
                Double beta_val = betaSecondsList.get(i);
                Double theta_val = thetaSecondsList.get(i);
                boolean alpha_active = false;
                if (alpha_val >= calmAlphaThreshold) {
                    countCalm++;
                    calmSeconds++;
                } else if (alpha_val < activeAlphaThreshold) {
                    countActive++;
                    activeSeconds++;
                } else {
                    countNeutral++;
                    neutralSeconds++;
                }
            }
        } else {
            for (int i = 0; i < alphaSecondsList.size(); i++) {
                Double alpha_val = alphaSecondsList.get(i);
                if (alpha_val >= activeAlphaThreshold) {
                    countActive++;
                    activeSeconds++;
                } else if (alpha_val < calmAlphaThreshold) {
                    countCalm++;
                    calmSeconds++;
                } else {
                    countNeutral++;
                    neutralSeconds++;
                }
            }
        }
        Log.i("SessionActivity", "calmSeconds: "+ calmSeconds);
        Log.i("SessionActivity", "neutralSeconds: "+ neutralSeconds);
        Log.i("SessionActivity", "activeSeconds: "+ activeSeconds);
        Toast.makeText(context, "calmSeconds:" + calmSeconds, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "neutralSeconds:" + neutralSeconds, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "activeSeconds:" + activeSeconds, Toast.LENGTH_SHORT).show();
    }

        /*if(alphaSecondsList.size() == betaSecondsList.size() && betaSecondsList.size() == thetaSecondsList.size()) {
            for(int i=0;i<alphaSecondsList.size();i++) {
                Double alpha_val = alphaSecondsList.get(i);
                Double beta_val = betaSecondsList.get(i);
                Double theta_val = thetaSecondsList.get(i);
                boolean alpha_active = false;
                if(alpha_val >= calmAlphaThreshold) {
                    countCalm++;
                    calmSeconds++;
                } else if(alpha_val < activeAlphaThreshold) {
                    countActive++;
                    activeSeconds++;
                } else {
                    countNeutral++;
                    neutralSeconds++;
                }
                *//*if(beta_val >= calmBetaThreshold) {
                    countCalm++;
                } else if(beta_val < activeBetaThreshold) {
                    countActive++;
                } else {
                    countNeutral++;
                }
                if(theta_val >= calmThetaThreshold) {
                    if(!theta_swapped) {
                       countActive++;
                    } else {
                        countCalm++;
                    }
                } else if(theta_val < activeThetaThreshold) {
                    if(!theta_swapped) {
                        countCalm++;
                    } else {
                        countActive++;
                    }
                } else {
                    countNeutral++;
                }
                if(countCalm >= 2) {
                    calmSeconds++;
                } else if((countNeutral == 2 && countCalm==1)) {
                    calmSeconds++;
                } else if(countNeutral >= 2) {
                    neutralSeconds++;
                }else if(countActive >= 1 ) {
                    activeSeconds++;
                }*//*
            }
        }*/


//        for(Double val: alphaSecondsList) {
//            if(val > calmAlphaThreshold) {
//                calmSeconds ++;
//            } else if(val< activeAlphaThreshold) {
//                activeSeconds ++;
//            } else {
//                neutralSeconds++;
//            }
//        }
//        for(Double val: alphaSecondsList) {
//            if(val > calmAlphaThreshold) {
//                calmSeconds ++;
//            } else if(val< activeAlphaThreshold) {
//                activeSeconds ++;
//            } else {
//                neutralSeconds++;
//            }
//        }
//        for(Double val: alphaSecondsList) {
//            if(val > calmAlphaThreshold) {
//                calmSeconds ++;
//            } else if(val< activeAlphaThreshold) {
//                activeSeconds ++;
//            } else {
//                neutralSeconds++;
//            }
//        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        manager = MuseManagerAndroid.getInstance();
        manager.setContext(this);
        Intent i = getIntent();
        String museName = i.getStringExtra("museName");


        activeAlphaThreshold = i.getDoubleExtra("activeAlphaThreshold",0.0);
        calmAlphaThreshold = i.getDoubleExtra("calmAlphaThreshold",0.0);
        activeBetaThreshold = i.getDoubleExtra("activeBetaThreshold",0.0);
        calmBetaThreshold = i.getDoubleExtra("calmBetaThreshold",0.0);
        activeThetaThreshold = i.getDoubleExtra("activeThetaThreshold",0.0);
        calmThetaThreshold = i.getDoubleExtra("calmThetaThreshold",0.0);

//        TextView activeAlphaThresholdView = (TextView) findViewById(R.id.activeAlpha);
//        TextView calmAlphaThresholdView = (TextView) findViewById(R.id.calmAlpha);
//        TextView activeBetaThresholdView = (TextView) findViewById(R.id.activeBeta);
//        TextView calmBetaThresholdView = (TextView) findViewById(R.id.calmBeta);
//        TextView activeThetaThresholdView = (TextView) findViewById(R.id.activeTheta);
//        TextView calmThetaThresholdView = (TextView) findViewById(R.id.calmTheta);

//        activeAlphaThresholdView.setText(activeAlphaThreshold+"");
//        calmAlphaThresholdView.setText(calmAlphaThreshold+"");
//        activeBetaThresholdView.setText(activeBetaThreshold+"");
//        calmBetaThresholdView.setText(calmBetaThreshold+"");
//        activeThetaThresholdView.setText(activeThetaThreshold+"");
//        calmThetaThresholdView.setText(calmThetaThreshold+"");

        /*Double activeAlphaThresholdTP = i.getDoubleExtra("activeAlphaThresholdTP",0.0);
        Double calmAlphaThresholdTP = i.getDoubleExtra("calmAlphaThresholdTP",0.0);
        Double activeBetaThresholdTP = i.getDoubleExtra("activeBetaThresholdTP",0.0);
        Double calmBetaThresholdTP = i.getDoubleExtra("calmBetaThresholdTP",0.0);
        Double activeThetaThresholdTP = i.getDoubleExtra("activeThetaThresholdTP",0.0);
        Double calmThetaThresholdTP = i.getDoubleExtra("calmThetaThresholdTP",0.0);

        Double activeAlphaThresholdFP = i.getDoubleExtra("activeAlphaThresholdFP",0.0);
        Double calmAlphaThresholdFP = i.getDoubleExtra("calmAlphaThresholdFP",0.0);
        Double activeBetaThresholdFP = i.getDoubleExtra("activeBetaThresholdFP",0.0);
        Double calmBetaThresholdFP = i.getDoubleExtra("calmBetaThresholdFP",0.0);
        Double activeThetaThresholdFP = i.getDoubleExtra("activeThetaThresholdFP",0.0);
        Double calmThetaThresholdFP = i.getDoubleExtra("calmThetaThresholdFP",0.0);*/



//        activeAlphaThresholdView.setText(activeAlphaThresholdTP+"");
//        calmAlphaThresholdView.setText(calmAlphaThresholdTP+"");
//        activeBetaThresholdView.setText(activeBetaThresholdTP+"");
//        calmBetaThresholdView.setText(calmBetaThresholdTP+"");
//        activeThetaThresholdView.setText(activeThetaThresholdTP+"");
//        calmThetaThresholdView.setText(calmThetaThresholdTP+"");
//
//        TextView activeAlphaThresholdView2 = (TextView) findViewById(R.id.activeAlpha2);
//        TextView calmAlphaThresholdView2 = (TextView) findViewById(R.id.calmAlpha2);
//        TextView activeBetaThresholdView2 = (TextView) findViewById(R.id.activeBeta2);
//        TextView calmBetaThresholdView2 = (TextView) findViewById(R.id.calmBeta2);
//        TextView activeThetaThresholdView2 = (TextView) findViewById(R.id.activeTheta2);
//        TextView calmThetaThresholdView2 = (TextView) findViewById(R.id.calmTheta2);
//
//        activeAlphaThresholdView2.setText(activeAlphaThresholdFP+"");
//        calmAlphaThresholdView2.setText(calmAlphaThresholdFP+"");
//        activeBetaThresholdView2.setText(activeBetaThresholdFP+"");
//        calmBetaThresholdView2.setText(calmBetaThresholdFP+"");
//        activeThetaThresholdView2.setText(activeThetaThresholdFP+"");
//        calmThetaThresholdView2.setText(calmThetaThresholdFP+"");


        List<Muse> availableMuses = manager.getMuses();
        WeakReference<SessionActivity> weakActivity =
                new WeakReference<SessionActivity> (this);
        dataListener = new SessionActivity.DataListener(weakActivity);
        for(Muse m:availableMuses) {
            if(m.getName().equals(museName)) {
                this.muse = m;
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
//                muse.registerDataListener(dataListener, MuseDataPacketType.THETA_RELATIVE);
//                muse.registerDataListener(dataListener, MuseDataPacketType.BETA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
                muse.registerDataListener(dataListener, MuseDataPacketType.HSI_PRECISION);
                muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
                muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
                muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);
            }
        }

    }

    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
        final long n = p.valuesSize();
        switch (p.packetType()) {
            case HSI_PRECISION:
                getHSIPrecisionValues(p);
                checkIfConnectionIsStillGood();
                hsiPrecisionStale = true;
                break;
            case ACCELEROMETER:
                getAccelValues(p);
                break;
            case ALPHA_RELATIVE:
                alphaBuffer = new ArrayList<Double> ();

                getEegChannelValues(alphaBuffer, p);
                if(hasSessionStarted) {
                    if(!(Double.isNaN(alphaBuffer.get(0)) || Double.isNaN(alphaBuffer.get(1)) ||
                            Double.isNaN(alphaBuffer.get(2)) || Double.isNaN(alphaBuffer.get(3)))) {
                        alphaList.add(alphaBuffer);
//                        Log.i("SessionActivity", "Adding alpha values");
                        alphaPerSecondList.add(getAvgReading(alphaBuffer));
                    }
                }
                break;
            case THETA_RELATIVE:
                thetaBuffer = new ArrayList<Double> ();
                getEegChannelValues(thetaBuffer, p);
                if(hasSessionStarted) {
                    if(!(Double.isNaN(thetaBuffer.get(0)) || Double.isNaN(thetaBuffer.get(1)) ||
                            Double.isNaN(thetaBuffer.get(2)) || Double.isNaN(thetaBuffer.get(3)))) {
                        thetaList.add(thetaBuffer);
//                        Log.i("SessionActivity", "Adding theta values");
                        thetaPerSecondList.add(getAvgReading(thetaBuffer));
                    }

                }

                break;
            case BETA_RELATIVE:
                betaBuffer = new ArrayList<Double> ();
                getEegChannelValues(betaBuffer, p);
                if(hasSessionStarted) {
                    if(!(Double.isNaN(betaBuffer.get(0)) || Double.isNaN(betaBuffer.get(1)) ||
                            Double.isNaN(betaBuffer.get(2)) || Double.isNaN(betaBuffer.get(3)))) {
                        betaList.add(betaBuffer);
//                        Log.i("SessionActivity", "Adding beta values");
                        betaPerSecondList.add(getAvgReading(betaBuffer));
                    }
                }
                break;
            case BATTERY:
            /*case DRL_REF:
            case QUANTIZATION:*/
            default:
                break;
        }
    }

    public Double getAvgReading(List<Double> buffer) {
        return (buffer.get(0) + buffer.get(1) + buffer.get(2) + buffer.get(3))/4;
    }

    public void playSession(View v) {
        Button play = (Button) v;
        play.setVisibility(View.INVISIBLE);
        Button pause = (Button) findViewById(R.id.pauseButton);
        pause.setVisibility(View.VISIBLE);
        MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.meditation_mixdown);
        mp.start();
        hasSessionStarted = true;
        Log.i("SessionActivity", "activeThetaThreshold: "+ activeThetaThreshold);
        Log.i("SessionActivity", "calmThetaThreshold: "+ calmThetaThreshold);
        Log.i("SessionActivity", "activeBetaThreshold: "+ activeBetaThreshold);
        Log.i("SessionActivity", "calmBetaThreshold: "+ calmBetaThreshold);
        Log.i("SessionActivity", "activeAlphaThreshold: "+ activeAlphaThreshold);
        Log.i("SessionActivity", "calmAlphaThreshold: "+ calmAlphaThreshold);
        startTimer(countDownTimerSession);

    }

    public void pauseSession(View v) {

    }

    public void startTimer(CountDownTimer timer) {
        timer.start();
    }
    public void stopTimer(CountDownTimer timer) {
        if (null != timer)
            timer.cancel();
    }

    public void checkIfConnectionIsStillGood() {
        int isGood = 0;
        int isPoor = 0;
        int isMedium = 0;
        if(hsiPrecisionBuffer.get(0) == 1) {
            isGood++;
        } else if(hsiPrecisionBuffer.get(0) == 2) {
            isMedium++;
        } else {
            isPoor++;
        }
        if(hsiPrecisionBuffer.get(1) == 1) {
            isGood++;
        } else if(hsiPrecisionBuffer.get(1) == 2) {
            isMedium++;
        } else {
            isPoor++;
        }
        if(hsiPrecisionBuffer.get(2) == 1) {
            isGood++;
        } else if(hsiPrecisionBuffer.get(2) == 2) {
            isMedium++;
        } else {
            isPoor++;
        }
        if(hsiPrecisionBuffer.get(3) == 1) {
            isGood++;
        } else if(hsiPrecisionBuffer.get(3) == 2) {
            isMedium++;
        } else {
            isPoor++;
        }

        if(isPoor > 0) {
            getSupportActionBar().setTitle("Calibration...");
            MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.connection_lost);
            mp.start();
            hasSessionStarted = false;
            mp.release();
        }
    }


    private void getEegChannelValues(List<Double> buffer, MuseDataPacket p) {
        buffer.add(p.getEegChannelValue(Eeg.EEG1));
        buffer.add(p.getEegChannelValue(Eeg.EEG2));
        buffer.add(p.getEegChannelValue(Eeg.EEG3));
        buffer.add(p.getEegChannelValue(Eeg.EEG4));
        buffer.add(p.getEegChannelValue(Eeg.AUX_LEFT));
        buffer.add(p.getEegChannelValue(Eeg.AUX_RIGHT));
    }

    private void getHSIPrecisionValues(MuseDataPacket p) {
        hsiPrecisionBuffer.clear();
        hsiPrecisionBuffer.add(p.getEegChannelValue(Eeg.EEG1));
        hsiPrecisionBuffer.add(p.getEegChannelValue(Eeg.EEG2));
        hsiPrecisionBuffer.add(p.getEegChannelValue(Eeg.EEG3));
        hsiPrecisionBuffer.add(p.getEegChannelValue(Eeg.EEG4));
    }

    private void getAccelValues(MuseDataPacket p) {
        accelBuffer.clear();
        accelBuffer.add(p.getAccelerometerValue(Accelerometer.X));
        accelBuffer.add(p.getAccelerometerValue(Accelerometer.Y));
        accelBuffer.add(p.getAccelerometerValue(Accelerometer.Z));
    }

    public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {

    }

    class DataListener extends MuseDataListener {
        final WeakReference<SessionActivity> activityRef;

        DataListener(final WeakReference<SessionActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            activityRef.get().receiveMuseDataPacket(p, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }

}
