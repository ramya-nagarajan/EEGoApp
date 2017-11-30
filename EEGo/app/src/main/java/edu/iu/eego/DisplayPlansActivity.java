package edu.iu.eego;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayPlansActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_plans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_white);

        Intent intent = getIntent();
        if (null != intent) {
            String txt = intent.getStringExtra("buttonSelected");
            toolbar.setTitle(txt);
            setSupportActionBar(toolbar);

        }
    }

    public void showPlanInformation(View view) {

        Button btn = (Button) view;
        //TextView textView = (TextView) findViewById(R.id.plan2);
        switch(btn.getId()){
            case R.id.beginPlan1:
                textView = (TextView) findViewById(R.id.plan1);
                break;
            case R.id.beginPlan2:
                textView = (TextView) findViewById(R.id.plan2);
                break;
            case R.id.beginPlan3:
                textView = (TextView) findViewById(R.id.plan3);
                break;
                default:
                    break;

        }
        Intent intent = new Intent(getApplicationContext(), PlanInformationActivity.class);
        intent.putExtra("planSelected",textView.getText()+"");
        startActivity(intent);
    }

}
