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
    static final int REQUEST_TAKE_PHOTO = 1;
    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;

    public ListView mListView;
    PhotoSaveReceiver mSaveReceiver;
    SelfiesAdapter mAdapter;
    ArrayList<Selfie> mSelfiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.list);

        getAndSetSelfies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_camera:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register photo saving receiver
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
        // Convert list of selfies to array list
        mSelfiesList = new ArrayList(Selfie.listAll(Selfie.class));
        // Create the adapter to convert the array to views
        mAdapter = new SelfiesAdapter(this, mSelfiesList);
        // Attach the adapter to the ListView
        mListView.setAdapter(mAdapter);
    }

    /** CAMERA LOGIC **/

    /**
     * Called after Camera app takes the photo. If successful, launch the intent service,
     * which will save the path and description (i.e. timestamp) into a sqlite database.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
     * Start the camera by dispatching a camera intent.
     * Camera application saves a full-sized photo if you give it a filename to save to.
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
                Toast toast = Toast.makeText(this, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }

            // Continue only if the file was successfully created
            if (photoFile.exists()) {
                Uri fileUri = Uri.fromFile(photoFile);

                // Set file's uri and path
                setCapturedImageURI(fileUri); // mCapturedImageURI
                setCurrentPhotoPath(fileUri.getPath());

                // To save full image, specify target file to save image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCapturedImageURI());

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /** Start the intent service to async save the photo into our sqlite database. **/

    protected void startSaveService() {
        final Intent saveIntent = new Intent(MainActivity.this, PhotoSaveService.class);
        saveIntent.putExtra(PhotoSaveService.CAPTURED_PHOTO_PATH_KEY, getCurrentPhotoPath());

        startService(saveIntent);
    }

    public class PhotoSaveReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(PhotoSaveService.ACTION_SUCCESS, false)) {

                // Add just-saved selfie from the intent service to mSelfiesList
                final Selfie newSelfie = intent.getExtras().getParcelable(Selfie.SELFIE_PARCEL);
                mSelfiesList.add(newSelfie);

                getAdapter().notifyDataSetChanged();
            }
        }
    }

    /** Getters and setters **/

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
