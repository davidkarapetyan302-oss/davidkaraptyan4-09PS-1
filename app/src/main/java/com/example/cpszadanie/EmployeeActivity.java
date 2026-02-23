package com.example.cpszadanie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmployeeActivity extends AppCompatActivity {

    private Button btnCheckIn, btnCheckOut, btnLogout;
    private LinearLayout btnHistory, btnProfile, btnStats;
    private TextView tvWelcome, tvEmployeeInfo, tvTodayHours, tvWeekHours, tvAvatar;
    private User currentUser;
    private List<TimeRecord> records;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = JsonHelper.getCurrentUser(this);
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_employee_modern);

        initViews();
        loadData();
        setupListeners();
        updateStats();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvEmployeeInfo = findViewById(R.id.tvEmployeeInfo);
        tvTodayHours = findViewById(R.id.tvTodayHours);
        tvWeekHours = findViewById(R.id.tvWeekHours);
        tvAvatar = findViewById(R.id.tvAvatar);

        btnCheckIn = findViewById(R.id.btnCheckIn);
        btnCheckOut = findViewById(R.id.btnCheckOut);
        btnHistory = findViewById(R.id.btnHistory);
        btnProfile = findViewById(R.id.btnProfile);
        btnStats = findViewById(R.id.btnStats);
        btnLogout = findViewById(R.id.btnLogout);


        tvWelcome.setText(currentUser.getFio());
        tvEmployeeInfo.setText(currentUser.getPosition() + " · " + currentUser.getDepartment());


        String[] nameParts = currentUser.getFio().split(" ");
        String initials = "";
        for (String part : nameParts) {
            if (!part.isEmpty()) initials += part.charAt(0);
        }
        tvAvatar.setText(initials);
    }

    private void loadData() {
        records = JsonHelper.loadTimeRecords(this);
    }

    private void setupListeners() {
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIn();
            }
        });

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOut();
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonHelper.logout(EmployeeActivity.this);
                startActivity(new Intent(EmployeeActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void checkIn() {
        String today = dateFormat.format(new Date());
        String now = timeFormat.format(new Date());

        for (TimeRecord record : records) {
            if (record.getUserId() == currentUser.getId() && record.getDate().equals(today)) {
                Toast.makeText(this, "❌ Вы уже отметили приход сегодня", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        TimeRecord newRecord = new TimeRecord(
                JsonHelper.getNextRecordId(this),
                currentUser.getId(),
                today,
                now,
                null,
                null
        );

        records.add(newRecord);
        JsonHelper.saveTimeRecords(this, records);

        Toast.makeText(this, "✅ Приход отмечен в " + now, Toast.LENGTH_SHORT).show();
        updateStats();
    }

    private void checkOut() {
        String today = dateFormat.format(new Date());
        String now = timeFormat.format(new Date());

        TimeRecord foundRecord = null;
        int foundIndex = -1;

        for (int i = 0; i < records.size(); i++) {
            TimeRecord record = records.get(i);
            if (record.getUserId() == currentUser.getId() && record.getDate().equals(today)) {
                foundRecord = record;
                foundIndex = i;
                break;
            }
        }

        if (foundRecord == null) {
            Toast.makeText(this, "❌ Сначала отметьте приход", Toast.LENGTH_SHORT).show();
            return;
        }

        if (foundRecord.getTimeOut() != null) {
            Toast.makeText(this, "❌ Вы уже отметили уход сегодня", Toast.LENGTH_SHORT).show();
            return;
        }

        double hours = calculateHours(foundRecord.getTimeIn(), now);

        TimeRecord updatedRecord = new TimeRecord(
                foundRecord.getId(),
                foundRecord.getUserId(),
                foundRecord.getDate(),
                foundRecord.getTimeIn(),
                now,
                hours
        );

        records.set(foundIndex, updatedRecord);
        JsonHelper.saveTimeRecords(this, records);

        Toast.makeText(this, "✅ Уход отмечен в " + now + "\nОтработано: " +
                String.format("%.1f", hours) + " ч", Toast.LENGTH_LONG).show();
        updateStats();
    }

    private double calculateHours(String timeIn, String timeOut) {
        String[] partsIn = timeIn.split(":");
        String[] partsOut = timeOut.split(":");

        int minutesIn = Integer.parseInt(partsIn[0]) * 60 + Integer.parseInt(partsIn[1]);
        int minutesOut = Integer.parseInt(partsOut[0]) * 60 + Integer.parseInt(partsOut[1]);

        return (minutesOut - minutesIn) / 60.0;
    }

    private void updateStats() {
        String today = dateFormat.format(new Date());

        double todayHours = 0;
        for (TimeRecord record : records) {
            if (record.getUserId() == currentUser.getId() && record.getDate().equals(today) &&
                    record.getHours() != null) {
                todayHours = record.getHours();
                break;
            }
        }
        tvTodayHours.setText(String.format("%.1f ч", todayHours));

        long weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;
        String weekAgoStr = dateFormat.format(new Date(weekAgo));

        double weekHours = 0;
        for (TimeRecord record : records) {
            if (record.getUserId() == currentUser.getId() &&
                    record.getDate().compareTo(weekAgoStr) >= 0 &&
                    record.getDate().compareTo(today) <= 0 &&
                    record.getHours() != null) {
                weekHours += record.getHours();
            }
        }
        tvWeekHours.setText(String.format("%.1f ч", weekHours));
    }

}