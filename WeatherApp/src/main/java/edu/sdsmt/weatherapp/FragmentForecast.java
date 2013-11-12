package edu.sdsmt.weatherapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Marshall Gaucher on 11/12/13.
 */
public class FragmentForecast extends Fragment {

    public static final String LOCATION_KEY = "key_location";
    public static final String FORECAST_KEY = "key_forecast";

    @Override
    public void onCreate(Bundle argumentBundle)
    {
        super.onCreate(argumentBundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main,null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
