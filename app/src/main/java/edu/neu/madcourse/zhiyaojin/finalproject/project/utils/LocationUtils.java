package edu.neu.madcourse.zhiyaojin.finalproject.project.utils;

import android.location.Location;

import java.util.Random;

public class LocationUtils {

    public static double getDistance(Location location1, Location location2) {
        return location1.distanceTo(location2);
    }

    public static double[] getCoarseLatLng(double lat, double lng) {
        int bound = 5;
        double latOffset = nextDoubleOffset(bound);
        double lngOffset = nextDoubleOffset(bound);
        double[] newLatLng = new double[2];
        newLatLng[0] = lat + latOffset;
        newLatLng[1] = lng + lngOffset;
        return newLatLng;
    }

    private static double nextDoubleOffset(int bound) {
        Random random = new Random();
        return (random.nextInt(bound) + random.nextDouble()) / 10000;
    }

    public static Location newLocation(double lat, double lng) {
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        return location;
    }

    public static boolean isLocationClose(Location userLoc, Location missionLoc, double maxDistance) {
        return getDistance(userLoc, missionLoc) <= maxDistance;
    }
}
