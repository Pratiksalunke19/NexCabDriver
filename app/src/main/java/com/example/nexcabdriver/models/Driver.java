package com.example.nexcabdriver.models;

import android.util.Log;

public class Driver {
    String location,firstname,lastname,email,password,currentLocation;

    public String getCurrentLocation() {
        return currentLocation;
    }

    int trips, hours, ratings,earnings;
    String status;   // status can be (offline / online/ engaged)

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setTrips(int trips) {
        this.trips = trips;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public void setEarnings(int earnings) {
        this.earnings = earnings;
    }

    public Driver(){}
    public Driver(String firstname, String lastname, String email, String password){
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.status = "offline";
    }

    public int getTrips() {
        return trips;
    }

    public int getHours() {
        return hours;
    }

    public int getRatings() {
        return ratings;
    }

    public int getEarnings() {
        return earnings;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    // for testing and debugging
    public void getDriverDetails(){
        String details = "firstname : "+this.getFirstname()+"\n"+
                "lastname : "+this.getLastname()+"\n"+
                "Location : "+this.getCurrentLocation()+"\n"+
                "Email : "+this.getEmail()+"\n"+
                "Password: "+this.getPassword()+"\n"+
                "Status : "+this.getStatus()+"\n";
        Log.d("Driver details: ",details);
    }
}
