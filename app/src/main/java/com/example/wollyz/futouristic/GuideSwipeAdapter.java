package com.example.wollyz.futouristic;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Wollyz on 02/02/2018.
 */
public class GuideSwipeAdapter extends PagerAdapter {
    private Context ctx;
    private ArrayList<String> landmarks;
    private ArrayList<String> chosenLandmarks;
    private int[] chosenLandmarksIndex;
    private LayoutInflater layoutInflater;
    private int i;
    private final int SIZE = 3;

    public GuideSwipeAdapter(Context ctx, ArrayList<String> landmarks)
    {
        this.ctx = ctx;
        this.landmarks = landmarks;
        chosenLandmarks = new ArrayList<String>();
        chosenLandmarksIndex = new int[SIZE];
        i = 0;
    }

    @Override
    public int getCount(){
        return landmarks.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.guide_swipe_layout, container, false);
        TextView textView = (TextView)item_view.findViewById(R.id.landmark_tv);
        textView.setText(landmarks.get(position));
        Button button = (Button)item_view.findViewById(R.id.add_landmark);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i < SIZE)
                {
                    chosenLandmarks.add(landmarks.get(position));
                    chosenLandmarksIndex[i] = position;
                    System.out.println(chosenLandmarks.get(i));
                    i++;
                }
                else
                {
                    BusProvider.getInstance().post(new GuideLandmarkChoiceEvent(chosenLandmarks, chosenLandmarksIndex));

                }
            }
        });
        container.addView(item_view);
        return item_view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout)object);
    }
}
