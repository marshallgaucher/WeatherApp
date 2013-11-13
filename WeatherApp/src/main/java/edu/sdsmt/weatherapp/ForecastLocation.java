package edu.sdsmt.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Marshall Gaucher on 11/12/13.
 */
public class ForecastLocation {

    private static final String TAG = "";

    // http://developer.weatherbug.com/docs/read/WeatherBug_API_JSON
    // NOTE:  See example JSON in doc folder.
    private String _URL = "http://i.wxbug.net/REST/Direct/GetLocation.ashx?zip=" + "%s" +
            "&api_key=k4dpzhefdma958cdw7xue3j2";


    public ForecastLocation()
    {
        ZipCode = null;
        City = null;
        State = null;
        Country = null;
    }

    public String ZipCode;
    public String City;
    public String State;
    public String Country;

    public class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
    {
        private IListeners _listener;
        private Context _context;



        public LoadForecastLocation(Context context, IListeners listener)
        {
            _context = context;
            _listener = listener;
        }

        protected ForecastLocation doInBackground(String... params)
        {
            ForecastLocation forecastLoc = null;

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

                    forecastLoc = readJSON(stringBuilder.toString());
                    return forecastLoc;
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

            return null;
        }

        protected void onPostExecute(ForecastLocation forecastLoc)
        {
            _listener.onLocationLoaded(forecastLoc);
        }

        public ForecastLocation readJSON(String jsonString)
        {
            ForecastLocation forecastLocation = null;
            try
            {
                JSONObject jToken = new JSONObject(jsonString);
                if(jToken.has("location")==true)
                {
                    JSONObject location = jToken.getJSONObject("location");

                    forecastLocation.City = location.getString("city");
                    forecastLocation.State = location.getString("state");
                    forecastLocation.Country = location.getString("country");
                    forecastLocation.ZipCode = location.getString("zipCode");

                }
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }

           return forecastLocation;
        }



    }


}
