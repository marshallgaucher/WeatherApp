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
 * Created by Marshall Gaucher and Dean Laganiere on 11/12/13.
 */
public class ForecastLocation implements Parcelable {
    public String ZipCode;
    public String City;
    public String State;
    public String Country;

    /**
     * ForecastLocation constructor
     */
    public ForecastLocation()
    {
        ZipCode = null;
        City = null;
        State = null;
        Country = null;
    }

    /**
     * Reads the Forecast Location from the parcel
     *
     * @param parcel
     */
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

    /**
     * Writes the Weather Location to parcel object.
     *
     * @param parcel
     * @param i
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ZipCode);
        parcel.writeString(City);
        parcel.writeString(State);
        parcel.writeString(Country);
    }

    /**
     * Async Task to load forecast location.
     */
    public static class LoadForecastLocation extends AsyncTask<String, Void, ForecastLocation>
    {
        private IListeners _listener;
        private Context _context;
        private String _errorMessage = "";

        /**
         * Sets the context and listener interface.
         *
         * @param context
         * @param listener
         */
        public LoadForecastLocation(Context context, IListeners listener)
        {
            _context = context;
            _listener = listener;
        }

        /**
         * Connects to the Forecast Location URL and parse the JSON Weather object
         *
         * @param params
         * @return
         */
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
                Log.e(Common.TAG, e.toString() + params[0]);
                return null;

            }
            catch (Exception e)
            {
                _errorMessage = e.toString();
                Log.e(Common.TAG, e.toString());
                return null;
            }

            return null;
        }

        /**
         * Inform the listener interface of when the forecast location has been loaded.
         *
         * @param forecastLoc
         */
        protected void onPostExecute(ForecastLocation forecastLoc)
        {
            if (!_errorMessage.equalsIgnoreCase("")) {
                Toast.makeText(_context, _errorMessage, Toast.LENGTH_SHORT).show();
            }

            _listener.onLocationLoaded(forecastLoc);
        }

        /**
         * Read the JSON object and the information from the JSON object reader
         *
         * @param jsonString
         * @return
         */
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
                Log.e(Common.TAG, e.toString());
                return null;
            }

           return forecastLocation;
        }

    }


}
