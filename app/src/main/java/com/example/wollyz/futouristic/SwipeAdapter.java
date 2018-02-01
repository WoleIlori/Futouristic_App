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
public class SwipeAdapter extends PagerAdapter {
    private Context ctx;
    private ArrayList<String> landmarksToNotify;
    private LayoutInflater layoutInflater;

    public SwipeAdapter(Context ctx, ArrayList<String> landmarksToNotify)
    {
        this.ctx = ctx;
        this.landmarksToNotify = landmarksToNotify;
    }

    @Override
    public int getCount(){
        return landmarksToNotify.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position){
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        TextView textView = (TextView)item_view.findViewById(R.id.textview);
        textView.setText(landmarksToNotify.get(position));
        Button button = (Button)item_view.findViewById(R.id.addBtn);
        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new UserInterestEvent(landmarksToNotify.get(position)));

            }
        });
        */

        container.addView(item_view);
        return item_view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        container.removeView((LinearLayout)object);
    }
}
