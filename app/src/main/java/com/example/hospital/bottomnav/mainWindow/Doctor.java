package com.example.hospital.bottomnav.mainWindow;

public class Doctor {
    private String id;
    private String name;
    private String specialty;

    public Doctor() {
        // Default constructor required for calls to DataSnapshot.getValue(Doctor.class)
    }

    public Doctor(String id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }
}
