package com.example.ycy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.example.ycy.R;
import com.example.ycy.activity.EditActivity;
import com.example.ycy.bean.Event;
import com.example.ycy.bean.MyUser;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OthersEventAdapter extends RecyclerView.Adapter {
    private List<Event> events;
    private Context context;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public OthersEventAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OthersEventHolder(LayoutInflater.from(context),viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Event note = events.get(i);
        ((OthersEventHolder)viewHolder).bind(note);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    private class OthersEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView eventPoster;
        private TextView eventTitle;
        private TextView eventDetail;
        private TextView eventCreateTime;
        private Event event;

        private String userName;
        public OthersEventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.shequ_event_item,parent,false));
            itemView.setOnClickListener(this);
            eventPoster = itemView.findViewById(R.id.event_poster);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDetail = itemView.findViewById(R.id.event_detail);
            eventCreateTime = itemView.findViewById(R.id.event_createtime);
        }

        void bind(Event event){
            try {
                MyUser myUser = AVObject.createWithoutData(MyUser.class,event.getOwner().getObjectId());
                eventPoster.setText("发布人：" + myUser.getUserName());
            } catch (AVException e) {
                Log.d("OthersEvent", "bind: "  );
                e.printStackTrace();
            }
            this.event = event;
            Date creatTime = event.getCreatTime();
            eventTitle.setText("标题：" + event.getTitle());
            String detail = event.getDetail();
            if (detail.length() > 50)
                detail = detail.substring(0,50) + "。。。";
            eventDetail.setText("内容：" + detail);
            eventCreateTime.setText("发表于：" + dateFormat.format(creatTime));
        //    eventPoster.setText("发布人：" + event.getOwner().getObjectId());
            AVObject avObject = AVObject.createWithoutData("_User",event.getOwner().getObjectId());
            avObject.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    eventPoster.setText("发布人：" + avObject.get("username"));
                    userName = (String) avObject.get("username");
                }
            });
          //  eventPoster.setText("发布人：" + avObject.get("username"));
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, EditActivity.class);
            intent.putExtra(EditActivity.TYPE,EditActivity.TYPE_VIEW);
            intent.putExtra("id",event.getId());
            intent.putExtra("title",event.getTitle());
            intent.putExtra("detail", event.getDetail());
            intent.putExtra("isopen",event.isOpen());
            intent.putExtra("owner",event.getOwner());
            intent.putExtra("postername",userName);
            context.startActivity(intent);
        }
    }
}
