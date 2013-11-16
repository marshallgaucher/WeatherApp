package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends Activity {

    private final static String BUNDLE_KEY = "the_key";
    private final static String FRAGMENT_FORECAST_TAG = "Forecast";
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

            _fragmentForecast = new FragmentForecast();

            _fragmentManager.beginTransaction()
                            .replace(R.id.container, _fragmentForecast, FRAGMENT_FORECAST_TAG)
                            .commit();
        }

        showForecast(TextUtils.split(_cities[0], "\\|")[0]);

    }

    private void showForecast(String zipCode)
    {
        //use bundle to pass arguments to fragment

        //Bundle bundle = new Bundle();
        //bundle.putString("key", "value")
        //ForecastFragment.setArguments(bundle);

        //Hint: FragmentManager().beginTransaction()


    }
}
