package com.example.ycy.bean;

import android.content.Context;
import android.widget.Toast;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EventLab {
    private static EventLab sNoteLab;
    private static List<Event> mEvents;
    private static Context mContext;

    public EventLab(Context context){
        mContext = context.getApplicationContext();
    }
    public static EventLab get(Context context){
        if (sNoteLab == null){
            sNoteLab = new EventLab(context);
        }
        return sNoteLab;
    }
    public   List<Event> getmEvent() {
        mEvents = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Event");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    AVObject.fetchAllInBackground(list, new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> avObjects, AVException avException) {
                            if (avException == null) {
                                for (int i = 0; i < avObjects.size(); i++) {
                                    AVObject avObject = avObjects.get(i);
                                    Event event = new Event(avObject.getObjectId(), avObject.getString("title"),
                                            avObject.getString("detail"), avObject.getDate("createdAt"), avObject.getBoolean("isopen"));
                                    mEvents.add(event);
                                }
                            } else {
                                Toast.makeText(mContext, "网络异常，请稍后", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }   else {
                    Toast.makeText(mContext, "网络异常，请稍后", Toast.LENGTH_SHORT).show();
                }
            }

        });
        return mEvents;
    }

    public static void addEvent(final Event event){
        AVObject eventSave = new AVObject("Event");// 构建对象
        eventSave.put("title", event.getTitle());// 设置名称
        eventSave.put("detail", event.getDetail());// 设置优先级
        eventSave.put("isopen",event.isOpen());
        eventSave.saveInBackground(new SaveCallback() {// 保存到服务端
            @Override
            public void done(AVException e) {
                if (e == null) {
                    mEvents.add(event);
                    Toast.makeText(mContext,"发表成功",Toast.LENGTH_SHORT).show();
                    // 存储成功
                   // 保存成功之后，objectId 会自动从服务端加载到本地
                } else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                    // 失败的话，请检查网络环境以及 SDK 配置是否正确
                }
            }
        });
    }
    public static void deleteEvent(final Event event){
        AVQuery<AVObject> avQuery = new AVQuery<>("Event");
        avQuery.getInBackground(event.getId(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null){
                    for (int i = 0; i < mEvents.size();i++){
                        if (mEvents.get(i).getId().equals(event.getId()))
                        {
                            mEvents.remove(i);
                            break;
                        }
                    }
                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateEvent(final Event event){
        AVQuery<AVObject> avQuery = new AVQuery<>("Event");
        avQuery.getInBackground(event.getId(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null){
                    avObject.put("title", event.getTitle());// 设置名称
                    avObject.put("detail", event.getDetail());// 设置优先级
                    avObject.put("isopen",event.isOpen());
                    avObject.saveInBackground(new SaveCallback() {// 保存到服务端
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(mContext,"修改成功",Toast.LENGTH_SHORT).show();
                                // 存储成功
                                // 保存成功之后，objectId 会自动从服务端加载到本地
                            } else {
                                Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                                // 失败的话，请检查网络环境以及 SDK 配置是否正确
                            }
                        }
                    });
                }else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}