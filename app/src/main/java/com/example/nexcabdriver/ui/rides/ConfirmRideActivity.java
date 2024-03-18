package com.example.nexcabdriver.ui.rides;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.nexcabdriver.MainActivity;
import com.example.nexcabdriver.R;
import com.example.nexcabdriver.databinding.ActivityConfirmRideBinding;
import com.example.nexcabdriver.models.Ride;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmRideActivity extends AppCompatActivity {

    public static Ride ride;
    FirebaseDatabase database;

    ActivityConfirmRideBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmRideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the parent intent
        Intent intent = getIntent();

        if(intent != null){
            ride = (Ride)intent.getSerializableExtra("ride");
            ride.getRideDetails();
            if(ride != null){
                setRideDetails();
            }
        }else{
            Toast.makeText(this, "Problem while loading intent!", Toast.LENGTH_SHORT).show();
        }

        // get database objects
        database = FirebaseDatabase.getInstance();

        // set listener for confirm button
        binding.confirmRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(database != null){
                    // set ride as booked
                    ride.setIs_booked(true);
                    // update ride in database
                    updateRideDetails();
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
        details += "Passenger count: "+ 0;
        details += "Fair: "+ "Rs. ";

        binding.confirmRideDetailsTextView.setText(details);
    }

    public void updateRideDetails(){
        database.getReference().child("Rides").child(ride.getPickupLocation()).child(ride.getrideId()).setValue(ride);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("selectedTab",2);
        startActivity(intent);
    }
}