package edu.iu.eego;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ramya on 11/22/2017.
 */


public class DiaryActivity extends Fragment {

    static int id =1;
    TextView text1;
    TextView date_text,date_diary;
    TextView before,before_diary;
    TextView moodBefore,moodBefore_diary;
    TextView after,after_diary;
    TextView moodAfter,moodAfter_diary;
    TextView desc;
    TextView showDayDescription;
    TextView hideDayDescription;
    TextView fullLengthDescription;
    TextView header;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_profile2, container, false);
        final RelativeLayout relLayout1 = (RelativeLayout) rootView.findViewById(R.id.allDaysView);
        final RelativeLayout relLayout2 = (RelativeLayout) rootView.findViewById(R.id.dayDetails);

        showDayDescription = new TextView(rootView.getContext());
        showDayDescription.generateViewId();
        showDayDescription.setText(">");
        showDayDescription.setTextSize(18);
        showDayDescription.setTextColor(Color.parseColor("#ffffff"));
        showDayDescription.setPadding(950, 170, 20, 10);
        showDayDescription.setClickable(true);


        text1 = new TextView(rootView.getContext());

        text1.setTextSize(18);
        text1.setTextColor(Color.parseColor("#ffffff"));
        text1.setText("A Life of Calm - Day 4");

        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams  layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,495);
        layoutParams.setMargins(15,40,15,484);
        text1.setLayoutParams(layoutParams);
        text1.setBackgroundResource(R.drawable.plan_1_image);
        text1.setPadding(20,20,138,10);
        text1.generateViewId();

        date_text = new TextView(rootView.getContext());
        date_text.setTextSize(10);
        date_text.setTextColor(Color.parseColor("#ffffff"));
        date_text.setText("November 22, 2017");
        date_text.setPadding(48,140,138,10);
        date_text.generateViewId();
        params.addRule(RelativeLayout.BELOW, text1.getId());
        date_text.setLayoutParams(params);


        before = new TextView(rootView.getContext());
        before.setTextSize(10);
        before.setTextColor(Color.parseColor("#ffffff"));
        before.setText("Before:");
        before.setPadding(48,190,138,10);

        moodBefore = new TextView(rootView.getContext());
        moodBefore.setTextSize(10);
        moodBefore.setTextColor(Color.parseColor("#ffffff"));
        moodBefore.setText("Frustrated");
        moodBefore.setPadding(170,190,138,10);

        after = new TextView(rootView.getContext());
        after.setTextSize(10);
        after.setTextColor(Color.parseColor("#ffffff"));
        after.setText("After: ");
        after.setPadding(48,240,138,10);

        moodAfter = new TextView(rootView.getContext());
        moodAfter.setTextSize(10);
        moodAfter.setTextColor(Color.parseColor("#ffffff"));
        moodAfter.setText("Relaxed");
        moodAfter.setPadding(140,240,138,10);

        desc = new TextView(rootView.getContext());
        desc.setTextSize(14);
        desc.setTextColor(Color.parseColor("#ffffff"));
        desc.setText("Today I was annoyed because our prototyping assignment took forever, so… ");
        desc.setPadding(48,330,138,10);

        relLayout1.addView(text1);
        relLayout1.addView(showDayDescription);
        relLayout1.addView(date_text);
        relLayout1.addView(before);
        relLayout1.addView(moodBefore);
        relLayout1.addView(after);
        relLayout1.addView(moodAfter);
        relLayout1.addView(desc);


        header = new TextView(rootView.getContext());
        header.setText("A Life of Calm-Day 4");
        header.setTextColor(Color.parseColor("#ffffff"));
        header.setTextSize(18);
        header.setPadding(200,60,0,0);

        date_diary = new TextView(rootView.getContext());
        date_diary.setTextSize(10);
        date_diary.setTextColor(Color.parseColor("#ffffff"));
        date_diary.setText("November 22,2017");
        date_diary.setPadding(200,160,0,0);
        date_diary.generateViewId();
//        params.addRule(RelativeLayout.BELOW, header.getId());
        date_diary.setLayoutParams(params);


        before_diary = new TextView(rootView.getContext());
        before_diary.setTextSize(10);
        before_diary.setTextColor(Color.parseColor("#ffffff"));
        before_diary.setText("Before: ");
        before_diary.setPadding(200,210,138,10);

        moodBefore_diary = new TextView(rootView.getContext());
        moodBefore_diary.setTextSize(10);
        moodBefore_diary.setTextColor(Color.parseColor("#ffffff"));
        moodBefore_diary.setText("Frustrated");
        moodBefore_diary.setPadding(310,210,0,10);

        after_diary = new TextView(rootView.getContext());
        after_diary.setTextSize(10);
        after_diary.setTextColor(Color.parseColor("#ffffff"));
        after_diary.setText("After: ");
        after_diary.setPadding(200,260,138,10);

        moodAfter_diary = new TextView(rootView.getContext());
        moodAfter_diary.setTextSize(10);
        moodAfter_diary.setTextColor(Color.parseColor("#ffffff"));
        moodAfter_diary.setText("Relaxed");
        moodAfter_diary.setPadding(300,260,0,10);



        fullLengthDescription = new TextView(rootView.getContext());
        fullLengthDescription.setTextSize(12);
        fullLengthDescription.setText("Today I was annoyed because our prototyping assigment took forever, so I wasn't able to work on my portfolio like I had originally planned." +
                "However, I'm grateful that we have a good team for this assignment, so working together hasn't been as difficult as some of the projects in the past."+
                "We're also making progress on our Mobile and Pervasive project, which is super exciting. It's been a great opportunity to learn how ot work alongside developers and learn about how they work."
        );
        fullLengthDescription.setTextColor(Color.parseColor("#ffffff"));
        fullLengthDescription.setPadding(200,320,138,10);
        hideDayDescription = new TextView(rootView.getContext());
        hideDayDescription.generateViewId();
        hideDayDescription.setText("<");
        hideDayDescription.setTextSize(18);
        hideDayDescription.setTextColor(Color.parseColor("#ffffff"));
        hideDayDescription.setPadding(50,30,200,10);
        hideDayDescription.setClickable(true);
        relLayout2.addView(hideDayDescription);
        relLayout2.addView(header);
        relLayout2.addView(date_diary);
        relLayout2.addView(before_diary);
        relLayout2.addView(moodBefore_diary);
        relLayout2.addView(after_diary);
        relLayout2.addView(moodAfter_diary);
        relLayout2.addView(fullLengthDescription);
        showDayDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayout1.setVisibility(View.INVISIBLE);
                relLayout2.setVisibility(View.VISIBLE);
            }

        });
        hideDayDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relLayout2.setVisibility(View.INVISIBLE);
                relLayout1.setVisibility(View.VISIBLE);
            }

        });
        return rootView;
    }
}
