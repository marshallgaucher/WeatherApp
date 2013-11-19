package edu.sdsmt.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

/**
 * Created by Marshall Gaucher and Dean Laganiere on 11/12/13.
 *
 * Forecast parcelable to hold the JSON weather fields and image.
 */
public class Forecast implements Parcelable{
    public Bitmap Image;
    public String ChancePrecipitation;
    public String DateTime;
    public String Description;
    public String DewPoint;
    public String FeelsLike;
    public String FeelsLikeLabel;
    public String Humidity;
    public String Icon;
    public String SkyCover;
    public String Temperature;
    public String WindDirection;
    public String WindSpeed;

    /**
     *  Reads Forecast information and image from parcel.
     *
     */
    public Forecast()
    {
        ChancePrecipitation =null;
        DateTime = null;
        Description = null;
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

    /**
     *  Reads Forecast information and image from parcel.
     *
     * @param parcel
     */
    private Forecast(Parcel parcel)
    {
        Image = parcel.readParcelable(Bitmap.class.getClassLoader());
        ChancePrecipitation = parcel.readString();
        DateTime = parcel.readString();
        Description = parcel.readString();
        DewPoint = parcel.readString();
        FeelsLike = parcel.readString();
        FeelsLikeLabel= parcel.readString();
        Humidity = parcel.readString();
        Icon = parcel.readString();
        SkyCover = parcel.readString();
        Temperature= parcel.readString();
        WindDirection = parcel.readString();
        WindSpeed = parcel.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     *  Writes forecast information to destination parcel.
     *
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(Image, 0);
        dest.writeString(ChancePrecipitation);
        dest.writeString(DateTime);
        dest.writeString(Description);
        dest.writeString(DewPoint);
        dest.writeString(FeelsLike);
        dest.writeString(FeelsLikeLabel);
        dest.writeString(Humidity);
        dest.writeString(Icon);
        dest.writeString(SkyCover);
        dest.writeString(Temperature);
        dest.writeString(WindDirection);
        dest.writeString(WindSpeed);
    }

    /**
     * Marshaler for Forecast parcelable.
     */
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

    /**
     * Async task to make API call for loading a forecast
     */
    public static class LoadForecast extends AsyncTask<String, Void, Forecast>
    {
        private IListeners _listener;
        private Context _context;
        private String _errorMessage = "";

        private int bitmapSampleSize = -1;

        /**
         * Set the context and listener interface
         *
         * @param context
         * @param listener
         */
        public LoadForecast(Context context, IListeners listener)
        {
            _context = context;
            _listener = listener;
        }

        /**
         * Connects to the Forecast URL and parse the JSON Weather object
         *
         * @param params
         * @return
         */
        protected Forecast doInBackground(String... params)
        {
            Forecast forecast = null;

            try
            {
                StringBuilder stringBuilder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(new HttpGet(String.format(Common.FORECAST_URL, params[0])));
                if( response.getStatusLine().getStatusCode() == 200)
                {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    //Read the JSON
                    String line;
                    while((line = reader.readLine()) != null)
                    {
                        stringBuilder.append(line);
                    }

                    forecast = readJSON(stringBuilder.toString());
                    forecast.Image = readIconBitmap(forecast.Icon, bitmapSampleSize);
                    return forecast;
                }

            }
            catch (IllegalStateException e)
            {
                _errorMessage = e.toString() + params[0];
                Log.e(Common.TAG, e.toString() + params[0]);
                return null;
            }
            catch (Exception e)
            {
                _errorMessage = e.toString();
                Log.e(Common.TAG, e.toString());
                return null;
            }

            return forecast;
        }

        /**
         * Inform the listener interface that a weather forecast has been loaded.
         *
         * @param forecast
         */
        protected void onPostExecute(Forecast forecast)
        {
            if (!_errorMessage.equalsIgnoreCase("")) {
                Toast.makeText(_context, _errorMessage, Toast.LENGTH_SHORT).show();
            }

            _listener.onForecastLoaded(forecast);
        }

        /**
         * Read the icon bitmap into a stream from the image url.
         *
         * @param conditionString
         * @param bitmapSampleSize
         * @return
         */
        private Bitmap readIconBitmap(String conditionString, int bitmapSampleSize)
        {
            Bitmap iconBitmap = null;
            try
            {
                URL weatherURL = new URL(String.format(Common.IMAGE_URL, conditionString));

                BitmapFactory.Options options = new BitmapFactory.Options();
                if (bitmapSampleSize != -1)
                {
                    options.inSampleSize = bitmapSampleSize;
                }

                iconBitmap = BitmapFactory.decodeStream(weatherURL.openStream(), null, options);
            }
            catch (MalformedURLException e)
            {
                _errorMessage = e.toString();
                Log.e(Common.TAG, e.toString());
                return null;
            }
            catch (IOException e)
            {
                _errorMessage = e.toString();
                Log.e(Common.TAG, e.toString());
                return null;
            }
            catch (Exception e)
            {
                _errorMessage = e.toString();
                Log.e(Common.TAG, e.toString());
                return null;
            }

            return iconBitmap;
        }

        /**
         * Reads JSON object passed from the object reader to parse out hourly forecast weather fields.
         *
         * @param jsonString
         * @return
         */
        public Forecast readJSON(String jsonString)
        {
            List<Forecast> forecastList = new Vector<Forecast>();
            try
            {
                JSONObject jToken = new JSONObject(jsonString);
                if(jToken.has("forecastHourlyList"))
                {

                    JSONArray forecastInfoList = jToken.getJSONArray("forecastHourlyList");
                    for(int i = 0; i < forecastInfoList.length(); i++)
                    {
                        JSONObject forecastInfo = forecastInfoList.getJSONObject(i);
                        Forecast forecast = new Forecast();

                        forecast.ChancePrecipitation = forecastInfo.getString("chancePrecip");
                        forecast.DateTime = forecastInfo.getString("dateTime");
                        forecast.Description = forecastInfo.getString("desc");
                        forecast.DewPoint = forecastInfo.getString("dewPoint");
                        forecast.FeelsLike = forecastInfo.getString("feelsLike");
                        forecast.FeelsLikeLabel = forecastInfo.getString("feelsLikeLabel");
                        forecast.Humidity = forecastInfo.getString("humidity");
                        forecast.Icon = forecastInfo.getString("icon");
                        forecast.SkyCover = forecastInfo.getString("skyCover");
                        forecast.Temperature = forecastInfo.getString("temperature");
                        forecast.WindDirection = forecastInfo.getString("windDir");
                        forecast.WindSpeed = forecastInfo.getString("windSpeed");

                        forecastList.add(forecast);
                    }
                }
            }
            catch (JSONException e)
            {
                _errorMessage = e.toString();
                Log.e(Common.TAG, e.toString());
                return null;
            }

            return forecastList.get(0);
        }
    }
}
