package edu.iu.eego;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.AnnotationData;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.MessageType;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConfiguration;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileFactory;
import com.choosemuse.libmuse.MuseFileReader;
import com.choosemuse.libmuse.MuseFileWriter;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;
import com.choosemuse.libmuse.MuseVersion;
import com.choosemuse.libmuse.Result;
import com.choosemuse.libmuse.ResultLevel;

import org.w3c.dom.Text;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener {

    final Context context = this;

    private MuseManagerAndroid manager;

    private Muse muse;

    private final String TAG = "ConnectionActivity";

    private ConnectionListener connectionListener;

    String planSelected = "";


    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();
        // Format a message to show the change of connection state in the UI.
        final String status = p.getPreviousConnectionState() + " -> " + current;
        Log.i(TAG, status);

        if (current == ConnectionState.CONNECTED) {
            Log.i(TAG, "Muse connected:" + muse.getName());
            Button connectBtn = (Button) findViewById(R.id.connectButton);
            connectBtn.setText("Connected to " + muse.getName());
            connectBtn.setEnabled(false);
            Button startBtn = (Button) findViewById(R.id.startButton);
            startBtn.setBackgroundColor(Color.parseColor("#f5a623"));
            startBtn.setTextColor(Color.parseColor("#ffffff"));
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.INVISIBLE);

            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView noOfMinutes = (TextView) findViewById(R.id.noOfMinutes) ;
                    TextView currentMood = (TextView) findViewById(R.id.currentMoodView) ;
                    String minutes = noOfMinutes.getText()+"";
                    String mood = currentMood.getText()+"";

                    Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
                    intent.putExtra("museName", muse.getName());
                    intent.putExtra("noOfMinutes",minutes);
                    intent.putExtra("currentMood",mood);
                    intent.putExtra("planSelected", planSelected);
                    startActivity(intent);

                }
            });
        } else if(current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            Toast.makeText(context, "Headset Disconnected!", Toast.LENGTH_SHORT).show();
            this.muse.unregisterAllListeners();
            this.muse = null;
        }
    }

    public void museListChanged() {
        final List<Muse> list = manager.getMuses();
        initDialog(list);
    }

    public void initDialog(List<Muse> list) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        dialog.setContentView(R.layout.muse_headsets_dialog);
        // set the custom dialog components - text, image and button
        final LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.museHeadSetLayout);
        linearLayout.removeAllViews();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        int i = 0;
        for(Muse m:list) {
            TextView text1 = new TextView(context);
            text1.setTextSize(18);
            text1.setTextColor(Color.parseColor("#f5a623"));
            text1.setText((i+1)+" : "+m.getName());
            text1.setPadding(10,40,10,40);
            text1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onSelectMuseHeadSet(v);
                }
            });
            linearLayout.addView(text1);
            i++;
        }

        Button dialogRefreshButton = (Button) dialog.findViewById(R.id.refreshBtn);
        // if button is clicked, close the custom dialog
        dialogRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                linearLayout.removeAllViews();
                manager.stopListening();
                manager.startListening();
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        if(!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void onSelectMuseHeadSet(View v) {
        TextView tv = (TextView) v;
        String tv_string = tv.getText()+"";
        int firstNum = Integer.parseInt(tv_string.charAt(0)+"");

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        List<Muse> availableMuses = manager.getMuses();

        // Check that we actually have something to connect to.
        if (availableMuses.size() < 1 ) {
            Log.w(TAG, "There is nothing to connect to");
            Toast.makeText(context, "There is nothing to connect to", Toast.LENGTH_SHORT).show();
        } else {

            // Cache the Muse that the user has selected.
            muse = availableMuses.get(firstNum-1);
            // Unregister all prior listeners and register our data listener to
            // receive the MuseDataPacketTypes we are interested in.  If you do
            // not register a listener for a particular data type, you will not
            // receive data packets of that type.
            muse.unregisterAllListeners();
            muse.registerConnectionListener(connectionListener);
        }
        // Initiate a connection to the headband and stream the data asynchronously.
        muse.runAsynchronously();
    }

    class ConnectionListener extends MuseConnectionListener {
        final WeakReference<ConnectionActivity> activityRef;

        ConnectionListener(final WeakReference<ConnectionActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            activityRef.get().receiveMuseConnectionPacket(p, muse);
        }
    }

    class MuseL extends MuseListener {
        final WeakReference<ConnectionActivity> activityRef;

        MuseL(final WeakReference<ConnectionActivity> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void museListChanged() {
            activityRef.get().museListChanged();
        }
    }

    private void ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have the ACCESS_COARSE_LOCATION permission so create the dialogs asking
            // the user to grant us the permission.

            DialogInterface.OnClickListener buttonListener =
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(ConnectionActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    0);
                        }
                    };

            // This is the context dialog which explains to the user the reason we are requesting
            // this permission.  When the user presses the positive (I Understand) button, the
            // standard Android permission dialog will be displayed (as defined in the button
            // listener above).
            AlertDialog introDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(R.string.permission_dialog_description)
                    .setPositiveButton(R.string.permission_dialog_understand, buttonListener)
                    .create();
            introDialog.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = MuseManagerAndroid.getInstance();
        manager.setContext(this);
        ensurePermissions();
        setContentView(R.layout.activity_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);
        Button button = (Button) findViewById(R.id.connectButton);
        SeekBar seekBar = (SeekBar) findViewById(R.id.sessionLengthSeekBar);
        planSelected = getIntent().getStringExtra("planSelected");
        seekBar.setProgress(0);
        seekBar.setMax(17);
        final TextView noOfMinutes = (TextView)findViewById(R.id.noOfMinutes);
        noOfMinutes.setText("3 minutes");
        final WeakReference<ConnectionActivity> weakActivity =
                new WeakReference<ConnectionActivity>(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                noOfMinutes.setText(String.valueOf(progress+3)+" minute(s)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar seekBar2 = (SeekBar) findViewById(R.id.currentMoodSeekBar);
        seekBar2.setProgress(0);
        seekBar2.setMax(9);
        final TextView currentMoodView = (TextView)findViewById(R.id.currentMoodView);
        currentMoodView.setText("Loving");
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0) {
                    currentMoodView.setText("Loving");
                } else if(progress == 1) {
                    currentMoodView.setText("Excited");
                } else if(progress == 2) {
                    currentMoodView.setText("Happy");
                } else if(progress == 3) {
                    currentMoodView.setText("Calm");
                } else if(progress == 4) {
                    currentMoodView.setText("Neutral");
                } else if(progress == 5) {
                    currentMoodView.setText("Sad");
                } else if(progress == 6) {
                    currentMoodView.setText("Anxious");
                } else if(progress == 7) {
                    currentMoodView.setText("Angry");
                } else if(progress == 8) {
                    currentMoodView.setText("Depressed");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        // add button listener
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());

                if(isBluetoothEnabled()) {

                    // Register a listener to receive connection state changes.
                    connectionListener = new ConnectionListener(weakActivity);

                    manager.setMuseListener(new MuseL(weakActivity));

                    manager.startListening();
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(context, "Please turn on Bluetooth", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }



    public boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(null == muse) {
                if(null != manager) {
                    manager.stopListening();
                }

                onBackPressed();
            } else {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.confirm_back_press_dialog);

                Button yesButton = (Button) dialog.findViewById(R.id.yesBtn);
                Button noButton = (Button) dialog.findViewById(R.id.noBtn);
                // if button is clicked, close the custom dialog
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        manager.stopListening();

                        muse.unregisterAllListeners();
                        muse.disconnect();

                        onBackPressed();
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if (!dialog.isShowing()) {
                    dialog.show();
                }

                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
