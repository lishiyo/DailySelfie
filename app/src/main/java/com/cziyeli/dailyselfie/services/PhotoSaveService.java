package com.cziyeli.dailyselfie.services;

import android.app.IntentService;
import android.content.Intent;

import com.cziyeli.dailyselfie.models.Selfie;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Asynchronously save the selfie to our sqlite database.
 * Columns - path to the image file, and description (here, just the timestamp).
 * Broadcast the newly created selfie to the main activity as a parcelable.
 *
 */

public class PhotoSaveService extends IntentService {
    public static final String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    public static final String ACTION_RESPONSE = "com.cziyeli.dailyselfie.SAVE";
    public static final String ACTION_SUCCESS = "SAVE_SUCCESS";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public PhotoSaveService() {
        super("PhotoSaveService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String currentPath = intent.getStringExtra(CAPTURED_PHOTO_PATH_KEY);

        try {
            final String description = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            final Selfie selfie = new Selfie(currentPath, description);
            selfie.save();

            broadcastResults(true, selfie);
        } catch (Exception e) {
            e.printStackTrace();
        }

        broadcastResults(false, null);
    }

    protected void broadcastResults(boolean success, Selfie selfie) {
        final Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_RESPONSE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(ACTION_SUCCESS, success);

        // Send the newly-created selfie object to the activity.
        intentResponse.putExtra(Selfie.SELFIE_PARCEL, selfie);

        sendBroadcast(intentResponse);
    }



}
