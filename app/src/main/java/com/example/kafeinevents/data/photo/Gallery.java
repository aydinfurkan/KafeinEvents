package com.example.kafeinevents.data.photo;

import android.app.Activity;
import android.content.Intent;

public class Gallery {

    private int requestCode;
    private boolean allowMultiple;
    private Activity activity;

    public Gallery(Activity activity, boolean allowMultiple, int requestCode){
        this.activity = activity;
        this.allowMultiple = allowMultiple;
        this.requestCode = requestCode;
    }

    public void open(){

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);

        intent.setType("image/*");

        if(allowMultiple)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        /*String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);*/

        activity.startActivityForResult(intent, requestCode);

    }


}
