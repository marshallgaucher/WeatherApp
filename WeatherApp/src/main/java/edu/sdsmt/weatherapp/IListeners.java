package edu.sdsmt.weatherapp;

/**
 * Created by Marshall Gaucher and Dean Laganiere on 11/12/13.
 *
 * Listens for the async tasks (location and forecast)
 */
public interface IListeners {
    public void onLocationLoaded(ForecastLocation forecastLocation);
    public void onForecastLoaded(Forecast forecast);
}
