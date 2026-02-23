package com.example.cpszadanie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = etLogin.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (login.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(login, password);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser(String login, String password) {
        try {
            List<User> users = JsonHelper.loadUsers(this);

            if (users == null || users.isEmpty()) {
                Toast.makeText(this, "Ошибка: список пользователей пуст", Toast.LENGTH_LONG).show();
                return;
            }

            User foundUser = null;
            for (User user : users) {
                if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                    foundUser = user;
                    break;
                }
            }

            if (foundUser != null) {
                JsonHelper.saveCurrentUser(this, foundUser);

                String roleText = "";
                switch (foundUser.getRole()) {
                    case 0:
                        roleText = "Сотрудник";
                        break;
                    case 1:
                        roleText = "Руководитель";
                        break;
                    case 2:
                        roleText = "Табельщик";
                        break;
                }

                Toast.makeText(this, "Вход выполнен! Роль: " + roleText, Toast.LENGTH_SHORT).show();

                Intent intent;
                switch (foundUser.getRole()) {
                    case 0:
                        intent = new Intent(LoginActivity.this, EmployeeActivity.class);
                        break;
                    case 1:
                        intent = new Intent(LoginActivity.this, ManagerActivity.class);
                        break;
                    case 2:
                        intent = new Intent(LoginActivity.this, TimekeeperActivity.class);
                        break;
                    default:
                        intent = new Intent(LoginActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}