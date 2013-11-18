package edu.sdsmt.weatherapp;

import android.content.Context;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Marshall Gaucher on 11/12/13.
 */
public class ForecastLocation implements Parcelable {

    private static final String TAG = "WeatherApp";

    public String ZipCode;
    public String City;
    public String State;
    public String Country;

    public ForecastLocation()
    {
        ZipCode = null;
        City = null;
        State = null;
        Country = null;
    }

    public ForecastLocation(Parcel parcel)
    {
        ZipCode = parcel.readString();
        City = parcel.readString();
        State = parcel.readString();
        Country = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ZipCode);
        parcel.writeString(City);
        parcel.writeString(State);
        parcel.writeString(Country);
    }

    public static class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
    {
        private IListeners _listener;
        private Context _context;
        private String _errorMessage = "";

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

                HttpResponse response = client.execute(new HttpGet(String.format(Common.LOCATION_URL, params[0])));
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
                _errorMessage = e.toString() + params[0];
                Log.e(TAG, e.toString() + params[0]);
                return null;

            }
            catch (Exception e)
            {
                _errorMessage = e.toString();
                Log.e(TAG, e.toString());
                return null;
            }

            return null;
        }

        protected void onPostExecute(ForecastLocation forecastLoc)
        {
            if (!_errorMessage.equalsIgnoreCase("")) {
                Toast.makeText(_context, _errorMessage, Toast.LENGTH_SHORT).show();
            }

            _listener.onLocationLoaded(forecastLoc);
        }

        public ForecastLocation readJSON(String jsonString)
        {
            ForecastLocation forecastLocation = new ForecastLocation();
            try
            {
                JSONObject jToken = new JSONObject(jsonString);
                if(jToken.has("location"))
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
                _errorMessage = e.toString();
                Log.e(TAG, e.toString());
                return null;
            }

           return forecastLocation;
        }



    }


}
