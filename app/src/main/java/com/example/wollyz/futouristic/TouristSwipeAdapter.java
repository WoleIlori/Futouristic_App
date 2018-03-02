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
 * Created by Wollyz on 01/02/2018.
 */
public class TouristSwipeAdapter extends PagerAdapter {
    private Context ctx;
    private ArrayList<TourNearby> availableTours;
    private LayoutInflater layoutInflater;

    public TouristSwipeAdapter(Context ctx, ArrayList<TourNearby> availableTours)
    {
        this.ctx = ctx;
        this.availableTours = availableTours;
    }

    @Override
    public int getCount(){
        return availableTours.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);

        TextView landmarkTv = (TextView)item_view.findViewById(R.id.landmark_name);
        landmarkTv.setText(availableTours.get(position).getLandmark());

        TextView guideTv = (TextView)item_view.findViewById(R.id.guide_name);
        landmarkTv.setText(availableTours.get(position).getGuideName());

        TextView priceTv = (TextView)item_view.findViewById(R.id.tour_price);
        landmarkTv.setText(Float.toString(availableTours.get(position).getPrice()));

        Button button = (Button)item_view.findViewById(R.id.addBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BusProvider.getInstance().post(new TouristInterestEvent(availableTours.get(position)));

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
