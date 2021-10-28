package com.moksh.securemessenger.LoginActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.moksh.securemessenger.ChatActivity;
import com.moksh.securemessenger.R;
import com.moksh.securemessenger.databinding.ActivitySigninBinding;

import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity {

    ActivitySigninBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null)
        {
            Intent intent = new Intent(this,ChatActivity.class);
            startActivity(intent);
        }

    }

    public boolean checkError() {
        int count = 0;
        if (binding.password.getText().toString().isEmpty() || binding.password.getText().toString().length() < 6) {
            binding.passwordError.setVisibility(View.VISIBLE);
            count++;
        } else {
            binding.passwordError.setVisibility(View.GONE);
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

    //    Method to launch Sign Up Activity
    public void launchSignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }

    //    method to launch chat Activity
    public void verify(View view) {
        if (checkError()) {
            auth.signInWithEmailAndPassword(binding.email.getText().toString(),binding.password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Intent intent = new Intent(getBaseContext(),ChatActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}