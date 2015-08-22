package com.cziyeli.dailyselfie;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Fragment showing list of selfies.
 */
public class SelfiesMainFragment extends Fragment {
    public ListView mListView;

    public static SelfiesMainFragment newInstance() {
        return new SelfiesMainFragment();
    }

    public SelfiesMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public void setThumbnail(Bitmap imageBitmap) {
//        mSelfieThumbnail.setImageBitmap(imageBitmap);
    }
}
