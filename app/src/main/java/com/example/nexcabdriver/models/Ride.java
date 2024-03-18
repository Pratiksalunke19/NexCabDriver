package com.example.nexcabdriver.models;

import android.util.Log;

import java.io.Serializable;

public class Ride implements Serializable {
    private String pickupLocation, dropoffLocation, date, time, status,rideId;
    private boolean ride_sharing;
    private boolean is_booked = false;

    public boolean isIs_booked() {
        return is_booked;
    }

    private String userId; // New field to store user's ID

    public void setIs_booked(boolean is_booked) {
        this.is_booked = is_booked;
    }

    public Ride() {
    }

    public String getrideId() {
        return rideId;
    }

    public Ride(String pickupLocation, String dropoffLocation, String date, String time, String status, boolean ride_sharing, String userId) {
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.date = date;
        this.time = time;
        this.status = status;
        this.ride_sharing = ride_sharing;
        this.userId = userId;
    }

    public void setrideId(String rideId) {
        this.rideId = rideId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setRide_sharing(boolean ride_sharing) {
        this.ride_sharing = ride_sharing;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isRide_sharing() {
        return ride_sharing;
    }

    public String getUserId() {
        return userId;
    }

    // for testing and debugging
    public void getRideDetails(){
        String details = "Pickup location: "+this.getPickupLocation()+"\n"+
                "Dropoff Location: "+this.getDropoffLocation()+"\n"+
                "Date: "+this.getDate()+"\n"+
                "Time: "+this.getTime()+"\n"+
                "Status: "+this.getStatus()+"\n"+
                "User id: "+this.getUserId()+"\n"+
                "Ride id: "+this.getrideId()+"\n"+
                "Ride Sharing: "+this.isRide_sharing()+"\n"+
                "Is booked: "+this.is_booked+"\n";
        Log.d("Ride details: ",details);
    }
}
