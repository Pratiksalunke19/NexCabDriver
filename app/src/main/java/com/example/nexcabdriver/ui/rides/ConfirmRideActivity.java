package com.example.nexcabdriver.ui.rides;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nexcabdriver.MainActivity;
import com.example.nexcabdriver.R;
import com.example.nexcabdriver.databinding.ActivityConfirmRideBinding;
import com.example.nexcabdriver.models.Driver;
import com.example.nexcabdriver.models.Ride;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConfirmRideActivity extends AppCompatActivity {

    public static Ride ride;
    FirebaseDatabase database;
    FirebaseAuth auth;

    ActivityConfirmRideBinding binding;
    private String receiverToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmRideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the parent intent
        Intent intent = getIntent();

        if(intent != null){
            ride = (Ride)intent.getSerializableExtra("ride");
            assert ride != null;
            ride.getRideDetails();
            if(ride != null){
                setRideDetails();
            }
        }else{
            Toast.makeText(this, "Problem while loading intent!", Toast.LENGTH_SHORT).show();
        }

        // get database objects
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // set listener for confirm button
        binding.confirmRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(database != null){
                    // set ride as booked
                    ride.setIs_booked(true);
                    getReceiverToken();
                }else{
                    Toast.makeText(ConfirmRideActivity.this, "Problem in Database contact! Cannot store ride!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setRideDetails(){
//        String details = "Pick Up Location: " +ride.getPickupLocation() + "\n\n";
        String details = "Drop Off Location: "+ ride.getDropoffLocation() + "\n\n";
        details += "Date: "+ ride.getDate() + "\n\n";
        details += "Time: "+ride.getTime() + "\n\n";
        details += "Sharing: "+ (ride.isRide_sharing() ? "Enabled" : "Disabled") + "\n\n";
        details += "Passenger count: "+ ride.getPassenger_count()+"\n\n";
        details += "Fair: "+ "Rs. ";

        // set the driver name
        ConfirmRideActivity.ride.setDriver_name(Driver.profile_first_name+" "+Driver.profile_last_name);

        binding.confirmRideDetailsTextView.setText(details);
    }

    public void updateRideDetails(){
        database.getReference().child("Rides").child(ride.getPickupLocation().toLowerCase()).child(ride.getRideId()).setValue(ride);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("selectedTab",2);
        startActivity(intent);
    }

    @SuppressLint("StaticFieldLeak")
    private void sendNotification(String title, String messageBody){
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
                    Toast.makeText(ConfirmRideActivity.this, "Exception while sending notification!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void getReceiverToken(){
        if(auth.getUid()!=null){
            DatabaseReference reference = database.getReference().child("Users").child(ride.getUserId());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get the token
                    receiverToken = (String)snapshot.child("token").getValue();
                    Log.d(": ", receiverToken+" ");
//                    Log.d("Id: ", ride.getUserId()+" ");
                    if(receiverToken != null){
                        // set hasupcomingride
                        Driver.setHasUpcomingRide(true);
                        // send message to client
                        sendNotification("Ride confirmation","Ride has been accepted!");
                        // update ride in database
                        updateRideDetails();
                        // move ride to booked rides
                        moveRideToBookedRides();
                    }else{
                        Toast.makeText(ConfirmRideActivity.this, "Token is null!", Toast.LENGTH_SHORT).show();
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

    // Method to move ride details from pickup location to booked rides
    private void moveRideToBookedRides() {
        DatabaseReference pickupLocationRef = database.getReference().child("Rides").child(ride.getPickupLocation().toLowerCase());
        DatabaseReference bookedRidesRef = database.getReference().child("Rides").child(ride.getPickupLocation().toLowerCase()).child("booked");


        // Get the ride details from pickup location
        pickupLocationRef.child(ride.getRideId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the ride details exist
                if (dataSnapshot.exists()) {
                    // Get the ride details
                    Object rideDetails = dataSnapshot.getValue();

                    // Remove the ride details from pickup location
                    pickupLocationRef.child(ride.getRideId()).removeValue();

                    // Add the ride details to booked rides
                    bookedRidesRef.child(ride.getRideId()).setValue(rideDetails);
                } else {
                    // Handle if ride details do not exist
                    Log.d("MoveRide", "Ride details not found at pickup location");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Log.e("MoveRide", "Database error: " + databaseError.getMessage());
            }
        });
    }
}