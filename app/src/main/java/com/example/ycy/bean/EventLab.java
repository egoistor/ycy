package com.example.ycy.bean;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class EventLab {
    private static EventLab sNoteLab;
    private static List<Event> mEvents = new ArrayList<>();
    private static List<Event> mOthersEvents = new ArrayList<>();
    private static Context mContext;
    private static String TAG = "EventLab";
    public EventLab(Context context){
        mContext = context.getApplicationContext();
    }
    public static EventLab get(Context context){
        if (sNoteLab == null){
            sNoteLab = new EventLab(context);
        }
        return sNoteLab;
    }


    public static List<Event> getEvents() {
        return mEvents;
    }

    public static List<Event> getOthersEvents() {
        return mOthersEvents;
    }

    public static void setOthersEvents(List<Event> mOthersEvents) {
        EventLab.mOthersEvents = mOthersEvents;
    }

    public void getOtherEventsFromNet(final Handler handler){
        mOthersEvents.clear();
        AVQuery<AVObject> query = new AVQuery<>("Event");
        query.whereEqualTo("isopen",true);
        query.orderByAscending("createdAt");
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
                                            avObject.getString("detail"), avObject.getDate("createdAt"), avObject.getBoolean("isopen"), (AVUser)avObject.getAVObject("owner"));
                                    Log.d(TAG, "owner: " + (AVUser)avObject.getAVObject("owner"));
                                    mOthersEvents.add(event);
                                }
                                Collections.sort(mOthersEvents, new Comparator<Event>() {
                                    @Override
                                    public int compare(Event o1, Event o2) {
                                        return o2.getCreatTime().compareTo(o1.getCreatTime()) ;
                                    }
                                });
                                handler.sendEmptyMessage(0);
                                Log.d(TAG, "others: " + mOthersEvents.size());
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
    }

    public void getMyEventFormNet(final Handler handler) {
        mEvents.clear();
        AVQuery<AVObject> query = new AVQuery<>("Event");
        query.whereEqualTo("owner",AVUser.getCurrentUser());
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
                                    Log.d(TAG, "done: " + avObject.getDate("createdAt"));
                                    Event event = new Event(avObject.getObjectId(), avObject.getString("title"),
                                            avObject.getString("detail"), avObject.getDate("createdAt"), avObject.getBoolean("isopen"), AVUser.getCurrentUser());
                                    mEvents.add(event);
                                }
                                Collections.sort(mEvents, new Comparator<Event>() {
                                    @Override
                                    public int compare(Event o1, Event o2) {
                                        return o2.getCreatTime().compareTo(o1.getCreatTime()) ;
                                    }
                                });
                                handler.sendEmptyMessage(0);
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

    }

    public static void addEvent(final Event event, final Handler handler, String path){
        AVObject eventSave = new AVObject("Event");// 构建对象
        eventSave.put("title", event.getTitle());// 设置名称
        eventSave.put("detail", event.getDetail());// 设置优先级
        eventSave.put("isopen",event.isOpen());
        eventSave.put("owner",event.getOwner());
        if (path != null){
            AVFile file = null;
            try {
                file = AVFile.withAbsoluteLocalPath("LeanCloud.png", path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext,"找不到图片",Toast.LENGTH_SHORT).show();
            }
            eventSave.put("pic", file);
        }

        eventSave.saveInBackground(new SaveCallback() {// 保存到服务端
            @Override
            public void done(AVException e) {
                if (e == null) {
                    mEvents.add(event);
                    handler.sendEmptyMessage(0);
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
    public static void deleteEvent(final Event event, final Handler handler){
        final AVObject eventDelet = AVObject.createWithoutData("Event", event.getId());
        eventDelet.deleteInBackground(new DeleteCallback() {
            @Override
            public void done( AVException e) {
                if (e == null){
                    for (int i = 0; i < mEvents.size();i++){
                        if (mEvents.get(i).getId().equals(event.getId()))
                        {
                            mEvents.remove(i);
                            break;
                        }
                    }
                    handler.sendEmptyMessage(0);
                    Toast.makeText(mContext,"删除成功",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext,"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void updateEvent(final Event event, final Handler handler,final String path){
        AVQuery<AVObject> avQuery = new AVQuery<>("Event");
        avQuery.getInBackground(event.getId(), new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e == null){
                    avObject.put("title", event.getTitle());// 设置名称
                    avObject.put("detail", event.getDetail());// 设置优先级
                    avObject.put("isopen",event.isOpen());
                    if (path != null){
                        AVFile file = null;
                        try {
                            Log.d(TAG, "done: " + path);
                            file = AVFile.withAbsoluteLocalPath("LeanCloud.png", path);
                        } catch (FileNotFoundException fe) {
                            fe.printStackTrace();
                            Toast.makeText(mContext,"找不到图片",Toast.LENGTH_SHORT).show();
                        }
                        avObject.put("pic", file);
                    }
                    avObject.saveInBackground(new SaveCallback() {// 保存到服务端
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                handler.sendEmptyMessage(0);
                                Toast.makeText(mContext,"修改成功",Toast.LENGTH_SHORT).show();
                                // 存储成功
                                // 保存成功之后，objectId 会自动从服务端加载到本地
                            } else {
                                Log.d(TAG, "done: " + e.getMessage());
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

    public static void getPic(String id, final Handler handler){
        AVQuery<AVObject> query = new AVQuery<>("Event");
        query.whereEqualTo("objectId", id);
        query.include("pic");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null)
                {

                    Message message = new Message();
                    message.what = 0;
                    AVFile avFile = list.get(0).getAVFile("pic");
                    if (avFile != null)
                    message.obj = avFile.getUrl();
                    handler.sendMessage(message);
                }
                else {
                    Toast.makeText(mContext,"加载不到图片",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void getHead(String id, final Handler handler){
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.whereEqualTo("objectId", id);
        query.include("pic");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null)
                {
                    Message message = new Message();
                    message.what = 0;
                    AVFile avFile = list.get(0).getAVFile("pic");
                    if (avFile != null)
                    message.obj = avFile.getUrl();

                    handler.sendMessage(message);
                }
                else {
                    Log.d(TAG, "done: " + e.getMessage());
                    Toast.makeText(mContext,"加载不到图片",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }




}
