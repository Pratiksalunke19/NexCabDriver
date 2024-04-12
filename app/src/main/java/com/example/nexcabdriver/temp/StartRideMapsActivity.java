package com.example.nexcabdriver.temp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.nexcabdriver.MainActivity;
import com.example.nexcabdriver.R;
import com.example.nexcabdriver.models.Driver;
import com.example.nexcabdriver.models.Ride;
import com.example.nexcabdriver.ui.rides.ConfirmRideActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StartRideMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment supportMapFragment;
    String receiverToken;
    FirebaseDatabase database;
    FirebaseAuth auth;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_ride_map2);

        //
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and initialize the GoogleMap object
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapStartRide);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Get the GoogleMap object
                GoogleMap mMap = googleMap;

                // Get pickup and dropoff locations from ConfirmRideActivity
                String pickupLocation = ConfirmRideActivity.ride.getPickupLocation();
                String dropoffLocation = ConfirmRideActivity.ride.getDropoffLocation();

                // Check if locations are not null
                if (pickupLocation != null && dropoffLocation != null) {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        // Get the list of addresses for pickup and dropoff locations
                        List<Address> pickupAddressList = geocoder.getFromLocationName(pickupLocation, 1);
                        List<Address> dropoffAddressList = geocoder.getFromLocationName(dropoffLocation, 1);

                        // Check if both pickup and dropoff address lists are not empty
                        if (!pickupAddressList.isEmpty() && !dropoffAddressList.isEmpty()) {
                            // Get the first address from the pickup address list
                            Address pickupAddress = pickupAddressList.get(0);
                            LatLng pickupLatLng = new LatLng(pickupAddress.getLatitude(), pickupAddress.getLongitude());
                            // Add marker for pickup location
                            mMap.addMarker(new MarkerOptions().position(pickupLatLng).title(pickupLocation));

                            // Get the first address from the dropoff address list
                            Address dropoffAddress = dropoffAddressList.get(0);
                            LatLng dropoffLatLng = new LatLng(dropoffAddress.getLatitude(), dropoffAddress.getLongitude());
                            // Add marker for dropoff location
                            mMap.addMarker(new MarkerOptions().position(dropoffLatLng).title(dropoffLocation));

                            // Move camera to show both markers
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(pickupLatLng);
                            builder.include(dropoffLatLng);
                            LatLngBounds bounds = builder.build();
                            int padding = 100; // Padding in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.moveCamera(cu);
                        } else {
                            Toast.makeText(StartRideMapsActivity.this, "No matching addresses found for pickup or dropoff locations", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(StartRideMapsActivity.this, "Locations are null!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // set onclicklistener to end ride button
        Button endRidebutton = findViewById(R.id.endRideButtonMapsActivity);
        endRidebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReceiverToken();
                updateDriverCards();
            }
        });

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);
    }

    public void updateDriverCards(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Drivers");

        ref.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the ride data exists
                if (dataSnapshot.exists()) {
                    // Retrieve the Ride object
                    Driver driver = dataSnapshot.getValue(Driver.class);

                    // update card values
                    assert driver != null;
                    driver.setTrips(driver.getTrips() + 1);
                    driver.setEarnings(driver.getEarnings() + 400);
                    driver.setHours(driver.getHours() + 1);
                    driver.setRatings(4);

                    // set driver with updated values
                    database.getReference().child("Drivers").child(auth.getUid()).setValue(driver);
                } else {
                    Toast.makeText(StartRideMapsActivity.this, "Problem in updating cards!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
                Log.e("Firebase", "Error fetching ride data: " + databaseError.getMessage());
            }
        });
    }

    public void getReceiverToken(){
        // set hasupcomingride as false
        Driver.setHasUpcomingRide(false);

        if(auth.getUid()!=null){
            DatabaseReference reference = database.getReference().child("Users").child(ConfirmRideActivity.ride.getUserId());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get the token
                    receiverToken = (String)snapshot.child("token").getValue();
                    Log.d(": ", receiverToken+" ");
//                    Log.d("Id: ", ride.getUserId()+" ");
                    if(receiverToken != null){

                        // send message to client
                        sendNotification();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }else{
                        Toast.makeText(getApplicationContext(), "Token is null!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(this, "User Auth object null! Cannot send message!", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void sendNotification(){
        String title = "Ride ended";
        String messageBody = "Ride Ended ! Hope you enjoyed our ride!";
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonNotif = new JSONObject();
        JSONObject wholeObj = new JSONObject();
        try {
            jsonNotif.put("title",title);
            jsonNotif.put("body",messageBody);
            wholeObj.put("to",receiverToken);
            wholeObj.put("notification",jsonNotif);
        } catch (JSONException e) {
            Log.d("ConfirmRideActivity: SendNotification ", "Exception while sending");
            Toast.makeText(this, "Exception while sending notification!", Toast.LENGTH_SHORT).show();
        }

        RequestBody requestBody = RequestBody.create(mediaType,wholeObj.toString());
        Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization","key=AAAAcx0Yvnw:APA91bEQjo2hXkWqcsyaOTLvZO14C8REtJ6bgN7X4kk4vKx05TEL5ShNB4PXE-fQrxYzKVAhXLggJqoBHBIbdke469Pcril1Le98HuvqgTOnnMNcGnSS-ZNvNJ0YrbNtnasTswNZf4N5")
                .addHeader("Content-Type","application/json").build();

        AsyncTask<Void, Void, Response> execute;
        execute = new AsyncTask<Void, Void, Response>() {
            @Override
            protected Response doInBackground(Void... voids) {
                try {
                    return client.newCall(request).execute();
                } catch (IOException e) {
                    Log.e("ConfirmRideActivity", "Exception while sending response", e);
                    return null; // or handle the exception as needed
                }
            }

            @Override
            protected void onPostExecute(Response response) {
                if (response != null) {
                    // Handle the response here
                } else {
                    Toast.makeText(getApplicationContext(), "Exception while sending notification!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}