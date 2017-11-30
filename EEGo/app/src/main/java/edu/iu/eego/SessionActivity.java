package edu.iu.eego;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

    ArrayList<List<Double>> betaList = new ArrayList<>();
    ArrayList<List<Double>> alphaList = new ArrayList<>();
    ArrayList<List<Double>> thetaList = new ArrayList<>();

    ArrayList<Double> alphaPerSecondList = new ArrayList<>();
    ArrayList<Double> betaPerSecondList = new ArrayList<>();
    ArrayList<Double> thetaPerSecondList = new ArrayList<>();

    ArrayList<Double> alphaSecondsList = new ArrayList<>();
    ArrayList<Double> betaSecondsList = new ArrayList<>();
    ArrayList<Double> thetaSecondsList = new ArrayList<>();

    private boolean hsiPrecisionStale;

    private boolean hasSessionStarted = false;
    private  MediaPlayer mp;

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

    private int minutes = 1;
    private String currentMood = "Happy";

    private int  mpLength=0;
    private boolean isStopRequested = false;

    private boolean isPaused = false;

    private int secondsLeft = 0;
    private int minutesLeft = 0;

    private CountDownTimer countDownTimerSession = new CountDownTimer(180000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            if(isPaused) {
                secondsLeft = seconds;
                minutesLeft = minutes;
                stopTimer(countDownTimerSession);
            }
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
            if(isStopRequested) {
                stopTimer(countDownTimerSession);
                timeRemaining.setText(String.format("%d:%02d", 0, 0));
                hasSessionStarted = false;
                performAnalysis();
            }
        }

        @Override
        public void onFinish() {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            final MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.session_completed);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.release();
                }
            });
            timeRemaining.setText(String.format("%d:%02d", 0, 0));
            hasSessionStarted = false;
            performAnalysis();

        }
    };

    public void performAnalysis() {
        int calmSeconds = 0;
        int numRecoveryTimes = 0;
        int calmPointsRate = 10;
        int neutralSeconds = 0;
        int activeSeconds = 0;
        StringBuffer sb = new StringBuffer();
        if (calmThetaThreshold < activeThetaThreshold) {
            for (int i = 0; i < alphaSecondsList.size(); i++) {
                Double alpha_val = alphaSecondsList.get(i);
                if (alpha_val >= calmAlphaThreshold) {
                    calmSeconds++;
                    sb.append("C");
                } else if (alpha_val < activeAlphaThreshold) {
                    activeSeconds++;
                    sb.append("A");
                } else {
                    neutralSeconds++;
                    sb.append("N");
                }
            }
        } else {
            for (int i = 0; i < alphaSecondsList.size(); i++) {
                Double alpha_val = alphaSecondsList.get(i);
                if (alpha_val >= activeAlphaThreshold) {
                    activeSeconds++;
                    sb.append("A");
                } else if (alpha_val < calmAlphaThreshold) {
                    calmSeconds++;
                    sb.append("C");
                } else {
                    neutralSeconds++;
                    sb.append("N");
                }
            }
        }
        numRecoveryTimes = findRecoveries(sb);
        int calmPoints = calmSeconds * calmPointsRate;
        if(calmPoints <= 500) {
            calmPoints = calmPoints + 500;
        }
        Log.i("SessionActivity", "calmSeconds: "+ calmSeconds);
        Log.i("SessionActivity", "neutralSeconds: "+ neutralSeconds);
        Log.i("SessionActivity", "activeSeconds: "+ activeSeconds);
        Log.i("SessionActivity", "num of recoveries: "+ numRecoveryTimes);
        Log.i("SessionActivity", "calm points: "+ calmPoints);

        Toast.makeText(context, "calmSeconds:" + calmSeconds, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "neutralSeconds:" + neutralSeconds, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "activeSeconds:" + activeSeconds, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "numRecoveryTimes:" + numRecoveryTimes, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "calmPoints:" + calmPoints, Toast.LENGTH_SHORT).show();

        showProgressActivity(alphaSecondsList,calmSeconds,numRecoveryTimes, calmPoints, (calmSeconds + neutralSeconds + activeSeconds));

    }

    private int findRecoveries(StringBuffer sb) {
        String s = new String(sb.toString());
        int recoveries = 0;
        while(s.length() > 0) {
            int index = s.indexOf("AAAC") ;
            int  index2 = s.indexOf("AAAN");
            if(index == -1 && index2 == -1) {
                break;
            } else {
                if(index > index2) {
                    s = s.substring(0,index2);
                } else {
                    s = s.substring(0,index);
                }
                recoveries++;
            }
        }
        return recoveries;
    }

    public void showProgressActivity(ArrayList<Double> alphaList, int calmSeconds, int recoveries, int calmPoints, int totalSeconds) {
        Intent intent = new Intent(getApplicationContext(), OverallProgressActivity.class);
        intent.putExtra("calmSeconds", calmSeconds+"");
        intent.putExtra("recoveries", recoveries+"");
        intent.putExtra("totalSeconds", totalSeconds+"");
        intent.putExtra("calmPoints", calmPoints+"");
        intent.putExtra("alphaList", alphaList);
        startActivity(intent);
    }


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
        minutes = Integer.parseInt(i.getStringExtra("noOfMinutes"));
        currentMood = i.getStringExtra("currentMood");

        Toast.makeText(context, "minutes:"+ minutes, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "currentMood:"+ currentMood, Toast.LENGTH_SHORT).show();

        activeAlphaThreshold = i.getDoubleExtra("activeAlphaThreshold",0.0);
        calmAlphaThreshold = i.getDoubleExtra("calmAlphaThreshold",0.0);
        activeBetaThreshold = i.getDoubleExtra("activeBetaThreshold",0.0);
        calmBetaThreshold = i.getDoubleExtra("calmBetaThreshold",0.0);
        activeThetaThreshold = i.getDoubleExtra("activeThetaThreshold",0.0);
        calmThetaThreshold = i.getDoubleExtra("calmThetaThreshold",0.0);


        List<Muse> availableMuses = manager.getMuses();
        WeakReference<SessionActivity> weakActivity =
                new WeakReference<SessionActivity> (this);
        dataListener = new SessionActivity.DataListener(weakActivity);
        for(Muse m:availableMuses) {
            if(m.getName().equals(museName)) {
                this.muse = m;
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
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
        mp = MediaPlayer.create(SessionActivity.this, R.raw.meditation_mixdown);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });
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
        mp.pause();
        isPaused = true;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.resume_end);
        dialog.setCanceledOnTouchOutside(false);
        // set the custom dialog components - text, image and button
        mpLength = mp.getCurrentPosition();
        Button dialogResumeButton = (Button) dialog.findViewById(R.id.resumeBtn);
        // if button is clicked, close the custom dialog
        dialogResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPaused = false;
                dialog.dismiss();
                mp.seekTo(mpLength);
                mp.start();
