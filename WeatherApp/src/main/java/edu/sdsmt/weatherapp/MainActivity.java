package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

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
    private static final int TOAST_DURATION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _isNetworkConnected = false;

        //check if the device has a network connection
        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (wifi.isConnected())
        {
           _isNetworkConnected = true;
        }

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

        showForecast(TextUtils.split(_cities[0], "\\|")[0]);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        _cities = savedInstanceState.getStringArray(CITIES_KEY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArray(CITIES_KEY, _cities);
}

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

    @Override
    public void getLocation(String zipCode) {

       if(_isNetworkConnected)
       {
            _locationAsyncTask = new ForecastLocation.LoadForecastLocation(null, new ForecastWebListeners());
            _locationAsyncTask.execute(zipCode);
       }
       else
       {
           Context context = getApplicationContext();
           Toast.makeText(context, R.string.toastNoWifi, TOAST_DURATION ).show();
       }
    }

    @Override
    public void getForecast(String zipCode) {

        if(_isNetworkConnected)
        {
            _forecastAsyncTask = new Forecast.LoadForecast(null, new ForecastWebListeners());
            _forecastAsyncTask.execute(zipCode);
        }
    }

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
