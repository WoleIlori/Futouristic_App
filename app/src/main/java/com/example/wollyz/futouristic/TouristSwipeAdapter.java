package com.example.wollyz.futouristic;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wollyz.futouristic.RestApiPOJO.TourNearby;

import java.util.ArrayList;
import java.util.StringTokenizer;

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
        String[] summaries;
        summaries = parseTourSummary(availableTours.get(position).getSummary());
        TextView landmarkTv = (TextView)item_view.findViewById(R.id.landmark_name);
        landmarkTv.setText(availableTours.get(position).getLandmark());

        TextView guideTv = (TextView)item_view.findViewById(R.id.guide_name);
        guideTv.setText(availableTours.get(position).getGuideName());

        TextView priceTv = (TextView)item_view.findViewById(R.id.tour_price);
        priceTv.setText(Float.toString(availableTours.get(position).getPrice()));

        TextView start_time = (TextView)item_view.findViewById(R.id.start_time);
        start_time.setText(availableTours.get(position).getStartTime());

        TextView summary1 = (TextView)item_view.findViewById(R.id.highlight1Tv);
        summary1.setText(summaries[0]);

        TextView summary2 = (TextView)item_view.findViewById(R.id.highlight2Tv);
        summary2.setText(summaries[1]);

        TextView summary3 = (TextView)item_view.findViewById(R.id.highlight3Tv);
        summary3.setText(summaries[2]);

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

    private String[] parseTourSummary(String summary){
        String[] summaries = new String[3];
        StringTokenizer highlight = new StringTokenizer(summary,".");
        summaries[0] = highlight.nextToken();
        summaries[1] = highlight.nextToken();
        summaries[2] = highlight.nextToken();
        return summaries;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout)object);
    }
}
