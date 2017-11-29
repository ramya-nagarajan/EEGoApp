package edu.iu.eego;

import android.content.Context;
import android.content.Intent;
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

    private boolean hsiPrecisionStale;

    private boolean hasSessionStarted = false;

    final Context context = this;

    private MuseManagerAndroid manager;

    private Muse muse;

    private SessionActivity.DataListener dataListener;

    private final Handler handler = new Handler();

    private CountDownTimer countDownTimerSession = new CountDownTimer(60000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            int seconds = (int) (millisUntilFinished / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timeRemaining.setText(String.format("%d:%02d", minutes, seconds));
            SeekBar progressIndicator = (SeekBar) findViewById(R.id.progressIndicator);
            progressIndicator.incrementProgressBy(1);
        }

        @Override
        public void onFinish() {
            TextView timeRemaining = (TextView) findViewById(R.id.timeRemaining);
            MediaPlayer mp = MediaPlayer.create(SessionActivity.this, R.raw.session_completed);
            mp.start();
            timeRemaining.setText(String.format("%d:%02d", 0, 0));
            final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            final File file = new File(dir, "alpha_values.csv" );
            if (file.exists()) {
                file.delete();
            }
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
                for (List l : alphaList) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(l.get(0) + "," + l.get(1) + ","+l.get(2)+","+l.get(3));
                    sb.append("\n");
                    fileWriter.append(sb.toString());
                }
            } catch(Exception e) {
                Toast.makeText(context, "Cannot store data", Toast.LENGTH_SHORT).show();
                if(null != fileWriter) {
                    try {
                        fileWriter.close();
                    } catch (Exception e2) {
                        Toast.makeText(context, "Error occured", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    };

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
        Toast.makeText(context, "muse:" + museName, Toast.LENGTH_SHORT).show();

        Double activeAlphaThreshold = i.getDoubleExtra("activeAlphaThreshold",0.0);
        Double calmAlphaThreshold = i.getDoubleExtra("calmAlphaThreshold",0.0);
        Double activeBetaThreshold = i.getDoubleExtra("activeBetaThreshold",0.0);
        Double calmBetaThreshold = i.getDoubleExtra("calmBetaThreshold",0.0);
        Double activeThetaThreshold = i.getDoubleExtra("activeThetaThreshold",0.0);
        Double calmThetaThreshold = i.getDoubleExtra("calmThetaThreshold",0.0);

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
                muse.registerDataListener(dataListener, MuseDataPacketType.THETA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.BETA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
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
                hsiPrecisionStale = true;
                break;
            case ACCELEROMETER:
                getAccelValues(p);
                break;
            case ALPHA_RELATIVE:
                alphaBuffer = new ArrayList<Double> ();
                getEegChannelValues(alphaBuffer, p);
                if(hasSessionStarted) {
                    alphaList.add(alphaBuffer);
                }
                break;
            case THETA_RELATIVE:
                thetaBuffer = new ArrayList<Double> ();
                getEegChannelValues(thetaBuffer, p);
                if(hasSessionStarted) {
                    thetaList.add(thetaBuffer);
                }
                break;
            case BETA_RELATIVE:
                betaBuffer = new ArrayList<Double> ();
                getEegChannelValues(betaBuffer, p);
                if(hasSessionStarted) {
                    betaList.add(betaBuffer);
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
        final WeakReference<SessionActivity> activityRef;

        DataListener(final WeakReference<SessionActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            Log.i("DataListener", "receive muse data packet");
            activityRef.get().receiveMuseDataPacket(p, muse);
        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }

}

class CSVUtils {

    private static final char DEFAULT_SEPARATOR = ',';

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<Double> values, char separators) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (Double value : values) {
            if (!first) {
                sb.append(separators);
            }
            sb.append(followCVSformat(value.toString()));
            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }

}
