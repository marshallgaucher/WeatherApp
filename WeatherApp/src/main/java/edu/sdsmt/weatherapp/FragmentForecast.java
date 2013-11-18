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


    private View _rootView;
    private IForecastControlListener _listener;

    private String _zipCode;
    private Forecast _forecast;
    private ForecastLocation _forecastLocation;

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

        outState.putString(ZIP_CODE_KEY, _zipCode);

        if(_forecastLocation != null)
        {
            outState.putParcelable(LOCATION_KEY, _forecastLocation);
        }
        if(_forecast != null)
        {
            outState.putParcelable(FORECAST_KEY, _forecast);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _rootView = inflater.inflate(R.layout.fragment_main, null);

        Bundle argumentBundle = getArguments();

        //call after activity is attached so we don't get a null exception from get activity
        if (!Common.isNetworkAvailable(getActivity())) {
            setNoWifiForecast();
        }

        if(argumentBundle != null)
        {
            _zipCode = argumentBundle.getString(ZIP_CODE_KEY);
            _listener.getLocation(_zipCode);
        }

        if(savedInstanceState != null)
        {
            _zipCode = savedInstanceState.getString(ZIP_CODE_KEY);

            if(savedInstanceState.containsKey(LOCATION_KEY))
            {
                _forecastLocation = savedInstanceState.getParcelable(LOCATION_KEY);
                setForecastLocation(_forecastLocation);

                if(savedInstanceState.containsKey(FORECAST_KEY))
                {
                    _forecast = savedInstanceState.getParcelable(FORECAST_KEY);
                    setForecast(_forecast);
                }
                else
                {
                    _listener.getForecast(_zipCode);
                }
            }
            else
            {
                _listener.getLocation(_zipCode);
            }
        }





        return _rootView;
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
        if (location == null) {
            setNoWifiForecast();
            return;
        }

        _forecastLocation = location;

        TextView locationTextView = (TextView) _rootView.findViewById(R.id.textViewLocation);

        locationTextView.setText(location.City + ", " + location.State);

        _listener.getForecast(location.ZipCode);
    }

    public void setForecast(Forecast forecast)
    {
        if (forecast == null) {
            setNoWifiForecast();
            return;
        }
        _forecast = forecast;

        String formattedDateTime = formatDateTime(forecast.DateTime);

        TextView textViewTemp = (TextView) _rootView.findViewById(R.id.textViewTemp);
        TextView textViewFeelsLikeTemp = (TextView) _rootView.findViewById(R.id.textViewFeelsLikeTemp);
        TextView textViewHumidity = (TextView) _rootView.findViewById(R.id.textViewHumidity);
        TextView textViewChanceOfPrecipitation = (TextView) _rootView.findViewById(R.id.textViewChanceOfPrecip);
        TextView textViewAsOfTime = (TextView) _rootView.findViewById(R.id.textViewAsOfTime);
        ImageView imageViewImageForecast = (ImageView) _rootView.findViewById(R.id.imageForecast);

        RelativeLayout progressLayout = (RelativeLayout) _rootView.findViewById(R.id.layoutProgress);

        textViewTemp.setText(forecast.Temperature + "F");
        textViewFeelsLikeTemp.setText(forecast.FeelsLike +"F");
        textViewHumidity.setText(forecast.Humidity +"%");
        textViewChanceOfPrecipitation.setText(forecast.ChancePrecipitation + "%");
        textViewAsOfTime.setText(formattedDateTime);
        imageViewImageForecast.setImageBitmap(forecast.Image);

        progressLayout.setVisibility(View.INVISIBLE);
    }

    public void setNoWifiForecast()
    {
        TextView textViewLocation = (TextView) _rootView.findViewById(R.id.textViewLocation);
        TextView textViewTemp = (TextView) _rootView.findViewById(R.id.textViewTemp);
        TextView textViewFeelsLikeTemp = (TextView) _rootView.findViewById(R.id.textViewFeelsLikeTemp);
        TextView textViewHumidity = (TextView) _rootView.findViewById(R.id.textViewHumidity);
        TextView textViewChanceOfPrecipitation = (TextView) _rootView.findViewById(R.id.textViewChanceOfPrecip);
        TextView textViewAsOfTime = (TextView) _rootView.findViewById(R.id.textViewAsOfTime);
        ImageView imageViewImageForecast = (ImageView) _rootView.findViewById(R.id.imageForecast);

        textViewLocation.setText(R.string.unavailable);
        textViewTemp.setText(R.string.unavailable);
        textViewFeelsLikeTemp.setText(R.string.unavailable);
        textViewHumidity.setText(R.string.unavailable);
        textViewChanceOfPrecipitation.setText(R.string.unavailable);
        textViewAsOfTime.setText(R.string.unavailable);
        imageViewImageForecast.setImageResource(R.drawable.mrt);

        RelativeLayout progressLayout = (RelativeLayout) _rootView.findViewById(R.id.layoutProgress);
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
