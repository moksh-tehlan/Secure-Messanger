package com.moksh.securemessenger.LoginActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.moksh.securemessenger.ChatActivity;
import com.moksh.securemessenger.Models.UserModels;
import com.moksh.securemessenger.R;
import com.moksh.securemessenger.databinding.ActivitySignupBinding;

import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    ActivitySignupBinding binding;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference usernameReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        initialising firebase auth
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    //Method to check details and launching Activity
    public boolean checkError() {
        int count = 0;
        if (binding.password.getText().toString().isEmpty() || binding.password.getText().toString().length() < 6) {
            binding.passwordError.setVisibility(View.VISIBLE);
            count++;
        } else {
            binding.passwordError.setVisibility(View.GONE);
        }
        if (binding.firstName.getText().toString().isEmpty()) {
            binding.nameError.setVisibility(View.VISIBLE);
            count++;
        } else {
            binding.nameError.setVisibility(View.GONE);
        }
        if (Pattern.matches("^[a-z]([._-](?![._-])|[a-z0-9]){3,18}[a-z0-9]$", binding.username.getText().toString()) == false) {
            binding.usernameError.setText("Username can only contain [a-z][0-9][_.-] and can't be smaller then 5 characters");
            binding.usernameError.setVisibility(View.VISIBLE);
            count++;
        } else {
            binding.usernameError.setVisibility(View.GONE);
        }

        if (binding.email.getText().toString().isEmpty()) {
            binding.emailError.setVisibility(View.VISIBLE);
            count++;
        } else {
            binding.emailError.setVisibility(View.GONE);
        }

        if (count > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void signup(View view) {
        if (checkError()){
            auth.createUserWithEmailAndPassword(binding.email.getText().toString(), binding.password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                UserModels userModels = new UserModels(binding.username.getText().toString(),binding.firstName.getText().toString(),binding.lastName.getText().toString(),binding.password.getText().toString(),binding.email.getText().toString(),auth.getUid());
                                String id = task.getResult().getUser().getUid();

                                reference = database.getReference().child("Users").child(id);
                                usernameReference = database.getReference().child("usernames").child(binding.username.getText().toString());
                                reference.setValue(userModels);
                                usernameReference.setValue(id);

                                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                                startActivity(intent);
                            } else {
                                if(task.getException().getMessage().equals("The email address is badly formatted."))
                                {
                                    Toast.makeText(getBaseContext(), "Please check your email", Toast.LENGTH_SHORT).show();
                                }
                                else if(task.getException().getMessage().equals("The email address is already in use by another account."))
                                {
                                    Toast.makeText(getBaseContext(), "The email address is already in use please go back to sign in page", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getBaseContext(), "Something went wrong please check your Internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }
    public  void goback(View view)
    {
        super.onBackPressed();
    }
}