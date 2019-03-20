package com.example.ycy.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.ycy.R;
import com.example.ycy.bean.Event;
import com.example.ycy.bean.EventLab;
import com.example.ycy.utils.ImageTools;
import com.example.ycy.utils.OpenAlbumUtil;
import com.example.ycy.utils.ShowPopUtil;
import com.example.ycy.utils.SoftKeyboardUtil;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "EditActivity";
    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;
    private ImageView imageView;
    private File appDir;
    private Uri uriForCamera;
    private Date date;
    private String str = "";
    private ImageButton mBackButton;
    private ImageView mPic;
    private Button mPostButton;
    private Button mDeleButton;
    private EditText mEditTitle;
    private EditText mEditDetail;
    private SwitchCompat mSwitch;
    public static String EXTRA_EVENT = "EXTRA_EVENT";
    public static String TYPE = "type";
    public static int TYPE_NEW = 0; //新建
    public static int TYPE_CHANGE = 1; //修改
    public static int TYPE_VIEW = 2; //查看
    private  int currentType;
    private Uri picUri = null;
    private String id;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eidt);
        initView();
    }

    private void initView() {
        currentType = getIntent().getIntExtra(TYPE,0);
        imageView = findViewById(R.id.gallery_button);
        mBackButton = findViewById(R.id.activity_edit_back);
        mPostButton = findViewById(R.id.activity_edit_post);
        mSwitch = findViewById(R.id.activity_edit_isopen_btn);
        mBackButton = findViewById(R.id.activity_edit_back);
        mPostButton = findViewById(R.id.activity_edit_post);
        mDeleButton = findViewById(R.id.activity_edit_dele);
        mEditTitle = findViewById(R.id.activity_edit_edittitle);
        mEditDetail = findViewById(R.id.activity_edit_editdetail);
        mPic = findViewById(R.id.edit_pic);

        mPic.setVisibility(View.GONE);

        if (currentType == TYPE_CHANGE || currentType == TYPE_VIEW){
            id = getIntent().getStringExtra("id");
            Intent intent = getIntent();
            Event event = new Event(intent.getStringExtra("id"),
                                    intent.getStringExtra("title"),
                                    intent.getStringExtra("detail"),
                                    new Date(intent.getLongExtra("create",System.currentTimeMillis())),
                                    intent.getBooleanExtra("isopen",false),
                                    (AVUser) intent.getParcelableExtra("owner"));


            mEditTitle.setText(event.getTitle());
            mEditDetail.setText(event.getDetail());
            mSwitch.setChecked(event.isOpen());
            mPostButton.setText("修改");

            EventLab.getPic(intent.getStringExtra("id"),handlerPic);
        }

        if (currentType == TYPE_NEW){
            mDeleButton.setVisibility(View.GONE);
        }else if (currentType == TYPE_VIEW){
            mPostButton.setVisibility(View.GONE);
            mDeleButton.setVisibility(View.GONE);
            mPic.setClickable(false);
            imageView.setClickable(false);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        imageView.setOnClickListener(this);
        mPic.setOnClickListener(this);


        mBackButton.setOnClickListener(this);
        mPostButton.setOnClickListener(this);
        mDeleButton.setOnClickListener(this);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //第一层switch
        switch (requestCode) {
            case PHOTO_FROM_GALLERY:
                //第二层switch
                switch (resultCode) {
                    case RESULT_OK:
                        if (Build.VERSION.SDK_INT >= 19) {
                            path = OpenAlbumUtil.handleImageOnKitKat(this,data);
                        } else {
                            path = OpenAlbumUtil.handleImageBeforeKitKat(this,data);
                        }
          //              mPic.setImageURI(data.getData());
                        Log.d(TAG, "path: " + path);
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
                            //todo 添加图片
                            Uri uri = data.getData();
                            Log.d(TAG, "onActivityResult: " + uri.toString());
                            picUri = uri;
                            mPic.setImageURI(uri);
                            mPic.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                        }
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
            case PHOTO_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    SharedPreferences  sp = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
                    String user = sp.getString(("uri"), "");
                    Uri uri = Uri.parse(user);
                    updateDCIM(uri);
                    try {
                        //把URI转换为Bitmap，并将bitmap压缩，防止OOM(out of memory)
                        Bitmap bitmap = ImageTools.getBitmapFromUri(uri, this);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    removeCache("uri");
                } else {
                    Log.e("result", "is not ok" + resultCode);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置相片存放路径，先将照片存放到SD卡中，再操作
     *
     * @return
     */
    private File createImageStoragePath() {
        if (hasSdcard()) {
            appDir = new File("/sdcard/testImage/");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            date = new Date();
            str = simpleDateFormat.format(date);
            String fileName = str + ".jpg";
            File file = new File(appDir, fileName);
            return file;
        } else {
            Log.e("sd", "is not load");
            return null;
        }
    }

    /**
     * 将照片插入系统相册，提醒相册更新
     *
     * @param uri
     */
    private void updateDCIM(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        this.sendBroadcast(intent);

        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
    }

    /**
     * 判断SD卡是否可用
     *
     * @return
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 移除缓存
     *
     * @param cache
     */
    private void removeCache(String cache) {
        SharedPreferences sp = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }


    @Override
    public void onClick(View v) {
        SoftKeyboardUtil.hideSoftKeyboard(this);
        switch (v.getId()){
            case R.id.activity_edit_back:
                finish();
                break;
            case R.id.activity_edit_post:
                if (mEditTitle.getText().length() == 0 || mEditDetail.getText().length() ==0){
                    Toast.makeText(EditActivity.this,"题目和内容不能为空",Toast.LENGTH_SHORT).show();
                    break;
                }
                else if (mEditTitle.getText().length() > 20){
                    Toast.makeText(EditActivity.this,"题目不能超过20个字",Toast.LENGTH_SHORT).show();
                    break;
                }
                String str;
                if (currentType == TYPE_NEW){
                    str = "确定发表";
                }else {
                    str = "确定修改";
                }
                ShowPopUtil.showPop(this,str, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.tv_album:
                                if (currentType == TYPE_NEW){
                                    Event event = new Event(null,mEditTitle.getText().toString(),mEditDetail.getText().toString(),new Date(),mSwitch.isChecked(), AVUser.getCurrentUser());
                                    EventLab.addEvent(event,handler1,path);
                                }else {
                                    Event event = new Event(id,mEditTitle.getText().toString(),mEditDetail.getText().toString(),new Date(),mSwitch.isChecked(),AVUser.getCurrentUser());
                                    EventLab.updateEvent(event,handler1,path);
                                }
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
            case R.id.activity_edit_dele:
                ShowPopUtil.showPop(this,"确定删除", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        switch (view.getId()) {
                            case R.id.tv_album:
                                Event event = new Event(id,mEditTitle.getText().toString(),mEditDetail.getText().toString(),new Date(),mSwitch.isChecked(),AVUser.getCurrentUser());
                                EventLab.deleteEvent(event,handler1);
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
            case R.id.gallery_button:
            case R.id.edit_pic:
                OpenAlbumUtil.openAlbum(EditActivity.this);
                break;

        }
    }

    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                finish();
            }
        }
    };

    Handler handlerPic = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                imageView.setVisibility(View.GONE);
                mPic.setVisibility(View.VISIBLE);
                Log.d(TAG, "url: " + (String)msg.obj);
              //  RequestOptions options = new RequestOptions();
             //   options.error(R.drawable.login_bg);
               Glide.with(EditActivity.this).load(msg.obj).into(mPic);
            }
        }
    };

}
