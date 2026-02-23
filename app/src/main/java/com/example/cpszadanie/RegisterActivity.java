package com.example.cpszadanie;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {

    private EditText etFio, etLogin, etPassword,
            etConfirmPassword, etPhone, etEmail;
    private Spinner spinnerPosition;
    private Spinner spinnerDepartment;
    private Button btnRegister, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);

        TextView title = new TextView(this);
        title.setText("Регистрация сотрудника");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);

        // ===== ФИО =====
        TextView fioLabel = new TextView(this);
        fioLabel.setText("ФИО");
        fioLabel.setTextSize(14);
        fioLabel.setPadding(0, 10, 0, 5);
        mainLayout.addView(fioLabel);

        etFio = new EditText(this);
        etFio.setHint("Иванов Иван Иванович");
        etFio.setTextSize(16);
        etFio.setPadding(20, 15, 20, 15);
        etFio.setBackgroundResource(android.R.drawable.editbox_background);
        etFio.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        mainLayout.addView(etFio);

        // ===== ДОЛЖНОСТЬ =====
        TextView positionLabel = new TextView(this);
        positionLabel.setText("Должность");
        positionLabel.setTextSize(14);
        positionLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(positionLabel);

        spinnerPosition = new Spinner(this);

        ArrayAdapter<String> positionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Constants.POSITIONS  // Используем константы
        );
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPosition.setAdapter(positionAdapter);

        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.setMargins(0, 0, 0, 10);
        spinnerPosition.setLayoutParams(spinnerParams);
        spinnerPosition.setBackgroundResource(android.R.drawable.editbox_background);
        spinnerPosition.setPadding(20, 15, 20, 15);
        mainLayout.addView(spinnerPosition);

        // ===== ОТДЕЛ =====
        TextView departmentLabel = new TextView(this);
        departmentLabel.setText("Отдел");
        departmentLabel.setTextSize(14);
        departmentLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(departmentLabel);

        spinnerDepartment = new Spinner(this);

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Constants.DEPARTMENTS  // Используем константы
        );
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(departmentAdapter);

        LinearLayout.LayoutParams spinnerDeptParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerDeptParams.setMargins(0, 0, 0, 10);
        spinnerDepartment.setLayoutParams(spinnerDeptParams);
        spinnerDepartment.setBackgroundResource(android.R.drawable.editbox_background);
        spinnerDepartment.setPadding(20, 15, 20, 15);
        mainLayout.addView(spinnerDepartment);

        // ===== ЛОГИН =====
        TextView loginLabel = new TextView(this);
        loginLabel.setText("Логин");
        loginLabel.setTextSize(14);
        loginLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(loginLabel);

        etLogin = new EditText(this);
        etLogin.setHint("Логин");
        etLogin.setTextSize(16);
        etLogin.setPadding(20, 15, 20, 15);
        etLogin.setBackgroundResource(android.R.drawable.editbox_background);
        etLogin.setInputType(InputType.TYPE_CLASS_TEXT);
        mainLayout.addView(etLogin);

        // ===== ПАРОЛЬ =====
        TextView passwordLabel = new TextView(this);
        passwordLabel.setText("Пароль");
        passwordLabel.setTextSize(14);
        passwordLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(passwordLabel);

        etPassword = new EditText(this);
        etPassword.setHint("••••••••");
        etPassword.setTextSize(16);
        etPassword.setPadding(20, 15, 20, 15);
        etPassword.setBackgroundResource(android.R.drawable.editbox_background);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mainLayout.addView(etPassword);

        // ===== ПОДТВЕРЖДЕНИЕ ПАРОЛЯ =====
        TextView confirmLabel = new TextView(this);
        confirmLabel.setText("Подтверждение пароля");
        confirmLabel.setTextSize(14);
        confirmLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(confirmLabel);

        etConfirmPassword = new EditText(this);
        etConfirmPassword.setHint("••••••••");
        etConfirmPassword.setTextSize(16);
        etConfirmPassword.setPadding(20, 15, 20, 15);
        etConfirmPassword.setBackgroundResource(android.R.drawable.editbox_background);
        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mainLayout.addView(etConfirmPassword);

        // ===== ТЕЛЕФОН =====
        TextView phoneLabel = new TextView(this);
        phoneLabel.setText("Телефон");
        phoneLabel.setTextSize(14);
        phoneLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(phoneLabel);

        etPhone = new EditText(this);
        etPhone.setHint("+7XXXXXXXXXX");
        etPhone.setTextSize(16);
        etPhone.setPadding(20, 15, 20, 15);
        etPhone.setBackgroundResource(android.R.drawable.editbox_background);
        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        etPhone.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;
            private int maxDigits = 11;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                String text = s.toString();
                if (text.isEmpty()) return;

                int digitCount = 0;
                for (int i = 0; i < text.length(); i++) {
                    if (Character.isDigit(text.charAt(i))) {
                        digitCount++;
                    }
                }

                if (digitCount > maxDigits) {
                    isUpdating = true;

                    StringBuilder newText = new StringBuilder();
                    int digitsAdded = 0;

                    if (text.startsWith("+")) {
                        newText.append("+");
                    }

                    for (int i = text.startsWith("+") ? 1 : 0; i < text.length(); i++) {
                        char c = text.charAt(i);
                        if (Character.isDigit(c) && digitsAdded < maxDigits) {
                            newText.append(c);
                            digitsAdded++;
                        }
                    }

                    etPhone.setText(newText.toString());
                    etPhone.setSelection(etPhone.length());
                    isUpdating = false;
                    return;
                }

                if (!text.startsWith("+") && digitCount > 0) {
                    isUpdating = true;
                    etPhone.setText("+" + text);
                    etPhone.setSelection(etPhone.length());
                    isUpdating = false;
                }
            }
        });
        mainLayout.addView(etPhone);

        // ===== EMAIL =====
        TextView emailLabel = new TextView(this);
        emailLabel.setText("Email");
        emailLabel.setTextSize(14);
        emailLabel.setPadding(0, 20, 0, 5);
        mainLayout.addView(emailLabel);

        etEmail = new EditText(this);
        etEmail.setHint("ivanov@mail.ru");
        etEmail.setTextSize(16);
        etEmail.setPadding(20, 15, 20, 15);
        etEmail.setBackgroundResource(android.R.drawable.editbox_background);
        etEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mainLayout.addView(etEmail);

        // ===== КНОПКИ =====
        btnRegister = new Button(this);
        btnRegister.setText("ЗАРЕГИСТРИРОВАТЬСЯ");
        btnRegister.setTextSize(16);
        btnRegister.setPadding(20, 15, 20, 15);
        btnRegister.setPadding(0, 30, 0, 10);
        mainLayout.addView(btnRegister);

        btnBack = new Button(this);
        btnBack.setText("Назад");
        btnBack.setTextSize(16);
        btnBack.setPadding(20, 15, 20, 15);
        mainLayout.addView(btnBack);

        setContentView(mainLayout);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser() {
        if (etFio.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите ФИО", Toast.LENGTH_SHORT).show();
            etFio.requestFocus();
            return;
        }

        if (etLogin.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите логин", Toast.LENGTH_SHORT).show();
            etLogin.requestFocus();
            return;
        }

        if (etPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        if (etConfirmPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Подтвердите пароль", Toast.LENGTH_SHORT).show();
            etConfirmPassword.requestFocus();
            return;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
            etPhone.requestFocus();
            return;
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
            return;
        }

        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        }

        String phone = etPhone.getText().toString().trim();
        if (!isValidPhone(phone)) {
            Toast.makeText(this, "Телефон должен начинаться с + и содержать 11 цифр", Toast.LENGTH_LONG).show();
            etPhone.requestFocus();
            return;
        }

        String email = etEmail.getText().toString().trim();
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Введите корректный email (пример: name@domain.ru)", Toast.LENGTH_LONG).show();
            etEmail.requestFocus();
            return;
        }

        List<User> users = JsonHelper.loadUsers(this);

        for (User user : users) {
            if (user.getLogin().equals(etLogin.getText().toString())) {
                Toast.makeText(this, "Логин уже занят", Toast.LENGTH_SHORT).show();
                etLogin.requestFocus();
                return;
            }
        }

        String selectedPosition = spinnerPosition.getSelectedItem().toString();
        String selectedDepartment = spinnerDepartment.getSelectedItem().toString();

        User newUser = new User(
                JsonHelper.getNextUserId(this),
                etFio.getText().toString(),
                selectedPosition,
                selectedDepartment,
                0,
                etLogin.getText().toString(),
                etPassword.getText().toString(),
                phone,
                email
        );

        users.add(newUser);
        JsonHelper.saveUsers(this, users);

        Toast.makeText(this, "✅ Регистрация успешна! Теперь можно войти", Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean isValidPhone(String phone) {
        if (!phone.startsWith("+")) {
            return false;
        }

        String digits = phone.substring(1);
        int digitCount = 0;
        for (int i = 0; i < digits.length(); i++) {
            if (Character.isDigit(digits.charAt(i))) {
                digitCount++;
            }
        }

        return digitCount == 11;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Pattern.matches(emailPattern, email);
    }
}