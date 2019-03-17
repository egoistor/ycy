package com.example.ycy.bean;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.ArrayList;
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
                                    mOthersEvents.add(event);
                                }
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
                                    Event event = new Event(avObject.getObjectId(), avObject.getString("title"),
                                            avObject.getString("detail"), avObject.getDate("createdAt"), avObject.getBoolean("isopen"), AVUser.getCurrentUser());
                                    mEvents.add(event);
                                }
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

    public static void addEvent(final Event event, final Handler handler){
        AVObject eventSave = new AVObject("Event");// 构建对象
        eventSave.put("title", event.getTitle());// 设置名称
        eventSave.put("detail", event.getDetail());// 设置优先级
        eventSave.put("isopen",event.isOpen());
        eventSave.put("owner",event.getOwner());
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

    public static void updateEvent(final Event event, final Handler handler){
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
                                handler.sendEmptyMessage(0);
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
