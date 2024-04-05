package com.example.nexcabdriver.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexcabdriver.databinding.FragmentHomeBinding;
import com.example.nexcabdriver.models.Driver;
import com.example.nexcabdriver.models.Ride;
import com.example.nexcabdriver.ui.rides.ConfirmRideActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements RideAdapter.OnAcceptClickListener {

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    private RecyclerView recyclerView;
    private RideAdapter rideAdapter;
    private List<Ride> rideList;
    public static Driver driver;
    private String currentLocation;
    private DatabaseReference driverReference;

    private FragmentHomeBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init database objects
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        // Initialize RecyclerView
        recyclerView = binding.tripsRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        rideList = new ArrayList<>();
        rideAdapter = new RideAdapter(rideList);
        recyclerView.setAdapter(rideAdapter);

        // assign the driver reference
        driverReference = database.getReference().child("Drivers").child(firebaseUser.getUid());
//        showProgressBar();
        initDriver(driverReference);

        // check if the driver is online
        if(driver!= null && driver.getStatus().equals("online") && driver.getCurrentLocation()!= null){
            fetchRides();
        }

        // set the listener for handling Accept button event
        rideAdapter.setOnAcceptClickListener(this);

        binding.goOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the current location of driver
                currentLocation = binding.driverCurrentLocation.getText().toString();
                // fetch and display data
                fetchRides();
            }
        });
    }

    public void fetchRides(){
        if (binding.driverCurrentLocation.getText() != null) {
            updateDriver(binding.driverCurrentLocation.getText().toString(),"online");
            // Query Firebase Database to retrieve rides under the specified pickup location
            DatabaseReference ridesRef = database.getReference().child("Rides").child(binding.driverCurrentLocation.getText().toString());
            ridesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    rideList.clear();
                    for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                        Ride ride = rideSnapshot.getValue(Ride.class);
                        if (ride != null) {
                            rideList.add(ride);
                        }
                    }
                    rideAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        } else {
            // Handle empty pickup location
            Toast.makeText(getContext(), "Please enter pickup location", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDriver(String currentLocation, String status){
        // set the location in database
        driver.setCurrentLocation(currentLocation);
        driver.setStatus(status);
        driverReference.setValue(driver);
    }

    @Override
    public void onAcceptClick(Ride ride) {
        // Start the next activity and pass the Ride object as an intent extra
        Intent intent = new Intent(getContext(), ConfirmRideActivity.class);
        intent.putExtra("ride", ride);
        startActivity(intent);
    }

    public void initDriver(DatabaseReference driverReference){
        driverReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                hideProgressBar();
                if(snapshot.exists() && snapshot.hasChildren()){
                    driver = snapshot.getValue(Driver.class);
                }else{
                    Toast.makeText(getContext(), "Problem while initializing Driver object!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                hideProgressBar();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(driver != null){
            binding.driverCurrentLocation.setText(driver.getCurrentLocation());
        }
    }

    public void showProgressBar() {
        binding.progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        binding.progressBar.setVisibility(View.GONE);
    }

}