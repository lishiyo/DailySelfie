package com.cziyeli.dailyselfie.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cziyeli.dailyselfie.R;
import com.cziyeli.dailyselfie.SelfieActivity;
import com.cziyeli.dailyselfie.models.Selfie;

import java.util.ArrayList;

/**
 * Maps an array of Selfie objects to a ListView.
 */

public class SelfiesAdapter extends ArrayAdapter<Selfie> {
    ArrayList<Selfie> mSelfies;
    Context mContext;

    public SelfiesAdapter(Context context, ArrayList<Selfie> selfies) {
        super(context, 0, selfies);
        mContext = context;
        mSelfies = selfies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for the position
        final Selfie selfie = getItem(position);

        // Check if an existing view can be reused, otherwise inflate the view
        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_selfie_item, parent, false);

            viewHolder.mSelfieThumbnail = (ImageView) convertView.findViewById(R.id.selfie_thumbnail);
            viewHolder.mSelfieDescription = (TextView) convertView.findViewById(R.id.selfie_description);
            viewHolder.mSelfieThumbnail.setOnClickListener(mClickSelfieListener);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the Selfie object
        final String currentPhotoPath = selfie.getImagePath();
        viewHolder.setPic(currentPhotoPath, viewHolder.mSelfieThumbnail);
        viewHolder.setDescription(selfie.getDescription());

        // Add the photo path as a tag on the thumbnail to launch activity
        viewHolder.setImagePathOnView(currentPhotoPath);

        return convertView;
    }

    public View.OnClickListener mClickSelfieListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final String path = (String) v.getTag();
            if (!path.isEmpty()) {
                final Intent intent = new Intent(mContext, SelfieActivity.class);
                intent.putExtra(SelfieActivity.CAPTURED_PHOTO_PATH_KEY, path);
                mContext.startActivity(intent);
            }
        }
    };

    // View cache
    public static class ViewHolder {
        ImageView mSelfieThumbnail;
        TextView mSelfieDescription;

        // Hardcoded thumbnail dimensions
        static final Resources r = Resources.getSystem();
        static final int WIDTH = getWidth();
        static final int HEIGHT = getHeight();

        public static int getWidth() {
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics()));
        }

        public static int getHeight() {
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, r.getDisplayMetrics()));
        }

        /**
         * Set a scaled-down version of the photo into the thumbnail.
         *
         * @param currentPhotoPath  fully-qualified path to the thumbnail
         * @param imageView         image view to set the thumbnail to
         */
        public void setPic(String currentPhotoPath, ImageView imageView) {
            // Get hardcoded dimensions
            imageView.getLayoutParams().height = WIDTH;
            imageView.getLayoutParams().width = HEIGHT;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/WIDTH, photoH/HEIGHT);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            imageView.setImageBitmap(bitmap);
        }

        public void setDescription(String description) {
            mSelfieDescription.setText(description);
        }

        public void setImagePathOnView(String imagePath) {
            mSelfieThumbnail.setTag(imagePath);
        }

    }

}
