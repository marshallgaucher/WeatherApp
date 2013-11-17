package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Marshall Gaucher on 11/12/13.
 */
public class FragmentForecast extends Fragment {

    public static final String ZIP_CODE_KEY = "key_zip_code";
    public static final String LOCATION_KEY = "key_location";
    public static final String FORECAST_KEY = "key_forecast";

    private IForecastControlListener _listener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        _listener = (IForecastControlListener) activity;
    }

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
        View rootView = inflater.inflate(R.layout.fragment_main, null);

        Bundle argumentBundle = getArguments();

        if(argumentBundle != null)
        {
            String zipCode = argumentBundle.getString(ZIP_CODE_KEY);
            _listener.getLocation(zipCode);
        }

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setForecastLocation(ForecastLocation location)
    {
        View rootView = getView();
        TextView _locationTextView = (TextView) rootView.findViewById(R.id.textViewLocation);

        _locationTextView.setText(location.City + ", " + location.State);
    }
}
