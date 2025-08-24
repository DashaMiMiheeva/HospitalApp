package com.example.hospital.bottomnavDoctor.mainDoctorWindow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.example.hospital.LoginActivity;
import com.example.hospital.RegisterActivity;
import com.example.hospital.WebsiteActivity;
import com.example.hospital.WorkDaysActivity;
import com.example.hospital.databinding.FragmentMainWindowDoctorBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainDoctorWindowFragment extends Fragment {
    private FragmentMainWindowDoctorBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference doctorsAvailabilityRef;
    private TextView welcome_tv;
    private DatabaseReference doctorUid;
    private String doctorName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainWindowDoctorBinding.inflate(inflater, container, false);

        FirebaseDatabase.getInstance().getReference().child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue().toString();
                String welcome = "Welcome, " + username + "!";
                binding.welcomeTv.setText(welcome);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.websiteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), WebsiteActivity.class));
            }
        });

        binding.calendarTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WorkDaysActivity.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        doctorsAvailabilityRef = database.getReference().child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("availability");

        binding.scheduleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        return binding.getRoot();
    }

    private void showDatePickerDialog() {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startDate = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long endDate = calendar.getTimeInMillis();
        builder.setSelection(new Pair<>(startDate, endDate));

        final MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.show(getParentFragmentManager(), picker.toString());

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                if (selection != null) {
                    ArrayList<String> selectedDates = new ArrayList<>();
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

                    long start = selection.first;
                    long end = selection.second;
                    for (long date = start; date <= end; date += 24 * 60 * 60 * 1000) {
                        cal.setTimeInMillis(date);
                        selectedDates.add(sdf.format(new Date(date)));
                    }
                    addSelectedDatesToFirebase(selectedDates);
                }
            }
        });
    }
    private void addSelectedDatesToFirebase(ArrayList<String> selectedDates) {
        doctorsAvailabilityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> existingDates = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        existingDates.add(dateSnapshot.getValue(String.class));
                    }
                }
                existingDates.addAll(selectedDates);
                Map<String, Object> updates = new HashMap<>();
                for (int i = 0; i < existingDates.size(); i++) {
                    updates.put(String.valueOf(i), existingDates.get(i));
                }
                doctorsAvailabilityRef.updateChildren(updates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
