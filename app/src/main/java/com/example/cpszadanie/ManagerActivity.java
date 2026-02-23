package com.example.cpszadanie;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ManagerActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private User currentUser;
    private List<User> allUsers;
    private List<User> departmentEmployees;
    private List<TimeRecord> allRecords;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        currentUser = JsonHelper.getCurrentUser(this);
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        allUsers = JsonHelper.loadUsers(this);
        allRecords = JsonHelper.loadTimeRecords(this);


        departmentEmployees = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getDepartment().equals(currentUser.getDepartment()) && user.getRole() == 0) {
                departmentEmployees.add(user);
            }
        }

        createMainScreen();
    }

    private void createMainScreen() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 24, 24, 24);


        LinearLayout headerLayout = createHeader();
        mainLayout.addView(headerLayout);


        addDepartmentStats();


        addActionButton("📋 Сотрудники отдела", "Просмотр списка сотрудников", 0xFF6366F1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartmentEmployees();
            }
        });

        addActionButton("📊 Отчеты по отделу", "Статистика и отчеты", 0xFF10B981, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDepartmentReports();
            }
        });

        addActionButton("⏱ Отметки сотрудников", "Просмотр отметок за период", 0xFFF59E0B, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmployeeRecords();
            }
        });

        addActionButton("📈 Аналитика", "Опоздания и дисциплина", 0xFF8B5CF6, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnalytics();
            }
        });


        Button logoutBtn = createLogoutButton();
        mainLayout.addView(logoutBtn);

        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }

    private LinearLayout createHeader() {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(24, 24, 24, 24);
        headerLayout.setBackgroundResource(R.drawable.gradient_background);

        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerParams.setMargins(0, 0, 0, 24);
        headerLayout.setLayoutParams(headerParams);

        TextView welcomeText = new TextView(this);
        welcomeText.setText("Здравствуйте,");
        welcomeText.setTextColor(0xFFFFFFFF);
        welcomeText.setTextSize(16);
        welcomeText.setAlpha(0.9f);
        headerLayout.addView(welcomeText);

        TextView nameText = new TextView(this);
        nameText.setText(currentUser.getFio());
        nameText.setTextColor(0xFFFFFFFF);
        nameText.setTextSize(24);
        nameText.setTypeface(null, Typeface.BOLD);
        headerLayout.addView(nameText);

        TextView roleText = new TextView(this);
        roleText.setText("Руководитель · " + currentUser.getDepartment());
        roleText.setTextColor(0xFFFFFFFF);
        roleText.setTextSize(14);
        roleText.setAlpha(0.9f);
        roleText.setPadding(0, 8, 0, 0);
        headerLayout.addView(roleText);

        return headerLayout;
    }

    private Button createLogoutButton() {
        Button logoutBtn = new Button(this);
        logoutBtn.setText("🚪 Выйти из системы");
        logoutBtn.setTextSize(16);
        logoutBtn.setTextColor(0xFFEF4444);
        logoutBtn.setBackgroundResource(R.drawable.button_outline);
        logoutBtn.setPadding(24, 16, 24, 16);
        logoutBtn.setAllCaps(false);

        LinearLayout.LayoutParams logoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        logoutParams.setMargins(0, 24, 0, 0);
        logoutBtn.setLayoutParams(logoutParams);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonHelper.logout(ManagerActivity.this);
                startActivity(new Intent(ManagerActivity.this, LoginActivity.class));
                finish();
            }
        });

        return logoutBtn;
    }

    private void addDepartmentStats() {
        LinearLayout statsLayout = new LinearLayout(this);
        statsLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams statsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        statsParams.setMargins(0, 0, 0, 24);
        statsLayout.setLayoutParams(statsParams);


        String today = dateFormat.format(new Date());
        int presentToday = 0;
        int lateToday = 0;

        for (TimeRecord record : allRecords) {
            if (record.getDate().equals(today)) {
                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        presentToday++;
                        if (record.getTimeIn() != null && record.getTimeIn().compareTo("09:00") > 0) {
                            lateToday++;
                        }
                        break;
                    }
                }
            }
        }

        addStatCard(statsLayout, "👥", String.valueOf(departmentEmployees.size()), "Сотрудников");
        addStatCard(statsLayout, "✅", String.valueOf(presentToday), "На работе");
        addStatCard(statsLayout, "⚠️", String.valueOf(lateToday), "Опоздало");

        mainLayout.addView(statsLayout);
    }

    private void addStatCard(LinearLayout parent, String emoji, String value, String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);
        card.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1);
        cardParams.setMargins(4, 0, 4, 0);
        card.setLayoutParams(cardParams);

        TextView emojiView = new TextView(this);
        emojiView.setText(emoji);
        emojiView.setTextSize(24);
        card.addView(emojiView);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(20);
        valueView.setTypeface(null, Typeface.BOLD);
        valueView.setTextColor(0xFF1F2937);
        card.addView(valueView);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(12);
        labelView.setTextColor(0xFF6B7280);
        card.addView(labelView);

        parent.addView(card);
    }

    private void addActionButton(String title, String description, int color, View.OnClickListener listener) {
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.setPadding(20, 20, 20, 20);
        buttonLayout.setBackgroundResource(R.drawable.modern_card);
        buttonLayout.setClickable(true);
        buttonLayout.setFocusable(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 12);
        buttonLayout.setLayoutParams(params);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(16);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextColor(0xFF1F2937);
        buttonLayout.addView(titleView);

        TextView descView = new TextView(this);
        descView.setText(description);
        descView.setTextSize(14);
        descView.setTextColor(0xFF6B7280);
        descView.setPadding(0, 4, 0, 0);
        buttonLayout.addView(descView);

        buttonLayout.setOnClickListener(listener);

        mainLayout.addView(buttonLayout);
    }

    private Button createBottomBackButton() {
        Button backBtn = new Button(this);
        backBtn.setText("← На главную");
        backBtn.setTextSize(16);
        backBtn.setTextColor(0xFF6366F1);
        backBtn.setBackgroundResource(R.drawable.button_outline);
        backBtn.setPadding(24, 16, 24, 16);
        backBtn.setAllCaps(false);

        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        backParams.setMargins(0, 24, 0, 0);
        backBtn.setLayoutParams(backParams);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        return backBtn;
    }


    private void showDepartmentEmployees() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        listLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Сотрудники отдела " + currentUser.getDepartment());
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        listLayout.addView(title);

        for (User emp : departmentEmployees) {
            LinearLayout empCard = createEmployeeCard(emp);
            listLayout.addView(empCard);
        }

        Button backBtn = createBottomBackButton();
        listLayout.addView(backBtn);

        scrollView.addView(listLayout);
        setContentView(scrollView);
    }

    private LinearLayout createEmployeeCard(User emp) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 8);
        card.setLayoutParams(cardParams);

        TextView nameView = new TextView(this);
        nameView.setText(emp.getFio());
        nameView.setTextSize(16);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setTextColor(0xFF1F2937);
        card.addView(nameView);

        TextView posView = new TextView(this);
        posView.setText(emp.getPosition());
        posView.setTextSize(14);
        posView.setTextColor(0xFF6B7280);
        card.addView(posView);


        String today = dateFormat.format(new Date());
        String status = "Сегодня не отмечался";
        int statusColor = 0xFF6B7280;

        for (TimeRecord record : allRecords) {
            if (record.getUserId() == emp.getId() && record.getDate().equals(today)) {
                if (record.getTimeIn() != null) {
                    status = "Пришел: " + record.getTimeIn();
                    statusColor = 0xFF10B981;
                }
                if (record.getTimeOut() != null) {
                    status += ", ушел: " + record.getTimeOut();
                }
                break;
            }
        }

        TextView statusView = new TextView(this);
        statusView.setText(status);
        statusView.setTextSize(14);
        statusView.setTextColor(statusColor);
        statusView.setPadding(0, 8, 0, 0);
        card.addView(statusView);

        return card;
    }


    private void showDepartmentReports() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout reportsLayout = new LinearLayout(this);
        reportsLayout.setOrientation(LinearLayout.VERTICAL);
        reportsLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Отчеты по отделу");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        reportsLayout.addView(title);

        // Отчет за сегодня
        LinearLayout todayReport = createTodayReport();
        reportsLayout.addView(todayReport);

        // Отчет за неделю
        LinearLayout weekReport = createWeekReport();
        reportsLayout.addView(weekReport);

        // Отчет за месяц
        LinearLayout monthReport = createMonthReport();
        reportsLayout.addView(monthReport);

        Button backBtn = createBottomBackButton();
        reportsLayout.addView(backBtn);

        scrollView.addView(reportsLayout);
        setContentView(scrollView);
    }

    private LinearLayout createTodayReport() {
        LinearLayout card = createReportCard("📅 Отчет за сегодня");

        String today = dateFormat.format(new Date());
        int total = 0;
        int late = 0;
        double hours = 0;

        for (TimeRecord record : allRecords) {
            if (record.getDate().equals(today)) {
                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        total++;
                        if (record.getTimeIn() != null && record.getTimeIn().compareTo("09:00") > 0) {
                            late++;
                        }
                        if (record.getHours() != null) {
                            hours += record.getHours();
                        }
                        break;
                    }
                }
            }
        }

        addReportLine(card, "Отметилось:", String.valueOf(total));
        addReportLine(card, "Опоздало:", String.valueOf(late));
        addReportLine(card, "Всего часов:", String.format("%.1f ч", hours));

        return card;
    }

    private LinearLayout createWeekReport() {
        LinearLayout card = createReportCard("📊 Отчет за последние 7 дней");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String weekAgo = dateFormat.format(cal.getTime());
        String today = dateFormat.format(new Date());

        Map<Integer, Double> employeeHours = new HashMap<>();
        Map<Integer, Integer> employeeDays = new HashMap<>();

        for (TimeRecord record : allRecords) {
            if (record.getDate().compareTo(weekAgo) >= 0 &&
                    record.getDate().compareTo(today) <= 0 &&
                    record.getHours() != null) {

                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        employeeHours.put(emp.getId(),
                                employeeHours.getOrDefault(emp.getId(), 0.0) + record.getHours());
                        employeeDays.put(emp.getId(),
                                employeeDays.getOrDefault(emp.getId(), 0) + 1);
                        break;
                    }
                }
            }
        }

        double totalHours = 0;
        for (double h : employeeHours.values()) {
            totalHours += h;
        }

        addReportLine(card, "Всего часов:", String.format("%.1f ч", totalHours));
        addReportLine(card, "Сотрудников с отметками:", String.valueOf(employeeHours.size()));

        if (!employeeHours.isEmpty()) {
            addReportLine(card, "Среднее на сотрудника:",
                    String.format("%.1f ч", totalHours / employeeHours.size()));
        }

        return card;
    }

    private LinearLayout createMonthReport() {
        LinearLayout card = createReportCard("📆 Отчет за текущий месяц");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String monthStart = dateFormat.format(cal.getTime());
        String today = dateFormat.format(new Date());

        double totalHours = 0;
        int totalDays = 0;

        for (TimeRecord record : allRecords) {
            if (record.getDate().compareTo(monthStart) >= 0 &&
                    record.getDate().compareTo(today) <= 0 &&
                    record.getHours() != null) {

                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        totalHours += record.getHours();
                        totalDays++;
                        break;
                    }
                }
            }
        }

        addReportLine(card, "Всего часов:", String.format("%.1f ч", totalHours));
        addReportLine(card, "Всего отметок:", String.valueOf(totalDays));

        if (totalDays > 0) {
            addReportLine(card, "Среднее в день:", String.format("%.1f ч", totalHours / totalDays));
        }

        return card;
    }

    private LinearLayout createReportCard(String title) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        card.setLayoutParams(cardParams);

        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(18);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextColor(0xFF1F2937);
        titleView.setPadding(0, 0, 0, 12);
        card.addView(titleView);

        return card;
    }

    private void addReportLine(LinearLayout parent, String label, String value) {
        LinearLayout line = new LinearLayout(this);
        line.setOrientation(LinearLayout.HORIZONTAL);
        line.setPadding(0, 8, 0, 0);

        TextView labelView = new TextView(this);
        labelView.setText(label);
        labelView.setTextSize(14);
        labelView.setTextColor(0xFF6B7280);
        labelView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        line.addView(labelView);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(14);
        valueView.setTypeface(null, Typeface.BOLD);
        valueView.setTextColor(0xFF1F2937);
        line.addView(valueView);

        parent.addView(line);
    }


    private void showEmployeeRecords() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout recordsLayout = new LinearLayout(this);
        recordsLayout.setOrientation(LinearLayout.VERTICAL);
        recordsLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Отметки сотрудников");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        recordsLayout.addView(title);

        for (User emp : departmentEmployees) {
            Button empBtn = createEmployeeButton(emp);
            recordsLayout.addView(empBtn);
        }

        Button backBtn = createBottomBackButton();
        recordsLayout.addView(backBtn);

        scrollView.addView(recordsLayout);
        setContentView(scrollView);
    }

    private Button createEmployeeButton(User emp) {
        Button empBtn = new Button(this);
        empBtn.setText(emp.getFio() + " (" + emp.getPosition() + ")");
        empBtn.setTextSize(14);
        empBtn.setTextColor(0xFF1F2937);
        empBtn.setBackgroundResource(R.drawable.modern_card);
        empBtn.setPadding(20, 16, 20, 16);
        empBtn.setAllCaps(false);
        empBtn.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 0, 0, 8);
        empBtn.setLayoutParams(btnParams);

        empBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmployeeDetails(emp);
            }
        });

        return empBtn;
    }

    private void showEmployeeDetails(User emp) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout detailsLayout = new LinearLayout(this);
        detailsLayout.setOrientation(LinearLayout.VERTICAL);
        detailsLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Отметки: " + emp.getFio());
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        detailsLayout.addView(title);

        List<TimeRecord> empRecords = new ArrayList<>();
        for (TimeRecord record : allRecords) {
            if (record.getUserId() == emp.getId()) {
                empRecords.add(record);
            }
        }


        Collections.sort(empRecords, new Comparator<TimeRecord>() {
            @Override
            public int compare(TimeRecord r1, TimeRecord r2) {
                return r2.getDate().compareTo(r1.getDate());
            }
        });

        if (empRecords.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("Нет отметок");
            emptyView.setTextSize(16);
            emptyView.setTextColor(0xFF6B7280);
            emptyView.setPadding(0, 50, 0, 50);
            emptyView.setGravity(android.view.Gravity.CENTER);
            detailsLayout.addView(emptyView);
        } else {
            for (TimeRecord record : empRecords) {
                LinearLayout recordCard = createRecordCard(record);
                detailsLayout.addView(recordCard);
            }
        }

        Button backBtn = new Button(this);
        backBtn.setText("← Назад к списку");
        backBtn.setTextSize(16);
        backBtn.setTextColor(0xFF6366F1);
        backBtn.setBackgroundResource(R.drawable.button_outline);
        backBtn.setPadding(24, 16, 24, 16);
        backBtn.setAllCaps(false);

        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        backParams.setMargins(0, 16, 0, 0);
        backBtn.setLayoutParams(backParams);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmployeeRecords();
            }
        });
        detailsLayout.addView(backBtn);

        scrollView.addView(detailsLayout);
        setContentView(scrollView);
    }

    private LinearLayout createRecordCard(TimeRecord record) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 20, 20, 20);
        card.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 8);
        card.setLayoutParams(cardParams);


        TextView dateView = new TextView(this);
        try {
            Date date = dateFormat.parse(record.getDate());
            dateView.setText(displayDateFormat.format(date));
        } catch (Exception e) {
            dateView.setText(record.getDate());
        }
        dateView.setTextSize(16);
        dateView.setTypeface(null, Typeface.BOLD);
        dateView.setTextColor(0xFF1F2937);
        card.addView(dateView);


        LinearLayout timeLayout = new LinearLayout(this);
        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeLayout.setPadding(0, 8, 0, 0);

        TextView inView = new TextView(this);
        inView.setText("🟢 Приход: " + (record.getTimeIn() != null ? record.getTimeIn() : "—"));
        inView.setTextSize(14);
        inView.setTextColor(0xFF10B981);
        inView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        timeLayout.addView(inView);

        TextView outView = new TextView(this);
        outView.setText("🔴 Уход: " + (record.getTimeOut() != null ? record.getTimeOut() : "—"));
        outView.setTextSize(14);
        outView.setTextColor(record.getTimeOut() != null ? 0xFFEF4444 : 0xFF6B7280);
        outView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        timeLayout.addView(outView);

        card.addView(timeLayout);

        // Часы
        if (record.getHours() != null) {
            TextView hoursView = new TextView(this);
            hoursView.setText("⏱ Отработано: " + String.format("%.1f ч", record.getHours()));
            hoursView.setTextSize(14);
            hoursView.setTextColor(0xFF6366F1);
            hoursView.setTypeface(null, Typeface.BOLD);
            hoursView.setPadding(0, 8, 0, 0);
            card.addView(hoursView);
        }

        return card;
    }


    private void showAnalytics() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout analyticsLayout = new LinearLayout(this);
        analyticsLayout.setOrientation(LinearLayout.VERTICAL);
        analyticsLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Аналитика по отделу");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        analyticsLayout.addView(title);


        LinearLayout lateStats = createLateStats();
        analyticsLayout.addView(lateStats);


        LinearLayout ratingStats = createRatingStats();
        analyticsLayout.addView(ratingStats);


        LinearLayout dayStats = createDayStats();
        analyticsLayout.addView(dayStats);

        Button backBtn = createBottomBackButton();
        analyticsLayout.addView(backBtn);

        scrollView.addView(analyticsLayout);
        setContentView(scrollView);
    }

    private LinearLayout createLateStats() {
        LinearLayout card = createReportCard("⚠️ Статистика опозданий");

        String today = dateFormat.format(new Date());
        int lateToday = 0;
        int lateWeek = 0;
        Map<Integer, Integer> employeeLates = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String weekAgo = dateFormat.format(cal.getTime());

        for (TimeRecord record : allRecords) {
            if (record.getTimeIn() != null && record.getTimeIn().compareTo("09:00") > 0) {
                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        if (record.getDate().equals(today)) {
                            lateToday++;
                        }
                        if (record.getDate().compareTo(weekAgo) >= 0) {
                            lateWeek++;
                            employeeLates.put(emp.getId(),
                                    employeeLates.getOrDefault(emp.getId(), 0) + 1);
                        }
                        break;
                    }
                }
            }
        }

        addReportLine(card, "Опоздало сегодня:", String.valueOf(lateToday));
        addReportLine(card, "Опозданий за неделю:", String.valueOf(lateWeek));


        int maxLates = 0;
        String worstEmployee = "";
        for (Map.Entry<Integer, Integer> entry : employeeLates.entrySet()) {
            if (entry.getValue() > maxLates) {
                maxLates = entry.getValue();
                for (User emp : departmentEmployees) {
                    if (emp.getId() == entry.getKey()) {
                        worstEmployee = emp.getFio();
                        break;
                    }
                }
            }
        }

        if (!worstEmployee.isEmpty()) {
            addReportLine(card, "Чаще всех опаздывает:", worstEmployee + " (" + maxLates + " раз)");
        }

        return card;
    }

    private LinearLayout createRatingStats() {
        LinearLayout card = createReportCard("🏆 Рейтинг сотрудников");

        Map<Integer, Double> employeeHours = new HashMap<>();

        for (TimeRecord record : allRecords) {
            if (record.getHours() != null) {
                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        employeeHours.put(emp.getId(),
                                employeeHours.getOrDefault(emp.getId(), 0.0) + record.getHours());
                        break;
                    }
                }
            }
        }


        double maxHours = 0;
        String bestEmployee = "";
        for (Map.Entry<Integer, Double> entry : employeeHours.entrySet()) {
            if (entry.getValue() > maxHours) {
                maxHours = entry.getValue();
                for (User emp : departmentEmployees) {
                    if (emp.getId() == entry.getKey()) {
                        bestEmployee = emp.getFio();
                        break;
                    }
                }
            }
        }

        if (!bestEmployee.isEmpty()) {
            addReportLine(card, "Больше всех отработал:",
                    bestEmployee + " (" + String.format("%.1f", maxHours) + " ч)");
        }


        if (!employeeHours.isEmpty()) {
            double total = 0;
            for (double h : employeeHours.values()) {
                total += h;
            }
            addReportLine(card, "Среднее на сотрудника:",
                    String.format("%.1f ч", total / employeeHours.size()));
        }

        return card;
    }

    private LinearLayout createDayStats() {
        LinearLayout card = createReportCard("📆 Динамика по дням недели");

        Map<String, Integer> dayCount = new HashMap<>();
        dayCount.put("Пн", 0);
        dayCount.put("Вт", 0);
        dayCount.put("Ср", 0);
        dayCount.put("Чт", 0);
        dayCount.put("Пт", 0);
        dayCount.put("Сб", 0);
        dayCount.put("Вс", 0);

        SimpleDateFormat dayFormat = new SimpleDateFormat("E", new Locale("ru"));

        for (TimeRecord record : allRecords) {
            if (record.getTimeIn() != null) {
                for (User emp : departmentEmployees) {
                    if (record.getUserId() == emp.getId()) {
                        try {
                            Date date = dateFormat.parse(record.getDate());
                            String day = dayFormat.format(date);
                            dayCount.put(day, dayCount.get(day) + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }

        String[] days = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        for (String day : days) {
            if (dayCount.get(day) > 0) {
                addReportLine(card, day + ":", String.valueOf(dayCount.get(day)) + " отметок");
            }
        }

        return card;
    }
}