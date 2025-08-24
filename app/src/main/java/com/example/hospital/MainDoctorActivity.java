package com.example.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.hospital.bottomnav.mainWindow.MainWindowFragment;
import com.example.hospital.bottomnav.medicalCard.MedicalCardFragment;
import com.example.hospital.bottomnav.profile.ProfileFragment;
import com.example.hospital.bottomnavDoctor.mainDoctorWindow.MainDoctorWindowFragment;
import com.example.hospital.bottomnavDoctor.profileDoctor.ProfileDoctorFragment;
import com.example.hospital.databinding.ActivityMainDoctorBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainDoctorActivity extends AppCompatActivity {
    private ActivityMainDoctorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainDoctorActivity.this, LoginActivity.class));
        }

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainerDoctor.getId(), new MainDoctorWindowFragment()).commit();
        binding.bottomNavDoctor.setSelectedItemId(R.id.main_window_doctor);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.main_window_doctor, new MainDoctorWindowFragment());
        fragmentMap.put(R.id.profile_doctor, new ProfileDoctorFragment());

        binding.bottomNavDoctor.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainerDoctor.getId(), fragment).commit();
            return true;
        });
    }
}