package com.droid.resto.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.droid.resto.R;

/**
 * Created by a9jr5626 on 4/27/2014.
 */
public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //TODO: Add login/logout behaviour here (i.e if logged in show welcome instead of home)
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }
}