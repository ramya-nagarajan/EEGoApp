package edu.iu.eego;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private final Handler handler = new Handler();

    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();

        // Format a message to show the change of connection state in the UI.
        final String status = p.getPreviousConnectionState() + " -> " + current;
        Log.i(TAG, status);

        if (current == ConnectionState.CONNECTED) {
            Log.i(TAG, "Muse connected:" + muse.getName());
            Button connectBtn = (Button) findViewById(R.id.connectButton);
            connectBtn.setText("Connected to " + muse.getName());
            Button startBtn = (Button) findViewById(R.id.startButton);
            startBtn.setBackgroundColor(Color.parseColor("#f5a623"));
            startBtn.setTextColor(Color.parseColor("#ffffff"));
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button startBtn = (Button) v;
                    startBtn.setText("Started");
                }
            });
        } else if(current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            Toast.makeText(context, "Connection State:" + muse.getConnectionState(), Toast.LENGTH_SHORT).show();
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
        dialog.setContentView(R.layout.muse_headsets_dialog);

        // set the custom dialog components - text, image and button
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.museHeadSetLayout);
        linearLayout.removeAllViews();
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
        }

        Button dialogRefreshButton = (Button) dialog.findViewById(R.id.refreshBtn);
        // if button is clicked, close the custom dialog
        dialogRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.stopListening();
                manager.startListening();
            }
        });

        dialog.show();
    }

    public void onSelectMuseHeadSet(View v) {
        TextView tv = (TextView) v;
        String tv_string = tv.getText()+"";
        int firstNum = Integer.parseInt(tv_string.charAt(0)+"");
        manager.stopListening();

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
        seekBar.setProgress(0);
        seekBar.setMax(19);
        final TextView noOfMinutes = (TextView)findViewById(R.id.noOfMinutes);
        noOfMinutes.setText("1 minute(s)");
        final WeakReference<ConnectionActivity> weakActivity =
                new WeakReference<ConnectionActivity>(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                noOfMinutes.setText(String.valueOf(progress+1)+" minute(s)");
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
        seekBar2.setMax(3);
        final TextView currentMoodView = (TextView)findViewById(R.id.currentMoodView);
        currentMoodView.setText("Happy");
        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 0) {
                    currentMoodView.setText("Happy");
                } else if(progress == 1) {
                    currentMoodView.setText("Relaxed");
                } else if(progress == 2) {
                    currentMoodView.setText("Angry");
                } else if(progress == 3) {
                    currentMoodView.setText("Frustrated");
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
}
