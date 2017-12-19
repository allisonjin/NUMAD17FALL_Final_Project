package edu.neu.madcourse.zhiyaojin.finalproject.project.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public abstract class BaseLocationActivity extends BaseActivity {

    private final static String TAG = "BaseLocationActivity";

    private final static int LOCATION_PERMISSION_CODE = 3;

    private static String[] locationPermissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;

    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkLocationPermissions()) {
            Log.d(TAG, "permission request onCreate");
            askForLocationPermissions();
        } else {
            initLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdate();
    }

    protected boolean checkLocationPermissions() {
        return checkPermissions(locationPermissions);
    }

    protected void askForLocationPermissions() {
        askForPermission(locationPermissions, LOCATION_PERMISSION_CODE);
    }

    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d(TAG, "on location result");
                    mLocation = location;
                    onGetLocationSuccess(location);
                }
            }
        };
        mLocationRequest = defaultLocationRequest();
        getLastLocation();
        startLocationUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "permission result");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "permission result granted");
            initLocation();
//            onLocationPermissionsResult();
        }
    }

//    public abstract void onLocationPermissionsResult();

    protected Location getCurrentLocation() {
        return mLocation;
    }

    protected LocationRequest defaultLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @SuppressLint("MissingPermission")
    protected void getLastLocation() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.d(TAG, "get last location");
                            mLocation = location;
                            onGetLocationSuccess(location);
                        }
                    });
        }
    }

    public abstract void onGetLocationSuccess(Location location);

    @SuppressLint("MissingPermission")
    protected void startLocationUpdate() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    protected FusedLocationProviderClient getFusedLocationClient() {
        return mFusedLocationClient;
    }

    protected void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
}
