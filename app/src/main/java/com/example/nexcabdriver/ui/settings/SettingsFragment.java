package com.example.nexcabdriver.ui.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nexcabdriver.models.Driver;
import com.example.nexcabdriver.databinding.FragmentSettingsBinding;
import com.example.nexcabdriver.driverauthentication.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Retrieve user data from Firebase Realtime Database and populate the fields
        populateUserData();
    }

    private void populateUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("Drivers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Driver driver = dataSnapshot.getValue(Driver.class);
                        if (driver != null) {
                            Log.d("Firstname: ", driver.getFirstname());
                            Log.d("Email: ", driver.getEmail());
                            Driver.profile_first_name = driver.getFirstname();
                            Driver.profile_last_name = driver.getLastname();
                            Driver.profile_email = driver.getEmail();
                            binding.textViewUserName.setText(String.format("%s %s", driver.getFirstname(), driver.getLastname()));
                            binding.textViewUserEmail.setText(driver.getEmail());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle error
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Driver.profile_first_name == null || Driver.profile_email == null){
            populateUserData();
        }else{
            binding.textViewUserName.setText(String.format("%s %s", Driver.profile_first_name,Driver.profile_last_name));
            binding.textViewUserEmail.setText(Driver.profile_email);
        }
    }

    public void logout(){
        Log.d("status","Inside logout");
        mAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        Log.d("status","Inside logout");
        mAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
}