//                manager.startListening();
                int mseconds = (secondsLeft + (minutesLeft*60)) *1000;
                countDownTimerSession = new CountDownTimer(mseconds,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
                        int seconds = (int) (millisUntilFinished / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
                        if(isPaused) {
                            secondsLeft = seconds;
                            minutesLeft = minutes;
                            stopTimer(countDownTimerSession);
                        }
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
                        if(isStopRequested) {
                            stopTimer(countDownTimerSession);
                            timeRemaining.setText(String.format("%d:%02d", 0, 0));
                            hasSessionStarted = false;
                            performAnalysis();
                        }
                    }

                    @Override
                    public void onFinish() {
                        TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
                        final MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.session_completed);
                        mp.start();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mp.release();
                            }
                        });
                        timeRemaining.setText(String.format("%d:%02d", 0, 0));
                        hasSessionStarted = false;
                        performAnalysis();

                    }
                };
                startTimer(countDownTimerSession);
            }
        });

        Button dialogEndButton = (Button) dialog.findViewById(R.id.endBtn);
        // if button is clicked, close the custom dialog
        dialogEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mp.stop();
                isStopRequested = true;
                TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
                final MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.session_completed);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.release();
                    }
                });
                muse.unregisterDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
                muse.unregisterDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
                muse.unregisterDataListener(dataListener, MuseDataPacketType.HSI_PRECISION);
                muse.unregisterDataListener(dataListener, MuseDataPacketType.BATTERY);
                muse.unregisterDataListener(dataListener, MuseDataPacketType.DRL_REF);
                muse.unregisterDataListener(dataListener, MuseDataPacketType.QUANTIZATION);
                timeRemaining.setText(String.format("%d:%02d", 0, 0));
                performAnalysis();
            }
        });

        if(!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void startTimer(CountDownTimer timer) {
        timer.start();
    }

    public void stopTimer(CountDownTimer timer) {
        if (null != timer)
            timer.cancel();
    }

    public void checkIfConnectionIsStillGood() {
        if(hasSessionStarted) {
            int isGood = 0;
            int isPoor = 0;
            int isMedium = 0;
            if (hsiPrecisionBuffer.get(0) == 1) {
                isGood++;
            } else if (hsiPrecisionBuffer.get(0) == 2) {
                isMedium++;
            } else {
                isPoor++;
            }
            if (hsiPrecisionBuffer.get(1) == 1) {
                isGood++;
            } else if (hsiPrecisionBuffer.get(1) == 2) {
                isMedium++;
            } else {
                isPoor++;
            }
            if (hsiPrecisionBuffer.get(2) == 1) {
                isGood++;
            } else if (hsiPrecisionBuffer.get(2) == 2) {
                isMedium++;
            } else {
                isPoor++;
            }
            if (hsiPrecisionBuffer.get(3) == 1) {
                isGood++;
            } else if (hsiPrecisionBuffer.get(3) == 2) {
                isMedium++;
            } else {
                isPoor++;
            }

            if (isPoor > 0) {
                mp.stop();
                mp = MediaPlayer.create(SessionActivity.this, R.raw.connection_lost);
                mp.start();
                hasSessionStarted = false;
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mp.release();
                        finish();
                    }
                });
            }
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
