package com.example.shottracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnStartStop;
    private TextView tvCoordinates;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double startingLatitude = 0.0;
    private double startingLongitude = 0.0;
    private double endingLatitude = 0.0;
    private double endingLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);

        btnStartStop = findViewById(R.id.btnStartStop);
        tvCoordinates = findViewById(R.id.tvCoordinates);

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                Location location = gpsTracker.getLocation();

                if(location != null){
                    if(btnStartStop.getText().equals("Start Distance Tracker")) {
                        startingLatitude = location.getLatitude();
                        startingLongitude = location.getLongitude();
                        btnStartStop.setText(R.string.stopTracker);
                    } else{
                        endingLatitude = location.getLatitude();
                        endingLongitude = location.getLongitude();
                        btnStartStop.setText(R.string.startTracker);
                    }

                    if(startingLatitude != 0.0 && endingLongitude != 0.0) {
                        double yards = calculateDistance (startingLongitude, startingLatitude, endingLongitude, endingLatitude);
                        tvCoordinates.setText("Distance: " + yards);
                    }
                }
            }
        });
    }

    private double calculateDistance(double startingLongitude, double startingLatitude, double endingLongitude, double endingLatitude){
        //Convert Earths Radius from km to yards
        double earthRadius = 6173;

        double diffLat = degreesToRadians(endingLatitude - startingLatitude);
        double diffLong = degreesToRadians(endingLongitude - startingLongitude);

        startingLatitude = degreesToRadians(startingLatitude);
        endingLatitude = degreesToRadians(endingLatitude);

        double temp = Math.sin(diffLat/2) * Math.sin(diffLat/2) +
                Math.sin(diffLong/2) * Math.sin(diffLong/2) * Math.cos(startingLatitude) * Math.cos(endingLatitude);

        double temp2 = 2 * Math.atan2(Math.sqrt(temp), Math.sqrt(1 - temp));

        //Convert to yards and return
        return (earthRadius * 1093.61) * temp2;
    }

    private double degreesToRadians(double degrees){
        return degrees * (Math.PI / 180);
    }
}
