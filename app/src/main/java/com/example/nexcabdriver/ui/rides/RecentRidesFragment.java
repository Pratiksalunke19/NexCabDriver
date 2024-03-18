package com.example.nexcabdriver.ui.rides;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nexcabdriver.R;
import com.example.nexcabdriver.models.Ride;
import com.example.nexcabdriver.ui.home.RideAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecentRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Ride> recentRides;
    private RideAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_rides, container, false);

        recyclerView = view.findViewById(R.id.recentRidesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize your list of recent rides and adapter
        recentRides = new ArrayList<>();
        adapter = new RideAdapter(recentRides);
        recyclerView.setAdapter(adapter);

        // Populate recentRides list with data

        return view;
    }
}
