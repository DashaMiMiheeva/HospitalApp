package com.example.hospital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.hospital.databinding.ActivityCheckBinding;

public class CheckActivity extends AppCompatActivity {
    private ActivityCheckBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String userType = sharedPreferences.getString("userType", "");
        if (userType.equals("doctor")) {
            Intent intent = new Intent(CheckActivity.this, MainDoctorActivity.class);
            startActivity(intent);
            finish();
        } else if (userType.equals("user")) {
            Intent intent = new Intent(CheckActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(CheckActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}