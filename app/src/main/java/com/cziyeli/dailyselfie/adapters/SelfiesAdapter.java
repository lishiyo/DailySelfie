package com.cziyeli.dailyselfie.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cziyeli.dailyselfie.R;

/**
 * Created by connieli on 8/19/15.
 */
public class SelfiesAdapter extends CursorAdapter {

    public SelfiesAdapter(Context context, Cursor c, int flags) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_selfie_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.mSelfieThumbnail = (ImageView) view.findViewById(R.id.selfie_thumbnail);
            holder.mSelfieDescription = (TextView) view.findViewById(R.id.selfie_description);
            view.setTag(holder);
        }

        // Extract properties from cursor
//        String podcastName = cursor.getString(cursor.getColumnIndexOrThrow("podcast_name"));
//        String producerName = cursor.getString(cursor.getColumnIndexOrThrow("producer_name"));
//        long podcastId = cursor.getLong(cursor.getColumnIndexOrThrow("podcast_id")); // AA id

        // Populate fields with extracted properties
        holder.mPodcastName.setText(podcastName);
        holder.mProducerName.setText(producerName);
        holder.mListenBtn.setTag(podcastId);
    }

    public static class ViewHolder {
        ImageView mSelfieThumbnail;
        TextView mSelfieDescription;
    }
}
