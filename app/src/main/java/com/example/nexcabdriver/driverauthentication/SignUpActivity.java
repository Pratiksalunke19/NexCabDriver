package com.example.nexcabdriver.driverauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nexcabdriver.databinding.ActivitySignUpBinding;
import com.example.nexcabdriver.models.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    ActivitySignUpBinding binding;
    FirebaseDatabase firebaseDatabase;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // hide the Action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // redirect to login page when clicked
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBlankFields()) {
                    if(binding.password.getText().toString().equals(binding.passwordRepeat.getText().toString())) {
                        firebaseAuth.createUserWithEmailAndPassword
                                        (binding.email.getText().toString(), binding.password.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Add to real time database if task is successful
                                        if (task.isSuccessful()) {
                                            Driver driver = new Driver(binding.firstname.getText().toString(), binding.lastname.getText().toString(),
                                                    binding.email.getText().toString(), binding.password.getText().toString());
                                            Toast.makeText(SignUpActivity
                                                    .this, "User Created Successfully!", Toast.LENGTH_SHORT).show();
                                            String id = task.getResult().getUser().getUid();
                                            Task<Void> updateDatabaseTask = firebaseDatabase.getReference().child("Drivers").child(id).setValue(driver);
                                            if(updateDatabaseTask.isSuccessful()){
                                                intent = new Intent(getApplicationContext(),LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(SignUpActivity
                                                    .this, Objects.requireNonNull(task.getException())
                                                    .getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else{
                        Toast.makeText(SignUpActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkBlankFields(){
        return TextUtils.isEmpty(binding.firstname.getText()) && TextUtils.isEmpty(binding.lastname.getText()) &&
                TextUtils.isEmpty(binding.email.getText()) && TextUtils.isEmpty(binding.password.getText()) &&
                TextUtils.isEmpty(binding.passwordRepeat.getText());
    }
}