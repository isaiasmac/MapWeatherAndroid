package cl.isaiasmac.mapweatherapp.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import cl.isaiasmac.mapweatherapp.R;

/*

    var summary: String = ""
    var icon: String = ""
    var humidity: NSNumber = 0.0
    var precipProbability: NSNumber = 0.0
    var temperature: NSNumber = 0.0
    var precipType: String? = ""

 */
public class Weather {
    private String summary;
    private String icon;
    private double humidity;
    private double precipProbability;
    private double temperature;
    private String precipType;

    @NonNull
    @Override
    public String toString() {
        return this.getSummary() + ", "+this.temperature+", precipProb: "+this.precipProbability;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getHumidityInPorcent() {
        return (int)(this.humidity * 100);
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPrecipProbability() {
        return precipProbability;
    }

    public int getPrecipProbabilityPorcent() {
        return (int)(this.precipProbability * 100);
    }

    public void setPrecipProbability(double precipProbability) {
        this.precipProbability = precipProbability;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getIntTemperature() {
        return (int) Math.round(this.temperature);
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getPrecipType() {
        if (precipType == null) {
            return "----";
        }
        if (precipType.isEmpty()) {
            return "----";
        }

        switch (precipType) {
            case "rain":
                return "Lluvia";
            case "snow":
                return "Nieve";
            case "sleet":
                return "Agua Nieve";
        }

        return precipType;
    }

    public void setPrecipType(String precipType) {
        this.precipType = precipType;
    }

    public Integer getIcon(String name) {
        if (name == null) {
            name = "clear_day";
        }

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("clear_day", R.drawable.clear_day);
        map.put("clear_night", R.drawable.clear_night);
        map.put("cloudy", R.drawable.cloudy);
        map.put("partly_cloudy_day", R.drawable.partly_cloudy_day);
        map.put("partly_cloudy_night", R.drawable.partly_cloudy_night);
        map.put("sleet", R.drawable.sleet);
        map.put("snow", R.drawable.snow);
        map.put("thunderstorm", R.drawable.thunderstorm);
        map.put("rain", R.drawable.rain);

        String nameFix = name.replace('-', '_');
        return map.get(nameFix);
    }
}
