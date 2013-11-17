package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends Activity implements IForecastControlListener{
    private final static String FRAGMENT_FORECAST_TAG = "Forecast";
    private final static String CITIES_KEY = "Cities";
    private String[] _cities;
    private FragmentManager _fragmentManager;
    private FragmentForecast _fragmentForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a reference to the fragment manager to
        // be used for adding/replacing fragments.
        _fragmentManager = getFragmentManager();

        // If the fragment is not found, create it.
        _fragmentForecast = (FragmentForecast) _fragmentManager.findFragmentByTag(FRAGMENT_FORECAST_TAG);
        if (_fragmentForecast == null) {
            _fragmentForecast = new FragmentForecast();
        }
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

    private void showForecast(String zipCode)
    {
        //use bundle to pass arguments to fragment
        Bundle bundle = new Bundle();
        bundle.putString(FragmentForecast.ZIP_CODE_KEY, zipCode);

        _fragmentForecast = new FragmentForecast();
        _fragmentForecast.setArguments(bundle);

        _fragmentManager.beginTransaction()
                        .replace(R.id.container, _fragmentForecast, FRAGMENT_FORECAST_TAG)
                        .commit();
    }

    @Override
    public void getLocation(String zipCode) {
        ForecastLocation.LoadForecastLocation asyncTask = new ForecastLocation.LoadForecastLocation(null, new ForecastWebListeners());
        asyncTask.execute(zipCode);
    }

    @Override
    public void getForecast(String zipCode) {
        Forecast.LoadForecast asyncTask = new Forecast.LoadForecast(null, new ForecastWebListeners());
        asyncTask.execute(zipCode);
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
