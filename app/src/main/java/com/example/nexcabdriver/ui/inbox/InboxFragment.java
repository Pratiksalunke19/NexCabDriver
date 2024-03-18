package com.example.nexcabdriver.ui.inbox;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nexcabdriver.R;
import com.example.nexcabdriver.databinding.FragmentInboxBinding;

public class InboxFragment extends Fragment {

    FragmentInboxBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInboxBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
}