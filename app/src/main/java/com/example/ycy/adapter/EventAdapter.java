package com.example.ycy.adapter;

import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.ycy.R;
import com.example.ycy.activity.EditActivity;
import com.example.ycy.bean.Event;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter {
    private List<Event> events;
    private Context context;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public EventAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new EventHolder(LayoutInflater.from(context),viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Event note = events.get(i);
        ((EventHolder)viewHolder).bind(note);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventTitle;
        private TextView eventDetail;
        private TextView eventCreateTime;
        private Event event;
        public EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.event_item,parent,false));
            itemView.setOnClickListener(this);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDetail = itemView.findViewById(R.id.event_detail);
            eventCreateTime = itemView.findViewById(R.id.event_createtime);
        }

        void bind(Event event){
            Date creatTime = event.getCreatTime();
            this.event = event;
            eventTitle.setText(event.getTitle());
            String detail = event.getDetail();
            if (detail.length() > 50)
                detail = detail.substring(0,50) + "。。。";
            eventDetail.setText(detail);
            eventCreateTime.setText("发表于：" + dateFormat.format(creatTime));
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra(EditActivity.TYPE,EditActivity.TYPE_CHANGE);
            intent.putExtra("id",event.getId());
            intent.putExtra("title",event.getTitle());
            intent.putExtra("detail", event.getDetail());
            intent.putExtra("isopen",event.isOpen());
            intent.putExtra("owner",event.getOwner());
            context.startActivity(intent);
        }
    }
}
