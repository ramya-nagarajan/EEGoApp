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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile2, container, false);
        RelativeLayout relLayout = (RelativeLayout)rootView.findViewById(R.id.relLayoutFragment2);
        //relLayout.removeAllViews();

        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        int days = 4;

        TextView text1 = new TextView(rootView.getContext());

        text1.setTextSize(18);
        text1.setTextColor(Color.parseColor("#ffffff"));
        text1.setText("A Life of Calm - Day 4");

        RelativeLayout.LayoutParams  layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,495);
        layoutParams.setMargins(15,40,15,484);
        text1.setLayoutParams(layoutParams);
        text1.setBackgroundResource(R.drawable.plan_1_image);
        text1.setPadding(20,20,138,10);
        //text1.setId(id);
        text1.generateViewId();

        TextView date_text = new TextView(rootView.getContext());
        date_text.setTextSize(10);
        date_text.setTextColor(Color.parseColor("#ffffff"));
        date_text.setText("November 22, 2017");
        date_text.setPadding(48,130,138,10);
        date_text.generateViewId();
        params.addRule(RelativeLayout.BELOW, text1.getId());
        date_text.setLayoutParams(params);
       /* RelativeLayout.LayoutParams  layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,225);
        layoutParams1.setMargins(40,107,15,484);
        date_text.setLayoutParams(layoutParams);*/

        TextView before = new TextView(rootView.getContext());
        before.setTextSize(10);
        before.setTextColor(Color.parseColor("#ffffff"));
        before.setText("Before:");
        before.setPadding(48,180,138,10);
       // RelativeLayout.LayoutParams  layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,225);
        //layoutParams2.setMargins(40,150,15,484);
      //  before.setLayoutParams(layoutParams);

       TextView moodBefore = new TextView(rootView.getContext());
        moodBefore.setTextSize(10);
        moodBefore.setTextColor(Color.parseColor("#ffffff"));
        moodBefore.setText("Frustrated");
        moodBefore.setPadding(170,180,138,10);
       /* RelativeLayout.LayoutParams  layoutParamsmoodBefore = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,225);
        layoutParamsmoodBefore.setMargins(150,40,15,484);
        moodBefore.setLayoutParams(layoutParamsmoodBefore);*/

        TextView after = new TextView(rootView.getContext());
        after.setTextSize(10);
        after.setTextColor(Color.parseColor("#ffffff"));
        after.setText("After: ");
        after.setPadding(48,230,138,10);
        /*RelativeLayout.LayoutParams  layoutParams3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,225);
        layoutParams2.setMargins(15,380,15,484);
        after.setLayoutParams(layoutParams);*/
        //add the after mood as a textview
        TextView moodAfter = new TextView(rootView.getContext());
        moodAfter.setTextSize(10);
        moodAfter.setTextColor(Color.parseColor("#ffffff"));
        moodAfter.setText("Relaxed");
        moodAfter.setPadding(140,230,138,10);

        TextView desc = new TextView(rootView.getContext());
        desc.setTextSize(14);
        desc.setTextColor(Color.parseColor("#ffffff"));
        desc.setText("Today I was annoyed because our prototyping assignment took forever, so… ");
        desc.setPadding(48,300,138,10);
        /*RelativeLayout.LayoutParams  layoutParams4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,225);
        layoutParams2.setMargins(15,510,15,484);
        desc.setLayoutParams(layoutParams);*/

        ImageButton dayDescription = new ImageButton(rootView.getContext());
        dayDescription.generateViewId();
        dayDescription.setBackgroundResource(R.drawable.ic_ab_front_white);
        //dayDescription.setTextColor(Color.parseColor("#7f000000"));
        dayDescription.setPadding(200,200,32,10);
        dayDescription.setElevation(20);
//        dayDescription.setHeight(9);
//        dayDescription.setWidth(9);
//        dayDescription.setBackgroundColor(Color.parseColor("#80000000"));
//        Resources r = getResources();
//        float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
//        float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9, r.getDisplayMetrics());
//        dayDescription.setHeight((int)height);
//        dayDescription.setWidth((int)width);
//        dayDescription.setLayoutParams(layoutParams);


        relLayout.addView(text1);
        relLayout.addView(date_text);
        relLayout.addView(before);
        relLayout.addView(moodBefore);
        relLayout.addView(after);
        relLayout.addView(moodAfter);
        relLayout.addView(dayDescription);

        relLayout.addView(desc);
        //Toast.makeText(rootView.getContext(), "this is fragment 2 id: "+rootView.findViewById(R.id.relLayoutFragment2), Toast.LENGTH_SHORT).show();
        return rootView;
    }
}
