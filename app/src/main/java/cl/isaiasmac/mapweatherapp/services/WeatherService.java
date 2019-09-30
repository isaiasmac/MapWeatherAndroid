package cl.isaiasmac.mapweatherapp.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import cl.isaiasmac.mapweatherapp.RequestSingleton;
import cl.isaiasmac.mapweatherapp.commons.AppConstants;
import cl.isaiasmac.mapweatherapp.commons.AppKeys;
import cl.isaiasmac.mapweatherapp.model.Weather;


public class WeatherService {

    private MyWeatherServiceInterface callbackInterface;

    public void setCallbackInterface(MyWeatherServiceInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    public void getInfo(Context context, double lat, double lng, final MyWeatherServiceInterface callback) {
        String url = String.format(AppConstants.URL_DARK_SKY, lat, lng);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("GET_INFO_TAG", response.toString());

                        try {
                            Weather weather = parseJSON(response);
                            callback.onDownloadFinished(weather);
                        }
                        catch (JSONException e) {
                            Log.e("TAG", e.getLocalizedMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MAIN_ACTIVITY_TAG", "ERROR ==> "+error.getLocalizedMessage());
                        callback.onError(error.getLocalizedMessage());
                    }
                });

        RequestSingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private Weather parseJSON(JSONObject response) throws JSONException {
        JSONObject currentlyJSON = response.getJSONObject(AppKeys.CURRENTLY);
        Weather weather = new Weather();
        weather.setSummary(currentlyJSON.getString(AppKeys.SUMMARY));
        weather.setIcon(currentlyJSON.getString(AppKeys.ICON));
        weather.setTemperature(currentlyJSON.getDouble(AppKeys.TEMPERATURE));
        weather.setHumidity(currentlyJSON.getDouble(AppKeys.HUMIDITY));
        weather.setPrecipProbability(currentlyJSON.getDouble(AppKeys.PRECIP_PROBABILITY));
        if (currentlyJSON.has(AppKeys.PRECIP_TYPE)) {
            if (!currentlyJSON.isNull(AppKeys.PRECIP_TYPE)) {
                weather.setPrecipType(currentlyJSON.getString(AppKeys.PRECIP_TYPE));
            }
        }

        return weather;
    }


}
