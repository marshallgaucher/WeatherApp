package edu.sdsmt.weatherapp;

/**
 * Created by Marshall Gaucher and Dean Laganiere on 11/16/13.
 */
public interface IForecastControlListener {
    public void getLocation(String zipCode);
    public void getForecast(String zipCode);
}
