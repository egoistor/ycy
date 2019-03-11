package com.example.ycy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector extends BaseActivity{

    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        activities.clear();
    }

    //登录过期自动退回登录界面
    public static void layout(Context context){
        SharedPreferences.Editor editor=context
                .getSharedPreferences("user_info",Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        Intent intent=new Intent(context,LoginActivity.class);
        context.startActivity(intent);
    }

}
