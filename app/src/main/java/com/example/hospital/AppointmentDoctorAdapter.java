package com.example.hospital;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hospital.bottomnav.medicalCard.Appointment;
import com.example.hospital.bottomnav.medicalCard.AppointmentAdapter;

import java.util.List;

public class AppointmentDoctorAdapter extends RecyclerView.Adapter<AppointmentDoctorAdapter.ViewHolder> {

    private List<AppointmentDoctor> appointmentList;

    public AppointmentDoctorAdapter(List<AppointmentDoctor> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentDoctorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_doc, parent, false);
        return new AppointmentDoctorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentDoctorAdapter.ViewHolder holder, int position) {
        AppointmentDoctor appointment = appointmentList.get(position);
        holder.tvUserName.setText("Doctor: " + appointment.getUserName());
        holder.tvDate.setText("Date: " + appointment.getDate());
        holder.tvTime.setText("Time: " + appointment.getTime());
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
