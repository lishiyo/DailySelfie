package com.cziyeli.dailyselfie.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cziyeli.dailyselfie.R;
import com.cziyeli.dailyselfie.models.Selfie;

import java.util.ArrayList;

/**
 * Takes array of Selfies
 */


public class SelfiesAdapter extends ArrayAdapter<Selfie> {
    ArrayList<Selfie> mSelfies;

    public SelfiesAdapter(Context context, ArrayList<Selfie> selfies) {
        super(context, 0, selfies);
        mSelfies = selfies;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for the position
        Selfie selfie = getItem(position);

        // Check if an existing view can be reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

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

        // Populate the data into the template view using the data object
        String currentPhotoPath = selfie.getImagePath();
        viewHolder.setPic(currentPhotoPath);
        viewHolder.setDescription(selfie.getDescription());

        return convertView; // view with pic and description set
    }

    public View.OnClickListener mClickSelfieListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("connie", "hit selfie!");

        }
    };

    // View cache
    public static class ViewHolder {
        ImageView mSelfieThumbnail;
        TextView mSelfieDescription;
        static final int WIDTH = 100;
        static final int HEIGHT = 50;

        /** given the path, set the pic in the thumbnail **/
        public void setPic(String currentPhotoPath) {
            // Get the dimensions of the View
//            int targetW = mSelfieThumbnail.getWidth();
//            int targetH = mSelfieThumbnail.getHeight();

            int targetW = WIDTH;
            int targetH = HEIGHT;

            if (targetW == 0 || targetH == 0) {
                Log.d("connie", "width or height are 0!!");

                mSelfieThumbnail.getLayoutParams().height = WIDTH;
                mSelfieThumbnail.getLayoutParams().width = HEIGHT;
            }

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            mSelfieThumbnail.setImageBitmap(bitmap);
        }

        public void setDescription(String description) {
            mSelfieDescription.setText(description);
        }
    }

}
