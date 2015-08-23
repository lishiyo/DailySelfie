package com.cziyeli.dailyselfie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.cziyeli.dailyselfie.adapters.SelfiesAdapter;
import com.cziyeli.dailyselfie.models.Selfie;
import com.cziyeli.dailyselfie.services.PhotoSaveService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    // requestCode for activity result
    static final int REQUEST_TAKE_PHOTO = 1;

    public ListView mListView;
    PhotoSaveReceiver mSaveReceiver;
    SelfiesAdapter mAdapter;
    ArrayList<Selfie> mSelfiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list);

        // create and set adapter
        getAndSetSelfies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_camera:
                openCamera();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register photo saving receiver;
        mSaveReceiver = new PhotoSaveReceiver();
        IntentFilter intentFilter = new IntentFilter(PhotoSaveService.ACTION_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mSaveReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSaveReceiver != null) {
            unregisterReceiver(mSaveReceiver);
        }
    }

    private void getAndSetSelfies() {
        Log.d("connie", "getandsetselfies called from onCreate!");

        // Convert list of selfies to array list
        mSelfiesList = new ArrayList(Selfie.listAll(Selfie.class));
        // Create the adapter to convert the array to views
        mAdapter = new SelfiesAdapter(this, mSelfiesList);
        // Attach the adapter to the ListView
        mListView.setAdapter(mAdapter);
    }

    /** CAMERA LOGIC **/

    private void openCamera() {
        dispatchTakePictureIntent();
    }

    /**
     * The Android Camera application encodes the photo in the return Intent delivered to onActivityResult() as a small Bitmap in the extras, under the key "data". Retrieve this thumbnail.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if camera operation was successful
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            // launch intent service to save into sqlite database
            startSaveService();
        }
    }

    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        setCurrentPhotoPath("file:" + image.getAbsolutePath());

        return image;
    }

    /**
     * Start the camera by dispatching a camera intent. Camera application saves a full-sized photo if you give it a filname to save to.
     */
    protected void dispatchTakePictureIntent() {
        // Check if there is a camera
        PackageManager packageManager = getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // If camera exists, create intent with a file path
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(this, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }

            // Continue only if the file was successfully created
            if (photoFile.exists()) {
                Uri fileUri = Uri.fromFile(photoFile);

                // Set file's uri and path
                setCapturedImageURI(fileUri); // mCapturedImageURI
                setCurrentPhotoPath(fileUri.getPath());

                Log.d("connie", "dispatching intent - current photo path ++ " + getCurrentPhotoPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCapturedImageURI());

                // to get full image, specify target file to save image
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /** Saving the photo logic **/

    protected void startSaveService() {
        Intent saveIntent = new Intent(MainActivity.this, PhotoSaveService.class);
        saveIntent.putExtra(PhotoSaveService.CAPTURED_PHOTO_PATH_KEY, getCurrentPhotoPath());
        Log.d("connie", "startSaveService with path ++ " + getCurrentPhotoPath());

        startService(saveIntent);
    }

    public class PhotoSaveReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(PhotoSaveService.ACTION_SUCCESS, false)) {
                Log.d("connie", "onReceive photosavereceiver!");
                // add passed-in Selfie to mSelfiesList
                Selfie newSelfie = intent.getExtras().getParcelable(Selfie.SELFIE_PARCEL);
                mSelfiesList.add(newSelfie);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Photo paths and URI
     */

    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    // Required for camera operations in order to save the image file on resume.
    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mCurrentPhotoPath != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath);
        }
        if (mCapturedImageURI != null) {
            savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Getters and setters.
     */

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.mCurrentPhotoPath = currentPhotoPath;
    }

    public Uri getCapturedImageURI() {
        return mCapturedImageURI;
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        this.mCapturedImageURI = capturedImageURI;
    }

    public SelfiesAdapter getAdapter() {
        return mAdapter;
    }
}
