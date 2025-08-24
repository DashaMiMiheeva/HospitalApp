package com.example.hospital.bottomnav.medicalCard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospital.databinding.FragmentMainWindowBinding;
import com.example.hospital.databinding.FragmentMedicalCardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MedicalCardFragment extends Fragment {
    private FragmentMedicalCardBinding binding;
    private RecyclerView rvAppointments;
    private TextView tvNoRecords;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMedicalCardBinding.inflate(inflater, container, false);

        rvAppointments = binding.rvAppointments;
        tvNoRecords = binding.tvNoRecords;

        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAppointments.addItemDecoration(new SpacingItemDecoration(16));appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(appointmentList);
        rvAppointments.setAdapter(appointmentAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Appointments");

        loadAppointments();
        return binding.getRoot();
    }

    private void loadAppointments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Appointment appointment = snapshot.getValue(Appointment.class);
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
