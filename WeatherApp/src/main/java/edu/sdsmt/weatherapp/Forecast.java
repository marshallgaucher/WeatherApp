package edu.sdsmt.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Marshall Gaucher on 11/12/13.
 */
public class Forecast implements Parcelable{

    private static final String TAG = "";


    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    private String _URL = "http://i.wxbug.net/REST/Direct/GetForecastHourly.ashx?zip=" + "%s" +
            "&ht=t&ht=i&ht=cp&ht=fl&ht=h" +
            "&api_key=q3wj56tqghv7ybd8dy6gg4e7";

    // http://developer.weatherbug.com/docs/read/List_of_Icons

    private String _imageURL = "http://img.weather.weatherbug.com/forecast/icons/localized/500x420/en/trans/%s.png";

    public Bitmap Image;
    public String ChancePrecip;
    public String DateTiime;
    public String Desc;
    public String DewPoint;
    public String FeelsLike;
    public String FeelsLikeLabel;
    public String Humidity;
    public String Icon;
    public String SkyCover;
    public String Temperature;
    public String WindDirection;
    public String WindSpeed;

    public Forecast()
    {
        ChancePrecip =null;
        DateTiime = null;
        Desc = null;
        DewPoint = null;
        FeelsLike = null;
        FeelsLikeLabel= null;
        Humidity = null;
        Icon = null;
        SkyCover = null;
        Temperature= null;
        WindDirection = null;
        WindSpeed = null;
        Image = null;
    }

    private Forecast(Parcel parcel)
    {
        Image = parcel.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(Image, 0);
    }

    public static final Parcelable.Creator<Forecast> Creator = new Parcelable.Creator<Forecast>()
    {
        @Override
        public Forecast createFromParcel(Parcel pc)
        {
            return new Forecast(pc);
        }

        @Override
        public Forecast[] newArray(int size)
        {
            return new Forecast[size];
        }
    };

    public class LoadForecast extends AsyncTask<String, Void, Forecast>
    {
        private IListeners _listener;
        private Context _context;

        private int bitmapSampleSize = -1;

        public LoadForecast(Context context, IListeners listener)
        {
            _context = context;
            _listener = listener;
        }

        protected Forecast doInBackground(String... params)
        {
            Forecast forecast = null;


            try
            {
                StringBuilder stringBuilder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(new HttpGet(String.format(_URL, params[0])));
                if( response.getStatusLine().getStatusCode() == 200)
                {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    //Read the JSON
                    String line;
                    while((line = reader.readLine())!=null)
                    {
                        stringBuilder.append(line);
                    }

                    forecast = readJSON(stringBuilder.toString());
                    return forecast;
                }

            }
            catch (IllegalStateException e)
            {
                Log.e(TAG, e.toString() + params[0]);
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

            return forecast;
        }

        protected void onPostExecute(Forecast forecast)
        {
            _listener.onForecastLoaded(forecast);
        }

        private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
        {
            Bitmap iconBitmap = null;
            try
            {
                URL weatherURL = new URL(String.format(_imageURL, conditionString));

                BitmapFactory.Options options = new BitmapFactory.Options();
                if (bitmapSampleSize != -1)
                {
                    options.inSampleSize = bitmapSampleSize;
                }

                iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
            }
            catch (MalformedURLException e)
            {
                Log.e(TAG, e.toString());
            }
            catch (IOException e)
            {
                Log.e(TAG, e.toString());
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

            return iconBitmap;
        }
        public Forecast readJSON(String jsonString)
        {
            Forecast forecast = null;
            try
            {
                JSONObject jToken = new JSONObject(jsonString);
                if(jToken.has("forecastHourlyList")==true)
                {
                    JSONObject forecastInfo = jToken.getJSONObject("forecastHourlyList");

                    forecast.ChancePrecip = forecastInfo.getString("chancePrecip");
                    forecast.DateTiime = forecastInfo.getString("dateTime");
                    forecast.Desc = forecastInfo.getString("desc");
                    forecast.DewPoint = forecastInfo.getString("dewPoint");
                    forecast.FeelsLike = forecastInfo.getString("feelsLike");
                    forecast.FeelsLikeLabel = forecastInfo.getString("feelsLikeLabel");
                    forecast.Humidity = forecastInfo.getString("humidity");
                    forecast.Icon = forecastInfo.getString("icon");
                    forecast.SkyCover = forecastInfo.getString("skyCover");
                    forecast.Temperature = forecastInfo.getString("temperature");
                    forecast.WindDirection = forecastInfo.getString("windDirection");
                    forecast.WindSpeed = forecastInfo.getString("windSpeed");

                }
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }

            return forecast;
        }

    }

}
