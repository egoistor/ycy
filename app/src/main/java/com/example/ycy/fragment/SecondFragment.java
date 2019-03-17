package com.example.ycy.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ycy.R;
import com.example.ycy.adapter.EventAdapter;
import com.example.ycy.adapter.OthersEventAdapter;
import com.example.ycy.bean.Event;
import com.example.ycy.bean.EventLab;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {
    private static String TAG = "SecondFragment";
    private static List<Event> othersEvents;
    private static OthersEventAdapter adapter;
    private View view;
    private RecyclerView mRecyclerView;
    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_second, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void  init(){
        mRecyclerView = view.findViewById(R.id.others_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    void update(){
        othersEvents =  EventLab.get(getContext()).getEvents();
        EventLab.get(getContext()).getOtherEventsFromNet(handler);
        if (adapter == null)
            adapter = new OthersEventAdapter(othersEvents,getContext());
        mRecyclerView.setAdapter(adapter);

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                othersEvents =  EventLab.get(getContext()).getOthersEvents();
                Log.d(TAG, "handleMessage: " + othersEvents.size());
                adapter.setEvents(othersEvents);
                adapter.notifyDataSetChanged();
            }
        }
    };

}
