package com.example.hospital;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkDaysAdapter extends RecyclerView.Adapter<WorkDaysAdapter.ViewHolder> {

    private ArrayList<String> workDaysList;

    public WorkDaysAdapter(ArrayList<String> workDaysList) {
        this.workDaysList = workDaysList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_work_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String workDay = workDaysList.get(position);
        holder.workDayTextView.setText(workDay);
    }

    @Override
    public int getItemCount() {
        return workDaysList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView workDayTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workDayTextView = itemView.findViewById(R.id.text_view_work_day);
        }
    }
}