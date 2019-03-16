package com.example.ycy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.ycy.R;
import com.example.ycy.utils.ImageTools;
import com.example.ycy.utils.OpenAlbumUtil;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends BaseActivity {

    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;
    private ImageView imageView;
    private File appDir;
    private Uri uriForCamera;
    private Date date;
    private String str = "";
    private ImageButton mBackButton;
    private Button mPostButton;
    private SwitchCompat mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eidt);

        initView();
    }

    public void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        imageView = findViewById(R.id.gallery_button);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenAlbumUtil.openAlbum(EditActivity.this);
            }
        });
        mBackButton = findViewById(R.id.activity_edit_back);
        mPostButton = findViewById(R.id.activity_edit_post);
        mSwitch = findViewById(R.id.activity_edit_isopen_btn);
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
                        if (data != null) {
                            Uri uri = data.getData();
                            imageView.setImageURI(uri);
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

}
