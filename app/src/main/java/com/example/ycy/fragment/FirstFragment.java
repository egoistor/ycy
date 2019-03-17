package com.example.ycy.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.ycy.R;
import com.example.ycy.activity.EditActivity;
import com.example.ycy.adapter.EventAdapter;
import com.example.ycy.bean.Event;
import com.example.ycy.bean.EventLab;
import com.example.ycy.utils.CustomRecyclerScrollViewListener;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {
    private static String TAG = "FirstFragment";
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private static List<Event> events;
    private static EventAdapter adapter;
    private static Context mContext;
    private View view;
    public FirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
        mFloatingActionButton = view.findViewById(R.id.fad_create);
        mRecyclerView = view.findViewById(R.id.event_rv);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //优化item固定大小
      //  mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new CustomRecyclerScrollViewListener() {
            @Override
            public void show() {
                mFloatingActionButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFloatingActionButton.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mFloatingActionButton.animate().translationY(mFloatingActionButton.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    void update(){
        events =  EventLab.get(getContext()).getEvents();
        EventLab.get(getContext()).getMyEventFormNet(handler);
        if (adapter == null)
            adapter = new EventAdapter(events,getContext());
        mRecyclerView.setAdapter(adapter);

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                events =  EventLab.get(getContext()).getEvents();
                adapter.setEvents(events);
                adapter.notifyDataSetChanged();
            }
        }
    };

}
