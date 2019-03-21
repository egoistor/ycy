package com.example.ycy.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ycy.R;

public class ShowPopUtil {
    private static PopupWindow pop;

    View.OnClickListener clickListener;



    public static void showPop(final Activity context,String str1,View.OnClickListener clickListener){
        View bottomView = View.inflate(context, R.layout.bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);
        mAlbum.setText(str1);
        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.5f;
        context.getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

//        View.OnClickListener clickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                switch (view.getId()) {
//                    case R.id.tv_album:
//                        if(ContextCompat.checkSelfPermission(context,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
//                                PackageManager.PERMISSION_GRANTED){
//                            ActivityCompat.requestPermissions(context,
//                                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//                        }
//                        else {
//                            OpenAlbumUtil.openAlbum(context);
//                        }
//                        break;
//
//                    case R.id.tv_cancel:
//                        //取消
//                        //closePopupWindow();
//                        break;
//                }
//                closePopupWindow();
//            }
//        };
        mAlbum.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }




    public static void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

}
