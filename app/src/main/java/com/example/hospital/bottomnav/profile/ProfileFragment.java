package com.example.hospital.bottomnav.profile;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.hospital.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Uri filePath;
    TextView birthday_tv;
    TextView gender_tv;
    TextView phone_tv;
    TextView address_tv;
    TextView polis_tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        loadUserInfo();

        binding.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
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

        birthday_tv = binding.birthdayTv;
        binding.addBirthdayIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        gender_tv = binding.genderTv;
        binding.addGenderIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderDialog();
            }
        });

        phone_tv = binding.phoneTv;
        binding.addPhoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneDialog();
            }
        });

        address_tv = binding.addressTv;
        binding.addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressDialog();
            }
        });

        polis_tv = binding.idTv;
        binding.addIdIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPolisDialog();
            }
        });

        return binding.getRoot();
    }
    private void showPolisDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_polis_dialog);

        EditText polisInput = dialog.findViewById(R.id.polis_et);
        MaterialButton buttonSubmit = dialog.findViewById(R.id.save_polis);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = polisInput.getText().toString();
                polis_tv.setText(inputText);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("medical polis").setValue(inputText);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showGenderDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_gender_dialog);

        EditText genderInput = dialog.findViewById(R.id.gender_et);
        MaterialButton buttonSubmit = dialog.findViewById(R.id.save_gender);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = genderInput.getText().toString();
                gender_tv.setText(inputText);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("gender").setValue(inputText);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showPhoneDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_phone_dialog);

        EditText phoneInput = dialog.findViewById(R.id.phone_et);
        MaterialButton buttonSubmit = dialog.findViewById(R.id.save_phone);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = phoneInput.getText().toString();
                phone_tv.setText(inputText);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("phone").setValue(inputText);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showAddressDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.custom_address_dialog);

        EditText addressInput = dialog.findViewById(R.id.address_et);
        MaterialButton buttonSubmit = dialog.findViewById(R.id.save_address);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = addressInput.getText().toString();
                address_tv.setText(inputText);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("address").setValue(inputText);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                birthday_tv.setText(selectedDate);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("birthday").setValue(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }


    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData()!=null && result.getData().getData()!= null){
                filePath = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), filePath);
                    binding.profileImageView.setImageBitmap(bitmap);
                }catch (IOException e){
                    e.printStackTrace();
                }
                uploadImage();
            }

        }
    });
    private void loadUserInfo(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue().toString();
                String profileImage = snapshot.child("profileImage").getValue().toString();
                String birthaday = snapshot.child("birthday").getValue().toString();
                String phoneNumber = snapshot.child("phone").getValue().toString();
                String gender = snapshot.child("gender").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String address = snapshot.child("address").getValue().toString();
                String medical_polis = snapshot.child("medical polis").getValue().toString();

                binding.usernameTv.setText(username);
                binding.birthdayTv.setText(birthaday);
                binding.idTv.setText(medical_polis);
                binding.phoneTv.setText(phoneNumber);
                binding.genderTv.setText(gender);
                binding.emailTv.setText(email);
                binding.addressTv.setText(address);
                if (!profileImage.isEmpty()){
                    Glide.with(getContext()).load(profileImage).into(binding.profileImageView);
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
                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("profileImage").setValue(uri.toString());
                        }
                    });

                    }
            });
        }
    }
}
