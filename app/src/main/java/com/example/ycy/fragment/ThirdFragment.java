package com.example.ycy.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.ycy.R;
import com.example.ycy.activity.EditActivity;
import com.example.ycy.activity.LoginActivity;
import com.example.ycy.activity.MainActivity;
import com.example.ycy.bean.Event;
import com.example.ycy.bean.EventLab;
import com.example.ycy.utils.ImageTools;
import com.example.ycy.utils.OpenAlbumUtil;
import com.example.ycy.utils.ShowPopUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment implements View.OnClickListener {
    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;
    View view;
    Button mLogoutButton;
    PopupWindow pop;
    SharedPreferences sp;
    TextView mUserNameText;
    RelativeLayout mSettingHeadImgRL;
    ImageView mHeadImg;
    String path;
    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_third, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
        sp = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);;
        mLogoutButton = view.findViewById(R.id.logout_button);
        mUserNameText = view.findViewById(R.id.setting_name_tv1);
        mSettingHeadImgRL = view.findViewById(R.id.setting_headimg_r1);
        mHeadImg = view.findViewById(R.id.mine_setting_head);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.logOut();// 清除缓存用户对象

                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        mUserNameText.setText(sp.getString("user_name",""));

        mSettingHeadImgRL.setOnClickListener(this);

        EventLab.getHead(AVUser.getCurrentUser().getObjectId(),handlerPic);
    }

    private void showPop(){
        View bottomView = View.inflate(getActivity(), R.layout.bottom_dialog, null);
        TextView mAlbum = bottomView.findViewById(R.id.tv_album);
        TextView mCancel = bottomView.findViewById(R.id.tv_cancel);

        pop = new PopupWindow(bottomView, -1, -2);
        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        pop.setAnimationStyle(R.style.main_menu_photo_anim);
        pop.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()) {
                    case R.id.tv_album:
                        if(ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                                PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                        }
                        else {
                            OpenAlbumUtil.openAlbum(getActivity());
                        }
                        break;

                    case R.id.tv_cancel:
                        //取消
                        //closePopupWindow();
                        break;
                }
                closePopupWindow();
            }
        };
        mAlbum.setOnClickListener(clickListener);
        mCancel.setOnClickListener(clickListener);
    }

    public void closePopupWindow() {
        if (pop != null && pop.isShowing()) {
            pop.dismiss();
            pop = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_headimg_r1:
                ShowPopUtil.showPop(getActivity(),"相册", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_album:
                                OpenAlbumUtil.openAlbum(ThirdFragment.this);
                                break;

                            case R.id.tv_cancel:
                                //取消
                                //closePopupWindow();
                                break;
                        }
                        ShowPopUtil.closePopupWindow();
                    }
                });
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.d("31231232", "onActivityResult: "+ 312312312);
        super.onActivityResult(requestCode, resultCode, data);
        //第一层switch
        switch (requestCode) {
            case PHOTO_FROM_GALLERY:
                //第二层switch
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (Build.VERSION.SDK_INT >= 19) {
                            path = OpenAlbumUtil.handleImageOnKitKat(getActivity(),data);
                        } else {
                            path = OpenAlbumUtil.handleImageBeforeKitKat(getActivity(),data);
                        }
                        //              mPic.setImageURI(data.getData());
 //                       Log.d(TAG, "path: " + path);
//                        if (!"".equals(path)) {
//                            File file = new File(path);
//                            Uri cropUri;
//                            if (Build.VERSION.SDK_INT >= 24) {
//                                cropUri = FileProvider.getUriForFile(this,
//                                        "com.fzu.fzuxiaoyoutong.provider", file);
//                            } else {
//                                cropUri = Uri.fromFile(file);
//                            }
//                            cropPhoto(cropUri);
//                        }
                        if (data != null) {

                            AVUser avUser = AVUser.getCurrentUser();
                            if (path != null){
                                AVFile file = null;
                                try {
                                    file = AVFile.withAbsoluteLocalPath("LeanCloud.png", path);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(),"找不到图片",Toast.LENGTH_SHORT).show();
                                }
                                avUser.put("pic", file);
                            }

                            avUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Uri uri = data.getData();
                                        mHeadImg.setImageURI(uri);
                                        Toast.makeText(getContext(),"头像修改成功",Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(),"网络异常，请稍后",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //todo 添加图片
  //                          Log.d("231312312", "onActivityResult: " + uri.toString());
  //                          picUri = uri;
//                            mPic.setVisibility(View.VISIBLE);
//                            imageView.setVisibility(View.GONE);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                break;
//            case PHOTO_FROM_CAMERA:
//                if (resultCode == Activity.RESULT_OK) {
//                    SharedPreferences  sp = Activity.getSharedPreferences("loginUser", Context.MODE_PRIVATE);
//                    String user = sp.getString(("uri"), "");
//                    Uri uri = Uri.parse(user);
//                    updateDCIM(uri);
//                    try {
//                        //把URI转换为Bitmap，并将bitmap压缩，防止OOM(out of memory)
//                        Bitmap bitmap = ImageTools.getBitmapFromUri(uri, this);
//                        imageView.setImageBitmap(bitmap);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    removeCache("uri");
//                } else {
//                    Log.e("result", "is not ok" + resultCode);
//                }
//                break;
            default:
                break;
        }
    }

    Handler handlerPic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                  RequestOptions options = new RequestOptions();
                   options.error(R.drawable.default_head);
                Glide.with(getActivity()).applyDefaultRequestOptions(options).load(msg.obj).into(mHeadImg);
            }
        }
    };
}
