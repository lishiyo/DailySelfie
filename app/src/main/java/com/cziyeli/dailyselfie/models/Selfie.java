package com.cziyeli.dailyselfie.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Selfie columns - path to the image file, description, fixed timestamp
 */

public class Selfie extends SugarRecord<Selfie> implements Parcelable {
    public String imagePath;
    public String description;
    public long timestamp;
    public static final String SELFIE_PARCEL = "com.cziyeli.dailyselfie.models.Selfie";

    // no-argument constructor required for SugarORM
    public Selfie() {
    }

    public Selfie(String imagePath, String description) {
        this.imagePath = imagePath; // imagePath becomes image_path
        this.description = description;

        // always store
        java.util.Date today = new java.util.Date();
        this.timestamp = today.getTime();
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public static ArrayList<Selfie> getAllSelfies() {
        return new ArrayList(Selfie.listAll(Selfie.class));
    }

    /** Parcelable methods **/

    public Selfie(Parcel in) {
        this.imagePath = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order.
     *
     * @param dest parcel
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imagePath);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Selfie> CREATOR = new Parcelable.Creator<Selfie>() {
        public Selfie createFromParcel(Parcel in) {
            return new Selfie(in);
        }

        public Selfie[] newArray(int size) {
            return new Selfie[size];
        }
    };
}
