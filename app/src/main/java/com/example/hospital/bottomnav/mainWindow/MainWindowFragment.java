package com.example.hospital.bottomnav.mainWindow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hospital.HospitalScheduleActivity;
import com.example.hospital.LoginActivity;
import com.example.hospital.MainActivity;
import com.example.hospital.R;
import com.example.hospital.databinding.FragmentMainWindowBinding;
import com.example.hospital.HospitalTimetableFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainWindowFragment extends Fragment {
    private FragmentMainWindowBinding binding;
    private Calendar calendar;
    private static final String API_KEY = "bcfc87cbb83110fbbe4e0214d3a9d46d";
    private static final String CITY = "Moscow";
    private static final String URL = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY;
    private static final String NASA_API_KEY = "tgpdjqgE4HIvTCeCGEVxtDqzuXDLtOL0wPxGUtha";
    private static final String NASA_URL = "https://api.nasa.gov/DONKI/GST?location=moscow&api_key=" + NASA_API_KEY;

    private TextView pressureValue;
    private TextView geomagneticStormValue;
    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainWindowBinding.inflate(inflater, container, false);
        pressureValue = binding.pressureValue;
        geomagneticStormValue = binding.geomagneticStormValue;
        new FetchGeomagneticStormTask().execute();
        new FetchWeatherTask().execute();
        calendar = Calendar.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Doctors");

        binding.contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContactsDialog();
            }
        });

        binding.recordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.hospitalScheduleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HospitalScheduleActivity.class));
            }
        });

        binding.infoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppInfoDialog();

            }
        });

        binding.recordTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpecializationsDialog();
            }
        });
        return binding.getRoot();
    }
    private void showSpecializationsDialog() {
        final String[] specializations = {"Pediatrician", "Surgeon", "Ophthalmologist", "Allergist", "Psychiatrist"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Specialization")
                .setItems(specializations, (dialog, which) -> {
                    String selectedSpecialization = specializations[which];
                    showDoctorsDialog(selectedSpecialization);
                });
        builder.create().show();
    }
    private void showDoctorsDialog(String specialization) {
        databaseReference.orderByChild("specialization").equalTo(specialization).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> doctorNames = new ArrayList<>();
                Map<String, String> doctorIdMap = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String doctorId = snapshot.getKey();
                    String doctorName = snapshot.child("username").getValue(String.class);
                    if (doctorName != null) {
                        doctorNames.add(doctorName);
                        doctorIdMap.put(doctorName, doctorId);
                    }
                }
                displayDoctorsList(doctorNames, doctorIdMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
    private void displayDoctorsList(List<String> doctors, Map<String, String> doctorIdMap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Doctors")
                .setItems(doctors.toArray(new String[0]), (dialog, which) -> {
                    String selectedDoctorName = doctors.get(which);
                    String selectedDoctorId = doctorIdMap.get(selectedDoctorName);
                    loadDoctorAvailability(selectedDoctorId, selectedDoctorName);
                })
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    private void loadDoctorAvailability(String doctorId, String selectedDoctorName) {
        databaseReference.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> availability = new HashMap<>();
                Map<String, List<String>> bookedSlots = new HashMap<>();

                if (dataSnapshot.hasChild("availability")) {
                    for (DataSnapshot snapshot : dataSnapshot.child("availability").getChildren()) {
                        String day = snapshot.getKey();
                        String hours = snapshot.getValue(String.class);
                        if (day != null && hours != null) {
                            availability.put(day, hours);
                        }
                    }
                }

                if (dataSnapshot.hasChild("Appointments")) {
                    for (DataSnapshot snapshot : dataSnapshot.child("Appointments").getChildren()) {
                        String day = snapshot.child("date").getValue(String.class);
                        String time = snapshot.child("time").getValue(String.class);
                        if (day != null && time != null) {
                            if (!bookedSlots.containsKey(day)) {
                                bookedSlots.put(day, new ArrayList<>());
                            }
                            bookedSlots.get(day).add(time);
                        }
                    }
                }

                displayAvailability(availability, bookedSlots, doctorId, selectedDoctorName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void displayAvailability(Map<String, String> availability, Map<String, List<String>> bookedSlots, String doctorId, String doctorName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = getLayoutInflater().inflate(R.layout.dialog_availability, null);
        LinearLayout availabilityContainer = view.findViewById(R.id.availabilityContainer);

        for (Map.Entry<String, String> entry : availability.entrySet()) {
            String day = entry.getValue();

            Button dayButton = new Button(getContext());
            dayButton.setText(day);
            dayButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            dayButton.setOnClickListener(v -> showTimeSelectionDialog(day, bookedSlots.get(day), doctorId, doctorName));
            availabilityContainer.addView(dayButton);
        }

        builder.setView(view);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showTimeSelectionDialog(String day, List<String> bookedSlots, String doctorId, String doctorName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = getLayoutInflater().inflate(R.layout.dialog_time_selection, null);
        LinearLayout timeContainer = view.findViewById(R.id.timeContainer);

        String[] times = generateTimeSlots("10:00", "21:00", 30);
        for (String time : times) {
            Button timeButton = new Button(getContext());
            timeButton.setText(time);
            timeButton.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            if (bookedSlots != null && bookedSlots.contains(time)) {
                timeButton.setBackgroundColor(Color.RED);
                timeButton.setEnabled(false);
            } else {
                timeButton.setOnClickListener(v -> showConfirmationDialog(day, time, doctorId, doctorName));
            }
            timeContainer.addView(timeButton);
        }

        builder.setView(view);
        builder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    private void showConfirmationDialog(String day, String time, String doctorId, String doctorName) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Appointment")
                .setMessage("Are you sure you want to book an appointment on " + day + " at " + time + "?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    Map<String, String> appointment = new HashMap<>();
                    appointment.put("doctorName", doctorName);
                    appointment.put("date", day);
                    appointment.put("time", time);

                    Map<String, String> doc_appointment = new HashMap<>();
                    doc_appointment.put("date", day);
                    doc_appointment.put("time", time);

                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String username = snapshot.child("username").getValue().toString();
                            doc_appointment.put("userName", username);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    DatabaseReference userAppointmentsRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Appointments");

                    userAppointmentsRef.push().setValue(appointment)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DatabaseReference doctorAppointmentsRef = databaseReference.child(doctorId).child("Appointments");
                                    doctorAppointmentsRef.push().setValue(doc_appointment)
                                            .addOnCompleteListener(doctorTask -> {
                                                if (doctorTask.isSuccessful()) {
                                                    new AlertDialog.Builder(getContext())
                                                            .setTitle("Appointment Confirmed")
                                                            .setMessage("Your appointment with Dr. " + doctorName + " on " + day + " at " + time + " has been booked successfully.")
                                                            .setPositiveButton("OK", null)
                                                            .show();
                                                } else {
                                                    new AlertDialog.Builder(getContext())
                                                            .setTitle("Booking Failed")
                                                            .setMessage("There was an error booking your appointment. Please try again.")
                                                            .setPositiveButton("OK", null)
                                                            .show();
                                                }
                                            });
                                }else {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Booking Failed")
                                            .setMessage("There was an error booking your appointment. Please try again.")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            });

                })
                .setNegativeButton("No", null)
                .show();
    }

    private String[] generateTimeSlots(String startTime, String endTime, int interval) {
        List<String> timeSlots = new ArrayList<>();
        int startHour = Integer.parseInt(startTime.split(":")[0]);
        int startMinute = Integer.parseInt(startTime.split(":")[1]);
        int endHour = Integer.parseInt(endTime.split(":")[0]);
        int endMinute = Integer.parseInt(endTime.split(":")[1]);

        while (startHour < endHour || (startHour == endHour && startMinute < endMinute)) {
            timeSlots.add(String.format("%02d:%02d", startHour, startMinute));
            startMinute += interval;
            if (startMinute >= 60) {
                startMinute -= 60;
                startHour++;
            }
        }

        return timeSlots.toArray(new String[0]);
    }
    private void showAppInfoDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.about_info_dialog);
        dialog.show();
    }

    private class FetchWeatherTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(URL)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
                    JsonObject main = jsonObject.getAsJsonObject("main");
                    double pressureHpa = main.get("pressure").getAsDouble();
                    double pressureMmHg = pressureHpa * 0.75006;
                    return String.format("%.2f мм рт. ст.", pressureMmHg);
                }else {
                    Log.e("FetchWeatherTask", "Response code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                pressureValue.setText(result);
            } else {
                //Toast.makeText(getActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class FetchGeomagneticStormTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(NASA_URL)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonData = response.body().string();
                    Log.d("FetchGeomagneticStormTask", "Response data: " + jsonData); // Логирование полного ответа

                    JsonArray jsonArray = JsonParser.parseString(jsonData).getAsJsonArray();
                    if (jsonArray.size() > 0) {
                        JsonObject stormData = jsonArray.get(0).getAsJsonObject();
                        JsonArray kpIndexArray = stormData.getAsJsonArray("allKpIndex");
                        if (kpIndexArray.size() > 0) {
                            JsonObject firstKpIndex = kpIndexArray.get(0).getAsJsonObject();
                            double kpIndex = firstKpIndex.get("kpIndex").getAsDouble();
                            return "K-index: " + kpIndex;
                        } else {
                            return "K-index data not available";
                        }
                    } else {
                        return "No geomagnetic storm data available";
                    }
                } else {
                    Log.e("FetchGeomagneticStormTask", "Response code: " + response.code());
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                geomagneticStormValue.setText(result);
            } else {
                Toast.makeText(getActivity(), "Failed to fetch geomagnetic storm data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showContactsDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_contacts_dialog);

        MaterialButton buttonSubmit = dialog.findViewById(R.id.save_contacts);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
