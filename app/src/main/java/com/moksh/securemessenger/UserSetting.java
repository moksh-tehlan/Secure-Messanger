package com.moksh.securemessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moksh.securemessenger.LoginActivities.SignIn;

import java.util.regex.Pattern;

public class UserSetting extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    EditText firstName,lastName,username;
    TextView nameError,usernameError;
    ImageView userProfile;
    String lastUsername,lastFirstName,lastLastName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

//        hooks
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        username = findViewById(R.id.username);
        userProfile = findViewById(R.id.userImage);
        nameError = findViewById(R.id.name_error);
        usernameError = findViewById(R.id.username_error);

        lastUsername = username.getText().toString();



        dataCollector();
    }

    public void dataCollector()
    {
        reference = database.getReference().child("Users").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    switch (dataSnapshot.getKey())
                    {
                        case "firstName": firstName.setText(dataSnapshot.getValue().toString());
                        break;
                        case "lastName": lastName.setText(dataSnapshot.getValue().toString());
                        break;
                        case "username": username.setText(dataSnapshot.getValue().toString());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean checkError() {
        int count = 0;

        if (firstName.getText().toString().isEmpty()) {
            nameError.setVisibility(View.VISIBLE);
            count++;
        } else {
            nameError.setVisibility(View.GONE);
        }
        if (Pattern.matches("^[a-z]([._-](?![._-])|[a-z0-9]){3,18}[a-z0-9]$", username.getText().toString()) == false) {
            usernameError.setText("Username can only contain [a-z][0-9][_.-] and can't be smaller then 5 characters");
            usernameError.setVisibility(View.VISIBLE);
            count++;
        } else {
            usernameError.setVisibility(View.GONE);
        }
        if (count > 0) {
            return false;
        } else {
            return true;
        }
    }

    public void uploadData(View view)
    {
        if(checkError() && !lastUsername.equals(username.getText().toString()))
        {
            reference = database.getReference().child("usernames");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = 0;
                    for (DataSnapshot dataSnapshot: snapshot.getChildren())
                    {
                        if (username.getText().toString().equals(dataSnapshot.getValue().toString()))
                        {
                            count++;
                        }
                    }
                    if (count>0)
                    {
                        Toast.makeText(getBaseContext(), "Username Already exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        reference = database.getReference().child("Users").child(auth.getUid()).child("username");
                        reference.setValue(username.getText().toString());

                        reference = database.getReference().child("Users").child(auth.getUid()).child("firstName");
                        reference.setValue(firstName.getText().toString());

                        reference = database.getReference().child("Users").child(auth.getUid()).child("lastName");
                        reference.setValue(lastName.getText().toString());

                        Toast.makeText(getBaseContext(), "Saved", Toast.LENGTH_SHORT).show();
                        UserSetting.super.onBackPressed();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void goback(View view)
    {
        super.onBackPressed();
    }
    public void logOut(View view)
    {
        auth.signOut();
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);
        finish();
    }
}