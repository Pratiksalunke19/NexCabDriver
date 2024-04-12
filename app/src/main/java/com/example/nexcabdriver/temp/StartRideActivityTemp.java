package com.example.nexcabdriver.temp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StartRideActivityTemp extends AppCompatActivity {

    String receiverToken;
    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get database objects
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_start_ride);
        Button button = findViewById(R.id.endRideButton);
        button.setOnClickListener(v -> getReceiverToken());
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
}