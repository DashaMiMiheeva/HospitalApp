package com.example.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hospital.databinding.ActivityHospitalScheduleBinding;
import com.example.hospital.databinding.ActivityMainBinding;
import com.example.hospital.databinding.FragmentMainWindowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.widget.TextView;

public class HospitalScheduleActivity extends AppCompatActivity {
    private ActivityHospitalScheduleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHospitalScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String medical_polis = snapshot.child("medical polis").getValue().toString();
                String str = "Medical polis №" + medical_polis;
                binding.polis.setText(str);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}