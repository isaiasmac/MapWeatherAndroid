package cl.isaiasmac.mapweatherapp.commons;

public class AppConstants {

    private final static String DARK_SKY_APIKEY = "57d5ae8abaa86bc1a9419156699635e9";

    public final static String URL_DARK_SKY = "https://api.darksky.net/forecast/"+DARK_SKY_APIKEY+"/%f,%f?lang=es&exclude=hourly,minutely&units=si";

    public final static float DEFAULT_ZOOM_LEVEL = 12f;
}
