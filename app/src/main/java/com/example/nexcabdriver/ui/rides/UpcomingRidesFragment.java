package com.example.nexcabdriver.ui.rides;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nexcabdriver.R;
import com.example.nexcabdriver.StartRideMapActivity;
import com.example.nexcabdriver.databinding.FragmentUpcomingRidesBinding;
import com.example.nexcabdriver.models.Driver;
import com.example.nexcabdriver.models.Ride;
import com.example.nexcabdriver.temp.StartRideActivityTemp;
import com.example.nexcabdriver.temp.StartRideMapsActivity;
import com.example.nexcabdriver.ui.home.RideAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpcomingRidesFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Ride> upcomingRides;
    private RideAdapter adapter;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String receiverToken;

    FragmentUpcomingRidesBinding binding;

    public UpcomingRidesFragment(){}
    public UpcomingRidesFragment(List<Ride> upcomingRides){
        this.upcomingRides = upcomingRides;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       binding = FragmentUpcomingRidesBinding.inflate(getLayoutInflater());

       // init database
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

       return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the ride details if available
        if(ConfirmRideActivity.ride != null){
            setRideDetails(ConfirmRideActivity.ride);
        }

        // add ride to recent rides in database
        // TODO: 08-04-2024 Find out why its not working
//        addRideToDatabase();

        Button button = view.findViewById(R.id.startRideButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StartRideMapsActivity.class);

                getReceiverToken();
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // control the visibility of views
        if(Driver.hasUpcomingRide){
            binding.confirmRideDetailsTextView.setVisibility(View.VISIBLE);
            binding.startRideButton.setVisibility(View.VISIBLE);

            binding.noUpcomingRidesTextView.setVisibility(View.GONE);
        }else{
            binding.confirmRideDetailsTextView.setVisibility(View.GONE);
            binding.startRideButton.setVisibility(View.GONE);

            binding.noUpcomingRidesTextView.setVisibility(View.VISIBLE);
        }
    }

    public void addRideToDatabase() {
        // set ride under Driver
        Ride ride = ConfirmRideActivity.ride;
        DatabaseReference ref = database.getReference().child("Drivers").child(auth.getUid()).child("rides");
        Task<Void> task = ref.child(ConfirmRideActivity.ride.getRideId()).setValue(ride);
        Log.d("addRideToDatabase successfully? ", task.isSuccessful()+" ?");
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

    @SuppressLint("StaticFieldLeak")
    private void sendNotification(String title, String messageBody){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject wholeObj = new JSONObject();
        try {

            // Add custom data payload for starting the activity
            JSONObject dataPayload = new JSONObject();
            dataPayload.put("action", "start_activity");
            dataPayload.put("pickup_location", ConfirmRideActivity.ride.getPickupLocation());
            dataPayload.put("activity_name", "RideStartedActivity");
            dataPayload.put("ride_id",ConfirmRideActivity.ride.getRideId());
            dataPayload.put("ride_user_id",ConfirmRideActivity.ride.getUserId());

            wholeObj.put("to",receiverToken);
//            wholeObj.put("notification",jsonNotif);
            wholeObj.put("data", dataPayload);
        } catch (JSONException e) {
            Log.d("ConfirmRideActivity: SendNotification ", "Exception while sending");
            Toast.makeText(requireContext(), "Exception while sending notification!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(requireContext(), "Exception while sending notification!", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    public void getReceiverToken(){
        if(auth.getUid()!=null){
            DatabaseReference reference = database.getReference().child("Users").child(ConfirmRideActivity.ride.getUserId());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // get the token
                    receiverToken = (String)snapshot.child("token").getValue();
                    Log.d("Token ", receiverToken);
                    Log.d(": ", receiverToken+" ");
//                    Log.d("Id: ", ride.getUserId()+" ");
                    if(receiverToken != null){
                        // send message to client
//                        sendStartRideMessageToClient();
                        sendNotification("Activity","Ride has started");
                    }else{
                        Toast.makeText(requireContext(), "Token is null!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(requireContext(), "User Auth object null! Cannot send message!", Toast.LENGTH_SHORT).show();
        }

    }

}
