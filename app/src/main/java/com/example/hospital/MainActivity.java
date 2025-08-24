package com.example.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.example.hospital.bottomnav.mainWindow.MainWindowFragment;
import com.example.hospital.bottomnav.medicalCard.MedicalCardFragment;
import com.example.hospital.bottomnav.profile.ProfileFragment;
import com.example.hospital.bottomnavDoctor.mainDoctorWindow.MainDoctorWindowFragment;
import com.example.hospital.bottomnavDoctor.profileDoctor.ProfileDoctorFragment;
import com.example.hospital.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), new MainWindowFragment()).commit();
        binding.bottomNav.setSelectedItemId(R.id.main_window);

        Map<Integer, Fragment> fragmentMap = new HashMap<>();
        fragmentMap.put(R.id.main_window, new MainWindowFragment());
        fragmentMap.put(R.id.medical_card, new MedicalCardFragment());
        fragmentMap.put(R.id.profile, new ProfileFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = fragmentMap.get(item.getItemId());
            getSupportFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(), fragment).commit();
            return true;
        });

    }
}