package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by Marshall Gaucher and Dean Laganiere on 11/12/13.
 *
 * MainActivity that implements IForecastControlListener to call async Tasks
 * to retrieve location and weather information from API.
 */
public class MainActivity extends Activity implements IForecastControlListener
{
    private final static String FRAGMENT_FORECAST_TAG = "ForecastFragment";
    private final static String CITIES_KEY = "Cities";

    private String[] _cities;
    private FragmentManager _fragmentManager;
    private FragmentForecast _fragmentForecast;

    private Forecast.LoadForecast _forecastAsyncTask;
    private ForecastLocation.LoadForecastLocation _locationAsyncTask;

    private Boolean _isNetworkConnected;


    /**
     * Set the main activity view and check for internet connection.
     * If we have the internet connection we allow our async tasks to call
     * the API URLS to retrieve our JSON objects.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check if the device has a network connection
        _isNetworkConnected = Common.isNetworkAvailable(this);

        // Get a reference to the fragment manager to
        // be used for adding/replacing fragments.
        _fragmentManager = getFragmentManager();

        if(savedInstanceState == null)
        {
            //get the city array from resources
            _cities = getResources().getStringArray(R.array.cityArray);
        }
        else
        {
            //get the city array from bundle
            _cities = savedInstanceState.getStringArray(CITIES_KEY);

        }

        if (_cities != null && _cities.length > 0) {
            showForecast(TextUtils.split(_cities[0], "\\|")[0]);
        }

    }

    /**
     * Get a given city from the bundle.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        _cities = savedInstanceState.getStringArray(CITIES_KEY);
    }

    /**
     * Preserve the city array inside the bundle.
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArray(CITIES_KEY, _cities);
}

    /**
     * Cancel our async tasks on rotate state so we dont crash the app.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(_locationAsyncTask != null)
        {
            _locationAsyncTask.cancel(true);
        }

        if(_forecastAsyncTask != null)
        {
            _forecastAsyncTask.cancel(true);
        }
    }

    /**
     * Show the given foreast from a zip code.
     *
     * @param zipCode
     */
    private void showForecast(String zipCode)
    {
        //use bundle to pass arguments to fragment
        Bundle bundle = new Bundle();
        bundle.putString(FragmentForecast.ZIP_CODE_KEY, zipCode);

        // If the fragment is not found, create it.
        _fragmentForecast = (FragmentForecast) _fragmentManager.findFragmentByTag(FRAGMENT_FORECAST_TAG);

        if(_fragmentForecast == null)
        {
            _fragmentForecast = new FragmentForecast();
            _fragmentForecast.setArguments(bundle);

            _fragmentManager.beginTransaction()
                    .add(R.id.container, _fragmentForecast, FRAGMENT_FORECAST_TAG)
                    .commit();
        }
    }

    /**
     * Call the async ask to get the forecast location based on its zip code.
     *
     * @param zipCode
     */
    @Override
    public void getLocation(String zipCode) {

        Context context = getApplicationContext();

        if (_isNetworkConnected) {
            _locationAsyncTask = new ForecastLocation.LoadForecastLocation(context, new ForecastWebListeners());
            _locationAsyncTask.execute(zipCode);
        } else {
            Toast.makeText(context, R.string.toastNoWifiLocation, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Call the async task to get the forecast information based on the zip code.
     *
     * @param zipCode
     */
    @Override
    public void getForecast(String zipCode) {
        Context context = getApplicationContext();

        if(_isNetworkConnected)
        {
            _forecastAsyncTask = new Forecast.LoadForecast(context, new ForecastWebListeners());
            _forecastAsyncTask.execute(zipCode);
        }
        else
        {
            Toast.makeText(context, R.string.toastNoWifiWeather, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Listen for the Location and Forecast callbacks and the set the corresponding
     * fragement information for Location and Forecast.
     *
     */
    private class ForecastWebListeners implements IListeners
    {
        @Override
        public void onLocationLoaded(ForecastLocation forecastLocation) {
            _fragmentForecast.setForecastLocation(forecastLocation);
        }

        @Override
        public void onForecastLoaded(Forecast forecast) {
            _fragmentForecast.setForecast(forecast);
        }
    }
}
