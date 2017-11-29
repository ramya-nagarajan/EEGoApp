package edu.iu.eego;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
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
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CalibrationActivity extends AppCompatActivity {

    private List<Double> betaBuffer = new ArrayList<Double>();
    private List<Double> alphaBuffer = new ArrayList<Double>();
    private List<Double> thetaBuffer = new ArrayList<Double>();
    private List<Double> accelBuffer = new ArrayList<Double>();
    private final List<Double> hsiPrecisionBuffer = new ArrayList<Double>();
    private boolean hsiPrecisionStale;
    private boolean isCalibrationTimerStarted = false;
    private boolean isStartTimerOn = false;
    private boolean isActiveTimerOn = false;
    private boolean isBreakTimerOn = false;
    private boolean isCalmTimerOn = false;
    private boolean isCalibrationCompleted = false;
    List<List<Double>> betaActiveList = new ArrayList<>();
    List<List<Double>> alphaActiveList = new ArrayList<>();
    List<List<Double>> thetaActiveList = new ArrayList<>();
    List<List<Double>> betaCalmList = new ArrayList<>();
    List<List<Double>> alphaCalmList = new ArrayList<>();
    List<List<Double>> thetaCalmList = new ArrayList<>();
    private Double calmAlphaThreshold;
    private Double calmBetaThreshold;
    private Double calmThetaThreshold;
    private Double activeAlphaThreshold;
    private Double activeBetaThreshold;
    private Double activeThetaThreshold;
    private Double calmAlphaThresholdTP;
    private Double calmBetaThresholdTP;
    private Double calmThetaThresholdTP;
    private Double activeAlphaThresholdTP;
    private Double activeBetaThresholdTP;
    private Double activeThetaThresholdTP;
    private Double calmAlphaThresholdFP;
    private Double calmBetaThresholdFP;
    private Double calmThetaThresholdFP;
    private Double activeAlphaThresholdFP;
    private Double activeBetaThresholdFP;
    private Double activeThetaThresholdFP;

    private CountDownTimer countDownTimerStart = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            SeekBar progressIndicator = (SeekBar) findViewById(R.id.progressIndicator);
            progressIndicator.incrementProgressBy(13);

        }

        @Override
        public void onFinish() {
            isStartTimerOn = false;
            isActiveTimerOn = true;
            SeekBar seekBar = (SeekBar) findViewById(R.id.progressIndicator);
            seekBar.setProgress(6);
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            timeRemaining.setText(String.format("%d:%02d", 30, 0));
            startTimer(countDownTimerActive);
        }


    };

    private CountDownTimer countDownTimerActive = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            SeekBar progressIndicator = (SeekBar) findViewById(R.id.progressIndicator);
            progressIndicator.incrementProgressBy(2);
        }

        @Override
        public void onFinish() {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            isActiveTimerOn = false;
            isBreakTimerOn = true;
            MediaPlayer mp = MediaPlayer.create(CalibrationActivity.this, R.raw.calibration_relax);
            mp.start();
            SeekBar seekBar = (SeekBar) findViewById(R.id.progressIndicator);
            seekBar.setProgress(6);
            timeRemaining.setText(String.format("%d:%02d", 5, 0));
            startTimer(countDownTimerBreak);
        }
    };

    private CountDownTimer countDownTimerBreak = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            SeekBar progressIndicator = (SeekBar) findViewById(R.id.progressIndicator);
            progressIndicator.incrementProgressBy(13);
        }

        @Override
        public void onFinish() {
            isBreakTimerOn = false;
            isCalmTimerOn = true;
            SeekBar seekBar = (SeekBar) findViewById(R.id.progressIndicator);
            seekBar.setProgress(6);
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            timeRemaining.setText(String.format("%d:%02d", 30, 0));
            startTimer(countDownTimerCalm);
        }
    };

    private CountDownTimer countDownTimerCalm = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            SeekBar progressIndicator = (SeekBar) findViewById(R.id.progressIndicator);
            progressIndicator.incrementProgressBy(2);
        }

        @Override
        public void onFinish() {
            isCalibrationCompleted = true;
            isCalmTimerOn = false;
            MediaPlayer mp = MediaPlayer.create(CalibrationActivity.this, R.raw.calibration_completed);
            mp.start();
            computeThresholds();
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            timeRemaining.setText("Calibration Completed! Click Next to start session");
            timeRemaining.setTextSize(14);
            Button nextActivityButton = (Button) findViewById(R.id.nextActivityButton);
            nextActivityButton.setBackgroundColor(Color.parseColor("#f5a623"));
            nextActivityButton.isEnabled();
            Intent intent = new Intent(getApplicationContext(), SessionActivity.class);

            intent.putExtra("activeAlphaThreshold",activeAlphaThreshold);
            intent.putExtra("calmAlphaThreshold",calmAlphaThreshold);
            intent.putExtra("activeBetaThreshold",activeBetaThreshold);
            intent.putExtra("calmBetaThreshold",calmBetaThreshold);
            intent.putExtra("activeThetaThreshold",activeThetaThreshold);
            intent.putExtra("calmThetaThreshold",calmThetaThreshold);

//            intent.putExtra("activeAlphaThresholdTP",activeAlphaThresholdTP);
//            intent.putExtra("calmAlphaThresholdTP",calmAlphaThresholdTP);
//            intent.putExtra("activeBetaThresholdTP",activeBetaThresholdTP);
//            intent.putExtra("calmBetaThresholdTP",calmBetaThresholdTP);
//            intent.putExtra("activeThetaThresholdTP",activeThetaThresholdTP);
//            intent.putExtra("calmThetaThresholdTP",calmThetaThresholdTP);
//            intent.putExtra("activeAlphaThresholdFP",activeAlphaThresholdFP);
//            intent.putExtra("calmAlphaThresholdFP",calmAlphaThresholdFP);
//            intent.putExtra("activeBetaThresholdFP",activeBetaThresholdFP);
//            intent.putExtra("calmBetaThresholdFP",calmBetaThresholdFP);
//            intent.putExtra("activeThetaThresholdFP",activeThetaThresholdFP);
//            intent.putExtra("calmThetaThresholdFP",calmThetaThresholdFP);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.EEG);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.HSI_PRECISION);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.BETA_RELATIVE);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.THETA_RELATIVE);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
            muse.unregisterDataListener(dataListener, MuseDataPacketType.BATTERY);
            intent.putExtra("museName", muse.getName());
            startActivity(intent);
        }
    };


    final Context context = this;

    private MuseManagerAndroid manager;

    private Muse muse;

    private DataListener dataListener;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        SeekBar seekBar = (SeekBar) findViewById(R.id.progressIndicator);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        manager = MuseManagerAndroid.getInstance();
        manager.setContext(this);
        Intent i = getIntent();
        String museName = i.getStringExtra("museName");
        List<Muse> availableMuses = manager.getMuses();
        WeakReference<CalibrationActivity> weakActivity =
                new WeakReference<CalibrationActivity> (this);
        dataListener = new DataListener(weakActivity);
        handler.post(tickUi);
        for(Muse m:availableMuses) {
            if(m.getName().equals(museName)) {
                this.muse = m;
                Toast.makeText(getApplicationContext(), "muse found: " +m.getName(),Toast.LENGTH_SHORT).show();
                muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
                muse.registerDataListener(dataListener, MuseDataPacketType.HSI_PRECISION);
                muse.registerDataListener(dataListener, MuseDataPacketType.BETA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.THETA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
                muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
            }
        }


    }

    private final Runnable tickUi = new Runnable() {
        @Override
        public void run() {
            if (hsiPrecisionStale && !isCalibrationCompleted) {
                updateHSIPrecision();
            }
            handler.postDelayed(tickUi, 1000 / 60);
        }
    };
    private void updateHSIPrecision() {
        Button tp9 = (Button) findViewById(R.id.tp9);
        Button tp10 = (Button) findViewById(R.id.tp10);
        Button fp1 = (Button) findViewById(R.id.fp1);
        Button fp2 = (Button) findViewById(R.id.fp2);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        int isGood = 0;
        int isPoor = 0;
        int isMedium = 0;
        if(hsiPrecisionBuffer.get(0) == 1) {
            isGood++;
            tp9.setBackgroundResource(R.drawable.ring_green);
        } else if(hsiPrecisionBuffer.get(0) == 2) {
            isMedium++;
            tp9.setBackgroundResource(R.drawable.ring_yellow);
        } else {
            isPoor++;
            tp9.setBackgroundResource(R.drawable.ring_red);
        }
        if(hsiPrecisionBuffer.get(1) == 1) {
            isGood++;
            fp1.setBackgroundResource(R.drawable.ring_green);
        } else if(hsiPrecisionBuffer.get(1) == 2) {
            isMedium++;
            fp1.setBackgroundResource(R.drawable.ring_yellow);
        } else {
            isPoor++;
            fp1.setBackgroundResource(R.drawable.ring_red);
        }
        if(hsiPrecisionBuffer.get(2) == 1) {
            isGood++;
            fp2.setBackgroundResource(R.drawable.ring_green);
        } else if(hsiPrecisionBuffer.get(2) == 2) {
            isMedium++;
            fp2.setBackgroundResource(R.drawable.ring_yellow);
        } else {
            isPoor++;
            fp2.setBackgroundResource(R.drawable.ring_red);
        }
        if(hsiPrecisionBuffer.get(3) == 1) {
            isGood++;
            tp10.setBackgroundResource(R.drawable.ring_green);
        } else if(hsiPrecisionBuffer.get(3) == 2) {
            isMedium++;
            tp10.setBackgroundResource(R.drawable.ring_yellow);
        } else {
            isPoor++;
            tp10.setBackgroundResource(R.drawable.ring_red);
        }
        TextView connectionStatus = (TextView) findViewById(R.id.connectionStatus);
        if(isPoor > 0) {
            getSupportActionBar().setTitle("Calibration...");
            connectionStatus.setText("Try to adjust your headset, as we are getting poor connectivity...");
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(Color.parseColor("#acacac"));
        } else if(isMedium <= 2 && isPoor >1){
            connectionStatus.setText("Try to adjust your headset, as we are getting poor connectivity...");
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(Color.parseColor("#acacac"));
        } else if(isMedium > 2){
            connectionStatus.setText("Your fit seems to be okay... Try to adjust more to get a better connectivity");
            nextButton.setEnabled(false);
            nextButton.setBackgroundColor(Color.parseColor("#acacac"));
        } else if(isGood == 4) {
            connectionStatus.setText("Your fit seems to be good... Do not move your headband now... Click Next");
            nextButton.setEnabled(true);
            nextButton.setBackgroundColor(Color.parseColor("#f5a623"));
            getSupportActionBar().setTitle("Calibration...");
        }
//       else if (isGood>2) {
//            if (isMedium == 2) {
//                RelativeLayout headSetPrecisionLayout = (RelativeLayout) findViewById(R.id.headSetPrecisionLayout);
//                headSetPrecisionLayout.setVisibility(View.INVISIBLE);
//                RelativeLayout calibrationLayout = (RelativeLayout) findViewById(R.id.calibrationLayout);
//                calibrationLayout.setVisibility(View.VISIBLE);
//                getSupportActionBar().setTitle("Calibration...");
//                canStartTimer = true;
//            }
//        }

        if(isCalibrationTimerStarted && isPoor > 0) {
            Toast.makeText(context, "Calibration stopped", Toast.LENGTH_SHORT).show();
            isCalmTimerOn = false;
            isActiveTimerOn = false;
            isBreakTimerOn = false;
            isStartTimerOn = false;
            isCalibrationTimerStarted = false;
            stopTimer(countDownTimerActive);
            stopTimer(countDownTimerCalm);
            stopTimer(countDownTimerBreak);
            stopTimer(countDownTimerStart);
            RelativeLayout headSetPrecisionLayout = (RelativeLayout) findViewById(R.id.headSetPrecisionLayout);
            headSetPrecisionLayout.setVisibility(View.VISIBLE);
            RelativeLayout calibrationLayout = (RelativeLayout) findViewById(R.id.calibrationLayout);
            calibrationLayout.setVisibility(View.INVISIBLE);
        }
    }

    public void startTimer(CountDownTimer timer) {
        timer.start();
    }
    public void stopTimer(CountDownTimer timer) {
        if (null != timer)
            timer.cancel();
    }

    public void switchViewsToCalibration(View v) {
        Button nextButton = (Button) v;
        if(nextButton.isEnabled()) {
            RelativeLayout headSetPrecisionLayout = (RelativeLayout) findViewById(R.id.headSetPrecisionLayout);
            headSetPrecisionLayout.setVisibility(View.INVISIBLE);
            RelativeLayout calibrationLayout = (RelativeLayout) findViewById(R.id.calibrationLayout);
            calibrationLayout.setVisibility(View.VISIBLE);
            SeekBar seekBar = (SeekBar) findViewById(R.id.progressIndicator);
            seekBar.setProgress(6);
            // calibration part 1 starts in 3...2...1... (think about your day or let your mind wander)
            MediaPlayer mp = MediaPlayer.create(CalibrationActivity.this, R.raw.calibration_starts);
            mp.start();
            startTimer(countDownTimerStart);
            isStartTimerOn = true;
            isCalibrationTimerStarted = true;
        }
    }

    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {

        final long n = p.valuesSize();
        switch (p.packetType()) {
            case HSI_PRECISION:
                getHSIPrecisionValues(p);
                hsiPrecisionStale = true;
                break;
            case ACCELEROMETER:
                getAccelValues(p);
                break;
            case ALPHA_RELATIVE:
                alphaBuffer = new ArrayList<Double> ();
                getEegChannelValues(alphaBuffer, p);
                if(!isCalibrationCompleted) {
                    if(isActiveTimerOn) {
                        alphaActiveList.add(alphaBuffer);
                    } else if(isCalmTimerOn) {
                        alphaCalmList.add(alphaBuffer);
                    }
                }
                break;
            case THETA_RELATIVE:
                thetaBuffer = new ArrayList<Double> ();
                getEegChannelValues(thetaBuffer, p);
                if(!isCalibrationCompleted) {
                    if(isActiveTimerOn) {
                        thetaActiveList.add(thetaBuffer);
                    } else if(isCalmTimerOn) {
                        thetaCalmList.add(thetaBuffer);
                    }
                }
                break;
            case BETA_RELATIVE:
                betaBuffer = new ArrayList<Double> ();
                getEegChannelValues(betaBuffer, p);
                if(!isCalibrationCompleted) {
                    if(isActiveTimerOn) {
                        betaActiveList.add(betaBuffer);
                    } else if(isCalmTimerOn) {
                        betaCalmList.add(betaBuffer);
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
        final WeakReference<CalibrationActivity> activityRef;

        DataListener(final WeakReference<CalibrationActivity> activityRef) {
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

    public void calculateAverageBetaValues() {
        Double avg_eeg1 = 0.0;
        Double avg_eeg2 = 0.0;
        Double avg_eeg3 = 0.0;
        Double avg_eeg4 = 0.0;
        for(List l : betaActiveList) {
            avg_eeg1 += (Double)l.get(0);
            avg_eeg2 += (Double)l.get(1);
            avg_eeg3 += (Double)l.get(2);
            avg_eeg4 += (Double)l.get(3);
            Log.i("CalibrationActivity", l.get(0) + " " + l.get(1) + " " + l.get(2) + " " + l.get(3));
        }
        Log.i("CalibrationActivity", "-----------------beta-completed------------------------------");
        for(List l : alphaActiveList) {
            avg_eeg1 += (Double)l.get(0);
            avg_eeg2 += (Double)l.get(1);
            avg_eeg3 += (Double)l.get(2);
            avg_eeg4 += (Double)l.get(3);
            Log.i("CalibrationActivity", l.get(0) + " " + l.get(1) + " " + l.get(2) + " " + l.get(3));
        }
        Log.i("CalibrationActivity", "-----------------alpha-completed------------------------------");
        for(List l : thetaActiveList) {
            avg_eeg1 += (Double)l.get(0);
            avg_eeg2 += (Double)l.get(1);
            avg_eeg3 += (Double)l.get(2);
            avg_eeg4 += (Double)l.get(3);
            Log.i("CalibrationActivity", l.get(0) + " " + l.get(1) + " " + l.get(2) + " " + l.get(3));
        }
        Log.i("CalibrationActivity", "-----------------theta-completed------------------------------");
        Toast.makeText(context, "eeg1:" + avg_eeg1, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "eeg2:" + avg_eeg2, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "eeg3:" + avg_eeg3, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "eeg4:" + avg_eeg4, Toast.LENGTH_SHORT).show();
        avg_eeg1/= betaActiveList.size();
        avg_eeg2/= betaActiveList.size();
        avg_eeg3/= betaActiveList.size();
        avg_eeg4/= betaActiveList.size();
        Intent intent = new Intent(getApplicationContext(), SessionActivity.class);
        intent.putExtra("eeg1",avg_eeg1);
        intent.putExtra("eeg2",avg_eeg2);
        intent.putExtra("eeg3",avg_eeg3);
        intent.putExtra("eeg4",avg_eeg4);
        intent.putExtra("museName", this.muse.getName());
        startActivity(intent);
    }

    public void computeThresholds() {
        computeAlphaThresholds();
        computeBetaThresholds();
        computeThetaThresholds();
    }

    public void computeAlphaThresholds() {
        List<Double> avgAlphaActiveBuffer = new ArrayList<Double>();
        activeAlphaThreshold = 0.0;
        for(List l : alphaActiveList) {
            Double avgAlphaReading = ((Double)l.get(0) + (Double)l.get(1) + (Double)l.get(2) + (Double)l.get(3))/4;
            avgAlphaActiveBuffer.add(avgAlphaReading);
        }
        for(Double reading: avgAlphaActiveBuffer) {
            activeAlphaThreshold += reading;
        }
        activeAlphaThreshold = activeAlphaThreshold/avgAlphaActiveBuffer.size();

        List<Double> avgAlphaCalmBuffer = new ArrayList<Double>();
        calmAlphaThreshold = 0.0;
        for(List l : alphaCalmList) {
            Double avgAlphaReading = ((Double)l.get(0) + (Double)l.get(1) + (Double)l.get(2) + (Double)l.get(3))/4;
            avgAlphaCalmBuffer.add(avgAlphaReading);
        }
        for(Double reading: avgAlphaCalmBuffer) {
            calmAlphaThreshold += reading;
        }
        calmAlphaThreshold = calmAlphaThreshold/avgAlphaCalmBuffer.size();
        Log.i("CalibrationActivity", "activeAlphaThreshold: " + activeAlphaThreshold + "calmAlphaThreshold: " + calmAlphaThreshold);
    }

    public void computeBetaThresholds() {
        List<Double> avgBetaActiveBuffer = new ArrayList<Double>();
        activeBetaThreshold = 0.0;
        for(List l : betaActiveList) {
            Double avgBetaReading = ((Double)l.get(0) + (Double)l.get(1) + (Double)l.get(2) + (Double)l.get(3))/4;
            avgBetaActiveBuffer.add(avgBetaReading);
        }
        for(Double reading: avgBetaActiveBuffer) {
            activeBetaThreshold += reading;
        }
        activeBetaThreshold = activeBetaThreshold/avgBetaActiveBuffer.size();

        List<Double> avgBetaCalmBuffer = new ArrayList<Double>();
        calmBetaThreshold = 0.0;
        for(List l : betaCalmList) {
            Double avgBetaReading = ((Double)l.get(0) + (Double)l.get(1) + (Double)l.get(2) + (Double)l.get(3))/4;
            avgBetaCalmBuffer.add(avgBetaReading);
        }
        for(Double reading: avgBetaCalmBuffer) {
            calmBetaThreshold += reading;
        }
        calmBetaThreshold = calmBetaThreshold/avgBetaCalmBuffer.size();
        Log.i("CalibrationActivity", "activeBetaThreshold: " + activeBetaThreshold + "calmBetaThreshold: " + calmBetaThreshold);
    }

    public void computeThetaThresholds() {
        List<Double> avgThetaActiveBuffer = new ArrayList<Double>();
        activeThetaThreshold = 0.0;
        for(List l : thetaActiveList) {
            Double avgThetaReading = ((Double)l.get(0) + (Double)l.get(1) + (Double)l.get(2) + (Double)l.get(3))/4;
            avgThetaActiveBuffer.add(avgThetaReading);
        }
        for(Double reading: avgThetaActiveBuffer) {
            activeThetaThreshold += reading;
        }
        activeThetaThreshold = activeThetaThreshold/avgThetaActiveBuffer.size();

        List<Double> avgThetaCalmBuffer = new ArrayList<Double>();
        calmThetaThreshold = 0.0;
        for(List l : thetaCalmList) {
            Double avgThetaReading = ((Double)l.get(0) + (Double)l.get(1) + (Double)l.get(2) + (Double)l.get(3))/4;
            avgThetaCalmBuffer.add(avgThetaReading);
        }
        for(Double reading: avgThetaCalmBuffer) {
            calmThetaThreshold += reading;
        }
        calmThetaThreshold = calmThetaThreshold/avgThetaCalmBuffer.size();
        Log.i("CalibrationActivity", "activeThetaThreshold: " + activeThetaThreshold + "calmThetaThreshold: " + calmThetaThreshold);
    }

    public void computeAlphaThresholds2() {
        List<Double> avgAlphaActiveBuffer1 = new ArrayList<Double>();
        List<Double> avgAlphaActiveBuffer2 = new ArrayList<Double>();
        activeAlphaThresholdTP = 0.0;
        activeAlphaThresholdFP = 0.0;
        for(List l : alphaActiveList) {
            Double avgAlphaReadingTP = ((Double)l.get(0) + (Double)l.get(3))/2;
            Double avgAlphaReadingFP = ((Double)l.get(1) + (Double)l.get(2))/2;
            avgAlphaActiveBuffer1.add(avgAlphaReadingTP);
            avgAlphaActiveBuffer2.add(avgAlphaReadingFP);
        }
        for(Double reading: avgAlphaActiveBuffer1) {
            activeAlphaThresholdTP += reading;
        }
        for(Double reading: avgAlphaActiveBuffer2) {
            activeAlphaThresholdFP += reading;
        }
        activeAlphaThresholdTP = activeAlphaThresholdTP/avgAlphaActiveBuffer1.size();
        activeAlphaThresholdFP = activeAlphaThresholdFP/avgAlphaActiveBuffer2.size();
        List<Double> avgAlphaCalmBuffer1 = new ArrayList<Double>();
        List<Double> avgAlphaCalmBuffer2 = new ArrayList<Double>();
        calmAlphaThresholdTP = 0.0;
        calmAlphaThresholdFP = 0.0;
        for(List l : alphaCalmList) {
            Double avgAlphaReadingTP = ((Double)l.get(0) + (Double)l.get(3))/2;
            Double avgAlphaReadingFP = ((Double)l.get(1) + (Double)l.get(2))/2;
            avgAlphaCalmBuffer1.add(avgAlphaReadingTP);
            avgAlphaCalmBuffer2.add(avgAlphaReadingFP);
        }
        for(Double reading: avgAlphaCalmBuffer1) {
            calmAlphaThresholdTP += reading;
        }
        for(Double reading: avgAlphaCalmBuffer2) {
            calmAlphaThresholdFP += reading;
        }
        calmAlphaThresholdTP = calmAlphaThresholdTP/avgAlphaCalmBuffer1.size();
        calmAlphaThresholdFP = calmAlphaThresholdFP/avgAlphaCalmBuffer2.size();
        Log.i("CalibrationActivity", "activeAlphaThresholdTP: " + activeAlphaThresholdTP + "calmAlphaThresholdTP: " + calmAlphaThresholdTP);
        Log.i("CalibrationActivity", "activeAlphaThresholdFP: " + activeAlphaThresholdFP + "calmAlphaThresholdFP: " + calmAlphaThresholdFP);
    }

    public void computeBetaThresholds2() {
        List<Double> avgBetaActiveBuffer1 = new ArrayList<Double>();
        List<Double> avgBetaActiveBuffer2 = new ArrayList<Double>();

        activeBetaThresholdTP = 0.0;
        activeBetaThresholdFP = 0.0;
        for(List l : betaActiveList) {
            Double avgBetaReadingTP = ((Double)l.get(0) + (Double)l.get(3))/2;
            Double avgBetaReadingFP = ( (Double)l.get(1) + (Double)l.get(2) )/2;
            avgBetaActiveBuffer1.add(avgBetaReadingTP);
            avgBetaActiveBuffer2.add(avgBetaReadingFP);
        }
        for(Double reading: avgBetaActiveBuffer1) {
            activeBetaThresholdTP += reading;
        }
        for(Double reading: avgBetaActiveBuffer2) {
            activeBetaThresholdFP += reading;
        }
        activeBetaThresholdTP = activeBetaThresholdTP/avgBetaActiveBuffer1.size();
        activeBetaThresholdFP = activeBetaThresholdFP/avgBetaActiveBuffer2.size();
        List<Double> avgBetaCalmBuffer1 = new ArrayList<Double>();
        List<Double> avgBetaCalmBuffer2 = new ArrayList<Double>();
        calmBetaThresholdTP = 0.0;
        calmBetaThresholdFP = 0.0;
        for(List l : betaCalmList) {
            Double avgBetaReadingTP = ((Double)l.get(0) + (Double)l.get(3))/2;
            Double avgBetaReadingFP = ((Double)l.get(1) + (Double)l.get(2))/2;
            avgBetaCalmBuffer1.add(avgBetaReadingTP);
            avgBetaCalmBuffer2.add(avgBetaReadingFP);
        }
        for(Double reading: avgBetaCalmBuffer1) {
            calmBetaThresholdTP += reading;
        }
        for(Double reading: avgBetaCalmBuffer2) {
            calmBetaThresholdFP += reading;
        }
        calmBetaThresholdTP = calmBetaThresholdTP/avgBetaCalmBuffer1.size();
        calmBetaThresholdFP = calmBetaThresholdFP/avgBetaCalmBuffer2.size();
        Log.i("CalibrationActivity", "activeBetaThresholdTP: " + activeBetaThresholdTP + "calmBetaThresholdTP: " + calmBetaThresholdTP);
        Log.i("CalibrationActivity", "activeBetaThresholdFP: " + activeBetaThresholdFP + "calmBetaThresholdFP: " + calmBetaThresholdFP);
    }

    public void computeThetaThresholds2() {
        List<Double> avgThetaActiveBuffer1 = new ArrayList<Double>();
        List<Double> avgThetaActiveBuffer2 = new ArrayList<Double>();
        activeThetaThresholdTP = 0.0;
        activeThetaThresholdFP = 0.0;
        for(List l : thetaActiveList) {
            Double avgThetaReading1 = ((Double)l.get(0)+ (Double)l.get(3))/2;
            Double avgThetaReading2 = ((Double)l.get(1) + (Double)l.get(2))/2;
            avgThetaActiveBuffer1.add(avgThetaReading1);
            avgThetaActiveBuffer2.add(avgThetaReading2);
        }
        for(Double reading: avgThetaActiveBuffer1) {
            activeThetaThresholdTP += reading;
        }
        for(Double reading: avgThetaActiveBuffer2) {
            activeThetaThresholdFP += reading;
        }
        activeThetaThresholdTP = activeThetaThresholdTP/avgThetaActiveBuffer1.size();
        activeThetaThresholdFP = activeThetaThresholdFP/avgThetaActiveBuffer2.size();

        List<Double> avgThetaCalmBuffer1 = new ArrayList<Double>();
        List<Double> avgThetaCalmBuffer2 = new ArrayList<Double>();
        calmThetaThresholdTP = 0.0;
        calmThetaThresholdFP = 0.0;
        for(List l : thetaCalmList) {
            Double avgThetaReadingTP = ((Double)l.get(0)+ (Double)l.get(3))/2;
            Double avgThetaReadingFP = ((Double)l.get(1) + (Double)l.get(2))/2;
            avgThetaCalmBuffer1.add(avgThetaReadingTP);
            avgThetaCalmBuffer2.add(avgThetaReadingFP);
        }
        for(Double reading: avgThetaCalmBuffer1) {
            calmThetaThresholdTP += reading;
        }
        for(Double reading: avgThetaCalmBuffer2) {
            calmThetaThresholdFP += reading;
        }
        calmThetaThresholdTP = calmThetaThresholdTP/avgThetaCalmBuffer1.size();
        calmThetaThresholdFP = calmThetaThresholdFP/avgThetaCalmBuffer2.size();
        Log.i("CalibrationActivity", "activeThetaThresholdTP: " + activeThetaThresholdTP + "calmThetaThresholdTP: " + calmThetaThresholdTP);
        Log.i("CalibrationActivity", "activeThetaThresholdFP: " + activeThetaThresholdFP + "calmThetaThresholdFP: " + calmThetaThresholdFP);
    }


}
