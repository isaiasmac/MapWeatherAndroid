package cl.isaiasmac.mapweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cl.isaiasmac.mapweatherapp.commons.AppConstants;
import cl.isaiasmac.mapweatherapp.commons.AppKeys;
import cl.isaiasmac.mapweatherapp.model.Weather;
import cl.isaiasmac.mapweatherapp.services.MyWeatherServiceInterface;
import cl.isaiasmac.mapweatherapp.services.WeatherService;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private FusedLocationProviderClient fusedLocationClient;
    private LinearLayout headerView;
    private TextView summaryTextView;
    private TextView temperatureTextView;
    private TextView precipProbabilityTextView;
    private TextView precipTypeTextView;
    private TextView humidityTextView;
    private ImageView iconImageView;
    private ImageButton locationImageButton;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        headerView = (LinearLayout) findViewById(R.id.headerView);
        summaryTextView = (TextView) findViewById(R.id.summaryTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        precipProbabilityTextView = (TextView) findViewById(R.id.precipProbabilityTextView);
        precipTypeTextView = (TextView) findViewById(R.id.precipTypeTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        iconImageView = (ImageView) findViewById(R.id.iconImageView);
        locationImageButton = (ImageButton) findViewById(R.id.locationImageButton);
        locationImageButton.setAlpha(0.0f);

        headerView.setVisibility(View.INVISIBLE);
        setTitle("");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MAIN_ACTIVITY_TAG", "click location button");
                getLocation();
            }
        });

    }

    private void getLocation() {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED
        ) {
            this.checkPermission();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("MAIN_ACTIVITY", "Location on success called...");
                        if (location != null) {
                            // Logic to handle location object
                            Log.d("MAIN_ACTIVITY", "Location: "+location.toString());

                            if (googleMap != null) {
                                googleMap.setMyLocationEnabled(true);
                                double latitude = location.getLatitude();
                                double longtiude = location.getLongitude();
                                LatLng coordinate = new LatLng(latitude, longtiude);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, AppConstants.DEFAULT_ZOOM_LEVEL));

                                getAddress(latitude, longtiude);

                                call(latitude, longtiude);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MAIN_ACTIVITY", "Exception: "+e.getLocalizedMessage());
                        showAlert(e.getLocalizedMessage());
                    }
                });
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
            Log.d("TAG_ADDRESS", "address: "+address);
            if (address.size() > 0) {
                Address addressFounded = address.get(0);
                setTitle(addressFounded.getAdminArea()+", "+addressFounded.getCountryName());
            }
        }
        catch (IOException e) {
            Log.d("TAG_ADDRESS", "IOException: "+e.getLocalizedMessage());
        }
        catch (IllegalArgumentException illegalArgumentException) {
            Log.d("TAG_ADDRESS", "IllegalArgumentException: "+illegalArgumentException.getLocalizedMessage());
        }
    }

    private void checkPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                Log.d("TAG", "onPermissionsChecked_ report: "+report.toString());

                if (report.areAllPermissionsGranted()) {
                    getLocation();
                }
                else {
                    showAlert(AppKeys.MESSAGE_PERMISSION);
                }

                if (report.isAnyPermissionPermanentlyDenied()) {
                    showAlert(AppKeys.MESSAGE_PERMISSION);
                }
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Log.d("TAG", "onPermissionRationaleShouldBeShown report: "+permissions.toString());
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void call(double lat, double lng){
        WeatherService weatherService = new WeatherService();
        weatherService.getInfo(this, lat, lng, new MyWeatherServiceInterface() {
            @Override
            public void onDownloadFinished(final Weather weather) {
                Log.d("TAG => ", "weather: "+weather.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        summaryTextView.setText(weather.getSummary());
                        temperatureTextView.setText(Integer.toString(weather.getIntTemperature())+"º");
                        precipProbabilityTextView.setText(weather.getPrecipProbabilityPorcent()+"%");
                        humidityTextView.setText(weather.getHumidityInPorcent()+"%");
                        precipTypeTextView.setText(weather.getPrecipType());
                        iconImageView.setImageResource(weather.getIcon(weather.getIcon()));

                        headerView.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onError(String error) {
                showAlert(error);
            }
        });
    }


    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //googleMap.setMyLocationEnabled(true);
        locationImageButton.setAlpha(1.0f);

        getLocation();
    }
}
