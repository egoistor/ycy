package com.example.ycy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class OpenAlbumUtil {
    private static final int PHOTO_FROM_GALLERY = 1;
    private static final int PHOTO_FROM_CAMERA = 2;

    public static void openAlbum(Activity context){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        context.startActivityForResult(intent, PHOTO_FROM_GALLERY);
    }
}
