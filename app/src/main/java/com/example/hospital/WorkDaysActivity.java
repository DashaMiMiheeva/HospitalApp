package com.example.hospital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hospital.bottomnav.medicalCard.Appointment;
import com.example.hospital.bottomnav.medicalCard.AppointmentAdapter;
import com.example.hospital.bottomnav.medicalCard.SpacingItemDecoration;
import com.example.hospital.databinding.ActivityWorkDaysBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WorkDaysActivity extends AppCompatActivity {
    private ActivityWorkDaysBinding binding;
    private RecyclerView rvAppointments;
    private TextView tvNoRecords;
    private AppointmentDoctorAdapter appointmentAdapter;
    private List<AppointmentDoctor> appointmentList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkDaysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rvAppointments = binding.rvAppointments;
        tvNoRecords = binding.tvNoRecords;

        rvAppointments.setLayoutManager(new LinearLayoutManager(WorkDaysActivity.this));
        rvAppointments.addItemDecoration(new SpacingItemDecoration(16));appointmentList = new ArrayList<>();
        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentDoctorAdapter(appointmentList);
        rvAppointments.setAdapter(appointmentAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Appointments");

        loadAppointments();
    }

    private void loadAppointments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AppointmentDoctor appointment = snapshot.getValue(AppointmentDoctor.class);
                        appointmentList.add(appointment);
                    }
                    appointmentAdapter.notifyDataSetChanged();
                    rvAppointments.setVisibility(View.VISIBLE);
                    tvNoRecords.setVisibility(View.GONE);
                }else{
                    rvAppointments.setVisibility(View.GONE);
                    tvNoRecords.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}