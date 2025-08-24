package com.example.hospital.bottomnav.medicalCard;

public class Appointment {
        private String doctorName;
        private String date;
        private String time;

        public Appointment() {
        }

        public Appointment(String doctorName, String date, String time) {
            this.doctorName = doctorName;
            this.date = date;
            this.time = time;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
}
