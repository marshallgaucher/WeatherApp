package edu.sdsmt.weatherapp;

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
}
