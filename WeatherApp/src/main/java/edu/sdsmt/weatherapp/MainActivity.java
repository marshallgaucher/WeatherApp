package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends Activity {

    private final static String BUNDLE_KEY = "the_key";
    private String[] _cities;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null)
        {
            //get the city array from resources
            _cities = getResources().getStringArray(R.array.cityArray);
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
