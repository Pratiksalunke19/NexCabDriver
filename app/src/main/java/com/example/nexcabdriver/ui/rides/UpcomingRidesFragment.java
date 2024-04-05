package com.example.nexcabdriver.ui.rides;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nexcabdriver.R;
import com.example.nexcabdriver.databinding.FragmentUpcomingRidesBinding;
import com.example.nexcabdriver.models.Ride;
import com.example.nexcabdriver.temp.StartRideActivityTemp;
import com.example.nexcabdriver.ui.home.RideAdapter;

import java.util.ArrayList;
import java.util.List;

public class UpcomingRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Ride> upcomingRides;
    private RideAdapter adapter;

    FragmentUpcomingRidesBinding binding;

    public UpcomingRidesFragment(){}
    public UpcomingRidesFragment(List<Ride> upcomingRides){
        this.upcomingRides = upcomingRides;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding = FragmentUpcomingRidesBinding.inflate(getLayoutInflater());
       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the ride details if available
        if(ConfirmRideActivity.ride != null){
            setRideDetails(ConfirmRideActivity.ride);
        }

        Button button = view.findViewById(R.id.startRideButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StartRideActivityTemp.class);
                startActivity(intent);
            }
        });
    }

    public void setRideDetails(Ride ride){
//        String details = "Pick Up Location: " +ride.getPickupLocation() + "\n\n";
        String details = "Drop Off Location: "+ ride.getDropoffLocation() + "\n\n";
        details += "Date: "+ ride.getDate() + "\n\n";
        details += "Time: "+ride.getTime() + "\n\n";
        details += "Sharing: "+ (ride.isRide_sharing() ? "Enabled" : "Disabled") + "\n\n";
        details += "Passenger count: "+ 1 + "\n\n";
        details += "Fair: "+ "Rs. 400";

        binding.confirmRideDetailsTextView.setText(details);
    }
}
