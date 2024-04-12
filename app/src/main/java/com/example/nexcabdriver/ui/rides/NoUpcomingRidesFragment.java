package com.example.nexcabdriver.ui.rides;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nexcabdriver.R;
import com.example.nexcabdriver.databinding.FragmentNoUpcomingRidesBinding;

public class NoUpcomingRidesFragment extends Fragment {

  FragmentNoUpcomingRidesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNoUpcomingRidesBinding.inflate(getLayoutInflater());



        return binding.getRoot();
    }
}