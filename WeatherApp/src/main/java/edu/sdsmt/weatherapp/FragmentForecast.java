package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
        TextView locationTextView = (TextView) rootView.findViewById(R.id.textViewLocation);

        locationTextView.setText(location.City + ", " + location.State);

        _listener.getForecast(location.ZipCode);
    }

    public void setForecast(Forecast forecast)
    {
        String formattedDateTime = formatDateTime(forecast.DateTime);
        View rootView = getView();

        TextView textViewTemp = (TextView) rootView.findViewById(R.id.textViewTemp);
        TextView textViewFeelsLikeTemp = (TextView) rootView.findViewById(R.id.textViewFeelsLikeTemp);
        TextView textViewHumidity = (TextView) rootView.findViewById(R.id.textViewHumidity);
        TextView textViewChanceOfPrecipitation = (TextView) rootView.findViewById(R.id.textViewChanceOfPrecip);
        TextView textViewAsOfTime = (TextView) rootView.findViewById(R.id.textViewAsOfTime);
        ImageView imageViewImageForecast = (ImageView) rootView.findViewById(R.id.imageForecast);

        RelativeLayout progressLayout = (RelativeLayout) rootView.findViewById(R.id.layoutProgress);

        textViewTemp.setText(forecast.Temperature);
        textViewFeelsLikeTemp.setText(forecast.FeelsLike);
        textViewHumidity.setText(forecast.Humidity);
        textViewChanceOfPrecipitation.setText(forecast.ChancePrecipitation);
        textViewAsOfTime.setText(formattedDateTime);
        imageViewImageForecast.setImageBitmap(forecast.Image);

        progressLayout.setVisibility(View.INVISIBLE);
    }

    public String formatDateTime(String timestamp)
    {
        Date date = new Date(Long.valueOf(timestamp));
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("gmt"));
        return dateFormat.format(date);
    }
}
