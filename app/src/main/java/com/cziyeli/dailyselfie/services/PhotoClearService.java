package com.cziyeli.dailyselfie.services;

import android.app.IntentService;
import android.content.Intent;

import com.cziyeli.dailyselfie.models.Selfie;

/**
 * Asynchronously clear all selfies from the database.
 */

public class PhotoClearService extends IntentService {
    public static final String ACTION_RESPONSE = "com.cziyeli.dailyselfie.CLEAR";
    public static final String ACTION_SUCCESS = "CLEAR_SUCCESS";

    public PhotoClearService() {
        super("PhotoClearService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Selfie.deleteAll(Selfie.class);
            broadcastResults(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        broadcastResults(false);
    }

    protected void broadcastResults(boolean success) {
        final Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_RESPONSE);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(ACTION_SUCCESS, success);

        sendBroadcast(intentResponse);
    }
}
