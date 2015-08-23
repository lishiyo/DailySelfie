package com.cziyeli.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * Full Selfie view launched by clicking on a thumbnail in our list of selfies.
 */

public class SelfieActivity extends AppCompatActivity {

    public final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private String mCurrentPhotoPath = null;

    ImageView mSelfieFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfie_full);
        mSelfieFull = (ImageView) findViewById(R.id.selfie_full);

        // Check if we already have the current photo path via onSaveInstanceState
        if (getCurrentPhotoPath() != null) {
            setFullImageFromFilePath(getCurrentPhotoPath(), mSelfieFull);
        } else {
            final Bundle extras = getIntent().getExtras();
            if (extras != null) {
                setCurrentPhotoPath(extras.getString(CAPTURED_PHOTO_PATH_KEY));
                setFullImageFromFilePath(getCurrentPhotoPath(), mSelfieFull);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Photo paths and URI
     */

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.mCurrentPhotoPath = currentPhotoPath;
    }

    /**
     * Scale the photo to the full size of the screen.
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the full window display
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int targetH = metrics.heightPixels;
        int targetW = metrics.widthPixels;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        // Rotate the bitmap because the camera is weird
        if (photoW > photoH) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, photoW, photoH, matrix, true);
        }

        imageView.setImageBitmap(bitmap);
    }
}
