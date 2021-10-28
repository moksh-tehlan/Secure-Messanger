package com.moksh.securemessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.moksh.securemessenger.Adapters.UsersAdapter;
import com.moksh.securemessenger.Models.UserModels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView userRecycler, chatRecycler;
    LinearLayout chatToolbar;
    FirebaseDatabase database;
    View toolbar;
    FirebaseAuth auth;
    UsersAdapter usersAdapter;
    EditText message;
    CardView send;
    LinearLayout linearLayout;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        linearLayout = findViewById(R.id.splashActivity);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation_view);
        chatToolbar = findViewById(R.id.chat_toolbar);

        LayoutInflater inflater = LayoutInflater.from(this);
        toolbar = inflater.inflate(R.layout.chat_toolbar, null);
        navigationView.bringToFront();

        message = findViewById(R.id.message);

        send = findViewById(R.id.send);
        chatRecycler = findViewById(R.id.chatRecyclerView);
        userRecycler = findViewById(R.id.user_recycler_view);
        userRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        userRecyclerView();
        setChatToolbar();
        if (count == 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    linearLayout.setVisibility(View.GONE);
                    drawerLayout.setVisibility(View.VISIBLE);
                    count++;
                }
            }, 5000);
        }
    }

    public void userRecyclerView() {
        List<UserModels> userModelsList = new ArrayList<>();
        DatabaseReference userDataReference = database.getReference().child("friends").child(auth.getUid());
        userDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String friendUserUID = dataSnapshot.getValue().toString();
                    DatabaseReference detailsReference = database.getReference().child("Users").child(friendUserUID);
                    detailsReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserModels userModels = new UserModels();
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                switch (dataSnapshot1.getKey()) {
                                    case "email": {
                                        userModels.setEmail(dataSnapshot1.getValue().toString());
                                        break;
                                    }
                                    case "firstName": {
                                        userModels.setFirstName(dataSnapshot1.getValue().toString());
                                        break;
                                    }
                                    case "lastName": {
                                        userModels.setLastName(dataSnapshot1.getValue().toString());
                                        break;
                                    }
                                    case "username": {
                                        userModels.setUsername(dataSnapshot1.getValue().toString());
                                        break;
                                    }
                                    case "password": {
                                        userModels.setPassword(dataSnapshot1.getValue().toString());
                                        break;
                                    }
                                    case "userUid": {
                                        userModels.setUserUid(dataSnapshot1.getValue().toString());
                                        break;
                                    }
                                }
                            }
                            if (!userModelsList.contains(userModels)) {
                                userModelsList.add(userModels);
                            }
                            usersAdapter = new UsersAdapter(getBaseContext(), userModelsList, drawerLayout, chatToolbar, send, message, chatRecycler, auth);
                            userRecycler.setAdapter(usersAdapter);
                            usersAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setChatToolbar() {

        ImageView menu = toolbar.findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseDrawer();
            }
        });
        chatToolbar.addView(toolbar);
    }

    public void CloseDrawer() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void addNewFriend(View view) {
        AlertDialog builder = new AlertDialog.Builder(this).create();
        View view2 = getLayoutInflater().inflate(R.layout.alert_dialog_view, null);
        builder.setView(view2);
        Button add = view2.findViewById(R.id.add_button);
        Button cancle = view2.findViewById(R.id.cancel_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = view2.findViewById(R.id.username);
                String searchedUsername = username.getText().toString();
                DatabaseReference addFriendReference = database.getReference().child("usernames");
                addFriendReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            if (searchedUsername.equals(dataSnapshot.getKey()) && !dataSnapshot.getValue().toString().equals(auth.getUid())) {
                                DatabaseReference reference = database.getReference().child("friends").child(auth.getUid().toString()).child(searchedUsername);
                                reference.setValue(dataSnapshot.getValue().toString());
                                Toast.makeText(getBaseContext(), "friend added successfully", Toast.LENGTH_SHORT).show();
                                userRecyclerView();
                                count++;
                                builder.dismiss();
                            }
                        }
                        if (count == 0) {
                            Toast.makeText(getBaseContext(), "Username doesn't exits", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    public void openSettings(View view)
    {
        Intent intent = new Intent(this,UserSetting.class);
        startActivity(intent);
    }
}














