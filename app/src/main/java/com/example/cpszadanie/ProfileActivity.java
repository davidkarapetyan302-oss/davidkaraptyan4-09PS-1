package com.example.cpszadanie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvFio, tvPosition, tvDepartment, tvLogin, tvPhone, tvEmail;
    private Button btnBack;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        currentUser = JsonHelper.getCurrentUser(this);
        if (currentUser == null) {
            finish();
            return;
        }

        initViews();
        displayUserInfo();
        setupListeners();
    }

    private void initViews() {
        tvFio = findViewById(R.id.tvProfileFio);
        tvPosition = findViewById(R.id.tvProfilePosition);
        tvDepartment = findViewById(R.id.tvProfileDepartment);
        tvLogin = findViewById(R.id.tvProfileLogin);
        tvPhone = findViewById(R.id.tvProfilePhone);
        tvEmail = findViewById(R.id.tvProfileEmail);
        btnBack = findViewById(R.id.btnBack);
    }

    private void displayUserInfo() {
        tvFio.setText(currentUser.getFio());
        tvPosition.setText(currentUser.getPosition());
        tvDepartment.setText(currentUser.getDepartment());
        tvLogin.setText(currentUser.getLogin());
        tvPhone.setText(currentUser.getPhone());
        tvEmail.setText(currentUser.getEmail());
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}