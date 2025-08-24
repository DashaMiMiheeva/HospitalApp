package com.example.hospital.bottomnavDoctor.profileDoctor;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.hospital.LoginActivity;
import com.example.hospital.R;
import com.example.hospital.databinding.FragmentMainWindowDoctorBinding;
import com.example.hospital.databinding.FragmentProfileDoctorBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileDoctorFragment extends Fragment {
    private Uri filePath;
    private TextView specialization_tv;
    private FragmentProfileDoctorBinding binding;
    String[] specialties = {"Pediatrician", "Surgeon", "Ophthalmologist", "Allergist", "Psychiatrist"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileDoctorBinding.inflate(inflater, container, false);

        loadDoctorInfo();

        binding.profileImageViewDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.logoutBtnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("userType");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        specialization_tv = binding.specializationTvDoctor;
        binding.addSpecializationIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpecializationDialog();
            }
        });

        binding.infoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppInfoDialog();

            }
        });

        return binding.getRoot();
    }

    private void showAppInfoDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.about_info_dialog);
        dialog.show();
    }

    private void showSpecializationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_specialization_dialog, null);
        builder.setView(dialogView);

        Spinner specialtySpinner = dialogView.findViewById(R.id.specialty_spinner);
        Button okButton = dialogView.findViewById(R.id.ok_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, specialties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        specialtySpinner.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSpecialty = (String) specialtySpinner.getSelectedItem();
                specialization_tv.setText(selectedSpecialty);
                FirebaseDatabase.getInstance().getReference().child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("specialization").setValue(selectedSpecialty);
                dialog.dismiss();
            }
        });
    }
    private void loadDoctorInfo(){
        FirebaseDatabase.getInstance().getReference().child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue().toString();
                String profileImage = snapshot.child("profileImage").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String specialization = snapshot.child("specialization").getValue().toString();

                binding.usernameTvDoctor.setText(username);
                binding.emailTvDoctor.setText(email);
                binding.specializationTvDoctor.setText(specialization);
                if (!profileImage.isEmpty()){
                    Glide.with(getContext()).load(profileImage).into(binding.profileImageViewDoctor);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!= null){
                filePath = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), filePath);
                    binding.profileImageViewDoctor.setImageBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }
                uploadImage();
            }

        }
    });
    private void uploadImage(){
        if (filePath != null){
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseStorage.getInstance().getReference().child("images/" + uid).putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Photo upload complete", Toast.LENGTH_SHORT).show();
                    FirebaseStorage.getInstance().getReference().child("images/" + uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseDatabase.getInstance().getReference().child("Doctors").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").setValue(uri.toString());
                        }
                    });

                }
            });
        }
    }
}
