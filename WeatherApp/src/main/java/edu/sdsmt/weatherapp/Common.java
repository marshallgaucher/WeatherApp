package edu.sdsmt.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mcrypt on 11/18/13.
 */
public class Common {
    public static final int TOAST_DURATION = 30;

    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    public static final String LOCATION_URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" +
            "&api_key=k4dpzhefdma958cdw7xue3j2";

    // http://developer.weatherbug.com/docs/read/List_of_Icons
    public static final String IMAGE_URL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";

    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    public static final String FORECAST_URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" +
            "&ht=t&ht=i&ht=cp&ht=fl&ht=h" +
            "&api_key=q3wj56tqghv7ybd8dy6gg4e7";

    // From: http://www.stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
