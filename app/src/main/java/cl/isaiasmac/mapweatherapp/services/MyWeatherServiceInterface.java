package cl.isaiasmac.mapweatherapp.services;

import cl.isaiasmac.mapweatherapp.model.Weather;

public interface MyWeatherServiceInterface {
    void onDownloadFinished(Weather weather);
    void onError(String error);
}
