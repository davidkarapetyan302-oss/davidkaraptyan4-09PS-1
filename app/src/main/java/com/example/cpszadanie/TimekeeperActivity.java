package com.example.cpszadanie;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;

public class TimekeeperActivity extends AppCompatActivity {

    private LinearLayout mainLayout;
    private User currentUser;
    private List<User> allUsers;
    private List<TimeRecord> allRecords;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Скрываем ActionBar
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

        createMainScreen();
    }

    private void createMainScreen() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(24, 24, 24, 24);

        // Шапка с приветствием
        LinearLayout headerLayout = createHeader();
        mainLayout.addView(headerLayout);

        // Статистика для табельщика
        addStatsSection();

        // Кнопки действий
        addActionButton("👥 Управление сотрудниками", "Добавление, удаление, редактирование", 0xFF6366F1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmployeeManagement();
            }
        });

        addActionButton("📋 Все сотрудники", "Просмотр полного списка", 0xFF10B981, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllEmployees();
            }
        });

        addActionButton("📊 Отчеты", "Формирование отчетов за период", 0xFFF59E0B, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReports();
            }
        });

        addActionButton("⏱ Корректировка отметок", "Исправление времени прихода/ухода", 0xFF8B5CF6, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCorrection();
            }
        });

        // Кнопка выхода
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
        welcomeText.setText("Добро пожаловать,");
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
        roleText.setText("Табельщик · HR отдел");
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
                JsonHelper.logout(TimekeeperActivity.this);
                startActivity(new Intent(TimekeeperActivity.this, LoginActivity.class));
                finish();
            }
        });

        return logoutBtn;
    }

    private void addStatsSection() {
        LinearLayout statsLayout = new LinearLayout(this);
        statsLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams statsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        statsParams.setMargins(0, 0, 0, 24);
        statsLayout.setLayoutParams(statsParams);

        int totalEmployees = 0;
        int totalManagers = 0;
        for (User user : allUsers) {
            if (user.getRole() == 0) totalEmployees++;
            if (user.getRole() == 1) totalManagers++;
        }

        addStatCard(statsLayout, "👥", String.valueOf(totalEmployees), "Сотрудников");
        addStatCard(statsLayout, "👔", String.valueOf(totalManagers), "Руководителей");
        addStatCard(statsLayout, "📅", String.valueOf(allRecords.size()), "Записей");

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
        backBtn.setText("← Назад");
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

    // ==================== ЭКРАН УПРАВЛЕНИЯ СОТРУДНИКАМИ ====================
    private void showEmployeeManagement() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout manageLayout = new LinearLayout(this);
        manageLayout.setOrientation(LinearLayout.VERTICAL);
        manageLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Управление сотрудниками");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        manageLayout.addView(title);

        Button addBtn = createAddEmployeeButton();
        manageLayout.addView(addBtn);

        for (User user : allUsers) {
            if (user.getRole() != 2) {
                LinearLayout userCard = createUserManageCard(user);
                manageLayout.addView(userCard);
            }
        }

        Button backBtn = createBottomBackButton();
        manageLayout.addView(backBtn);

        scrollView.addView(manageLayout);
        setContentView(scrollView);
    }

    private Button createAddEmployeeButton() {
        Button addBtn = new Button(this);
        addBtn.setText("+ Добавить нового сотрудника");
        addBtn.setTextSize(16);
        addBtn.setTextColor(0xFFFFFFFF);
        addBtn.setBackgroundResource(R.drawable.button_checkin);
        addBtn.setPadding(24, 16, 24, 16);
        addBtn.setAllCaps(false);

        LinearLayout.LayoutParams addParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        addParams.setMargins(0, 0, 0, 16);
        addBtn.setLayoutParams(addParams);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TimekeeperActivity.this, RegisterActivity.class));
            }
        });

        return addBtn;
    }

    private LinearLayout createUserManageCard(User user) {
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
        nameView.setText(user.getFio());
        nameView.setTextSize(16);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setTextColor(0xFF1F2937);
        card.addView(nameView);

        TextView detailsView = new TextView(this);
        detailsView.setText(user.getPosition() + " · " + user.getDepartment());
        detailsView.setTextSize(14);
        detailsView.setTextColor(0xFF6B7280);
        card.addView(detailsView);

        Button deleteBtn = new Button(this);
        deleteBtn.setText("Удалить");
        deleteBtn.setTextSize(14);
        deleteBtn.setTextColor(0xFFEF4444);
        deleteBtn.setBackgroundResource(R.drawable.button_outline);
        deleteBtn.setPadding(16, 8, 16, 8);
        deleteBtn.setAllCaps(false);

        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        deleteParams.setMargins(0, 12, 0, 0);
        deleteBtn.setLayoutParams(deleteParams);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allUsers.remove(user);
                JsonHelper.saveUsers(TimekeeperActivity.this, allUsers);
                Toast.makeText(TimekeeperActivity.this, "Сотрудник удален", Toast.LENGTH_SHORT).show();
                showEmployeeManagement();
            }
        });
        card.addView(deleteBtn);

        return card;
    }

    // ==================== ЭКРАН ВСЕХ СОТРУДНИКОВ ====================
    private void showAllEmployees() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        listLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Все сотрудники");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        listLayout.addView(title);

        for (User user : allUsers) {
            LinearLayout userCard = createUserViewCard(user);
            listLayout.addView(userCard);
        }

        Button backBtn = createBottomBackButton();
        listLayout.addView(backBtn);

        scrollView.addView(listLayout);
        setContentView(scrollView);
    }

    private LinearLayout createUserViewCard(User user) {
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
        nameView.setText(user.getFio());
        nameView.setTextSize(16);
        nameView.setTypeface(null, Typeface.BOLD);
        nameView.setTextColor(0xFF1F2937);
        card.addView(nameView);

        TextView detailsView = new TextView(this);
        detailsView.setText(user.getPosition() + " · " + user.getDepartment());
        detailsView.setTextSize(14);
        detailsView.setTextColor(0xFF6B7280);
        card.addView(detailsView);

        String roleStr = "";
        int roleColor = 0;
        if (user.getRole() == 0) {
            roleStr = "Сотрудник";
            roleColor = 0xFF10B981;
        } else if (user.getRole() == 1) {
            roleStr = "Руководитель";
            roleColor = 0xFF6366F1;
        } else if (user.getRole() == 2) {
            roleStr = "Табельщик";
            roleColor = 0xFF8B5CF6;
        }

        TextView roleView = new TextView(this);
        roleView.setText(roleStr);
        roleView.setTextSize(12);
        roleView.setTextColor(roleColor);
        roleView.setPadding(0, 8, 0, 0);
        card.addView(roleView);

        return card;
    }

    // ==================== ЭКРАН ОТЧЕТОВ (РАБОЧИЙ) ====================
    private void showReports() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout reportsLayout = new LinearLayout(this);
        reportsLayout.setOrientation(LinearLayout.VERTICAL);
        reportsLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Отчеты");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        reportsLayout.addView(title);

        // Отчет по дням
        LinearLayout dailyReport = createDailyReport();
        reportsLayout.addView(dailyReport);

        // Отчет по сотрудникам
        LinearLayout employeeReport = createEmployeeReport();
        reportsLayout.addView(employeeReport);

        // Отчет за последние 7 дней
        LinearLayout weeklyReport = createWeeklyReport();
        reportsLayout.addView(weeklyReport);

        Button backBtn = createBottomBackButton();
        reportsLayout.addView(backBtn);

        scrollView.addView(reportsLayout);
        setContentView(scrollView);
    }

    private LinearLayout createDailyReport() {
        LinearLayout reportCard = new LinearLayout(this);
        reportCard.setOrientation(LinearLayout.VERTICAL);
        reportCard.setPadding(20, 20, 20, 20);
        reportCard.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        reportCard.setLayoutParams(cardParams);

        TextView reportTitle = new TextView(this);
        reportTitle.setText("📅 Отчет за сегодня");
        reportTitle.setTextSize(18);
        reportTitle.setTypeface(null, Typeface.BOLD);
        reportTitle.setTextColor(0xFF1F2937);
        reportTitle.setPadding(0, 0, 0, 12);
        reportCard.addView(reportTitle);

        String today = dateFormat.format(new Date());
        int presentCount = 0;
        int lateCount = 0;
        double totalHoursToday = 0;

        for (TimeRecord record : allRecords) {
            if (record.getDate().equals(today)) {
                presentCount++;
                if (record.getTimeIn() != null && record.getTimeIn().compareTo("09:00") > 0) {
                    lateCount++;
                }
                if (record.getHours() != null) {
                    totalHoursToday += record.getHours();
                }
            }
        }

        addReportLine(reportCard, "Отметилось сегодня:", String.valueOf(presentCount));
        addReportLine(reportCard, "Опоздало:", String.valueOf(lateCount));
        addReportLine(reportCard, "Всего часов:", String.format("%.1f ч", totalHoursToday));

        return reportCard;
    }

    private LinearLayout createEmployeeReport() {
        LinearLayout reportCard = new LinearLayout(this);
        reportCard.setOrientation(LinearLayout.VERTICAL);
        reportCard.setPadding(20, 20, 20, 20);
        reportCard.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        reportCard.setLayoutParams(cardParams);

        TextView reportTitle = new TextView(this);
        reportTitle.setText("👥 Отчет по сотрудникам");
        reportTitle.setTextSize(18);
        reportTitle.setTypeface(null, Typeface.BOLD);
        reportTitle.setTextColor(0xFF1F2937);
        reportTitle.setPadding(0, 0, 0, 12);
        reportCard.addView(reportTitle);

        for (User user : allUsers) {
            if (user.getRole() == 0) {
                double userTotal = 0;
                int userDays = 0;
                for (TimeRecord record : allRecords) {
                    if (record.getUserId() == user.getId() && record.getHours() != null) {
                        userTotal += record.getHours();
                        userDays++;
                    }
                }
                String info = user.getFio() + ": " + String.format("%.1f", userTotal) + " ч (" + userDays + " дн.)";
                addReportLine(reportCard, "•", info);
            }
        }

        return reportCard;
    }

    private LinearLayout createWeeklyReport() {
        LinearLayout reportCard = new LinearLayout(this);
        reportCard.setOrientation(LinearLayout.VERTICAL);
        reportCard.setPadding(20, 20, 20, 20);
        reportCard.setBackgroundResource(R.drawable.modern_card);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 12);
        reportCard.setLayoutParams(cardParams);

        TextView reportTitle = new TextView(this);
        reportTitle.setText("📊 Отчет за последние 7 дней");
        reportTitle.setTextSize(18);
        reportTitle.setTypeface(null, Typeface.BOLD);
        reportTitle.setTextColor(0xFF1F2937);
        reportTitle.setPadding(0, 0, 0, 12);
        reportCard.addView(reportTitle);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        String weekAgo = dateFormat.format(cal.getTime());
        String today = dateFormat.format(new Date());

        double weekTotal = 0;
        int weekRecords = 0;

        for (TimeRecord record : allRecords) {
            if (record.getDate().compareTo(weekAgo) >= 0 &&
                    record.getDate().compareTo(today) <= 0 &&
                    record.getHours() != null) {
                weekTotal += record.getHours();
                weekRecords++;
            }
        }

        addReportLine(reportCard, "Всего часов:", String.format("%.1f ч", weekTotal));
        addReportLine(reportCard, "Всего записей:", String.valueOf(weekRecords));
        if (weekRecords > 0) {
            addReportLine(reportCard, "Среднее в день:", String.format("%.1f ч", weekTotal / weekRecords));
        }

        return reportCard;
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

    // ==================== ЭКРАН КОРРЕКТИРОВКИ ОТМЕТОК (РАБОЧИЙ) ====================
    private void showCorrection() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout correctionLayout = new LinearLayout(this);
        correctionLayout.setOrientation(LinearLayout.VERTICAL);
        correctionLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Корректировка отметок");
        title.setTextSize(22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        correctionLayout.addView(title);

        // Выбор сотрудника
        TextView selectEmployeeLabel = new TextView(this);
        selectEmployeeLabel.setText("Выберите сотрудника:");
        selectEmployeeLabel.setTextSize(16);
        selectEmployeeLabel.setTextColor(0xFF1F2937);
        selectEmployeeLabel.setPadding(0, 0, 0, 8);
        correctionLayout.addView(selectEmployeeLabel);

        for (User user : allUsers) {
            if (user.getRole() == 0) {
                Button userBtn = createEmployeeButton(user);
                correctionLayout.addView(userBtn);
            }
        }

        Button backBtn = createBottomBackButton();
        correctionLayout.addView(backBtn);

        scrollView.addView(correctionLayout);
        setContentView(scrollView);
    }

    private Button createEmployeeButton(User user) {
        Button userBtn = new Button(this);
        userBtn.setText(user.getFio() + " (" + user.getDepartment() + ")");
        userBtn.setTextSize(14);
        userBtn.setTextColor(0xFF1F2937);
        userBtn.setBackgroundResource(R.drawable.modern_card);
        userBtn.setPadding(20, 16, 20, 16);
        userBtn.setAllCaps(false);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(0, 0, 0, 8);
        userBtn.setLayoutParams(btnParams);

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmployeeRecords(user);
            }
        });

        return userBtn;
    }

    private void showEmployeeRecords(User user) {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F7FA);

        LinearLayout recordsLayout = new LinearLayout(this);
        recordsLayout.setOrientation(LinearLayout.VERTICAL);
        recordsLayout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("Отметки: " + user.getFio());
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(0xFF1F2937);
        title.setPadding(0, 0, 0, 20);
        recordsLayout.addView(title);

        List<TimeRecord> userRecords = new ArrayList<>();
        for (TimeRecord record : allRecords) {
            if (record.getUserId() == user.getId()) {
                userRecords.add(record);
            }
        }


        Collections.sort(userRecords, new Comparator<TimeRecord>() {
            @Override
            public int compare(TimeRecord r1, TimeRecord r2) {
                return r2.getDate().compareTo(r1.getDate());
            }
        });

        for (TimeRecord record : userRecords) {
            LinearLayout recordCard = createRecordCorrectionCard(record, user);
            recordsLayout.addView(recordCard);
        }

        Button backBtn = new Button(this);
        backBtn.setText("← Назад к списку сотрудников");
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
                showCorrection();
            }
        });
        recordsLayout.addView(backBtn);

        scrollView.addView(recordsLayout);
        setContentView(scrollView);
    }

    private LinearLayout createRecordCorrectionCard(TimeRecord record, User user) {
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


        LinearLayout currentLayout = new LinearLayout(this);
        currentLayout.setOrientation(LinearLayout.HORIZONTAL);
        currentLayout.setPadding(0, 8, 0, 12);

        TextView currentIn = new TextView(this);
        currentIn.setText("Приход: " + (record.getTimeIn() != null ? record.getTimeIn() : "—"));
        currentIn.setTextSize(14);
        currentIn.setTextColor(0xFF6B7280);
        currentIn.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        currentLayout.addView(currentIn);

        TextView currentOut = new TextView(this);
        currentOut.setText("Уход: " + (record.getTimeOut() != null ? record.getTimeOut() : "—"));
        currentOut.setTextSize(14);
        currentOut.setTextColor(0xFF6B7280);
        currentOut.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        currentLayout.addView(currentOut);

        card.addView(currentLayout);


        LinearLayout editLayout = new LinearLayout(this);
        editLayout.setOrientation(LinearLayout.HORIZONTAL);
        editLayout.setPadding(0, 0, 0, 12);

        EditText etNewIn = new EditText(this);
        etNewIn.setHint("Новый приход");
        etNewIn.setText(record.getTimeIn() != null ? record.getTimeIn() : "");
        etNewIn.setTextSize(14);
        etNewIn.setPadding(12, 8, 12, 8);
        etNewIn.setBackgroundResource(R.drawable.edittext_background);
        etNewIn.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        editLayout.addView(etNewIn);

        EditText etNewOut = new EditText(this);
        etNewOut.setHint("Новый уход");
        etNewOut.setText(record.getTimeOut() != null ? record.getTimeOut() : "");
        etNewOut.setTextSize(14);
        etNewOut.setPadding(12, 8, 12, 8);
        etNewOut.setBackgroundResource(R.drawable.edittext_background);
        etNewOut.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        editLayout.addView(etNewOut);

        card.addView(editLayout);


        Button saveBtn = new Button(this);
        saveBtn.setText("Сохранить изменения");
        saveBtn.setTextSize(14);
        saveBtn.setTextColor(0xFFFFFFFF);
        saveBtn.setBackgroundResource(R.drawable.button_checkin);
        saveBtn.setPadding(16, 12, 16, 12);
        saveBtn.setAllCaps(false);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newIn = etNewIn.getText().toString().trim();
                String newOut = etNewOut.getText().toString().trim();


                if (!newIn.isEmpty() && !newIn.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    Toast.makeText(TimekeeperActivity.this, "Неверный формат времени прихода. Используйте ЧЧ:ММ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newOut.isEmpty() && !newOut.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
                    Toast.makeText(TimekeeperActivity.this, "Неверный формат времени ухода. Используйте ЧЧ:ММ", Toast.LENGTH_SHORT).show();
                    return;
                }


                for (int i = 0; i < allRecords.size(); i++) {
                    if (allRecords.get(i).getId() == record.getId()) {
                        String timeIn = newIn.isEmpty() ? record.getTimeIn() : newIn;
                        String timeOut = newOut.isEmpty() ? record.getTimeOut() : newOut;

                        double hours = 0;
                        if (timeIn != null && timeOut != null) {
                            hours = calculateHours(timeIn, timeOut);
                        }

                        TimeRecord updatedRecord = new TimeRecord(
                                record.getId(),
                                record.getUserId(),
                                record.getDate(),
                                timeIn,
                                timeOut,
                                hours > 0 ? hours : null
                        );

                        allRecords.set(i, updatedRecord);
                        break;
                    }
                }

                JsonHelper.saveTimeRecords(TimekeeperActivity.this, allRecords);
                Toast.makeText(TimekeeperActivity.this, "Отметка обновлена", Toast.LENGTH_SHORT).show();
                showEmployeeRecords(user); // Обновляем экран
            }
        });
        card.addView(saveBtn);

        return card;
    }

    private double calculateHours(String timeIn, String timeOut) {
        try {
            String[] partsIn = timeIn.split(":");
            String[] partsOut = timeOut.split(":");

            int minutesIn = Integer.parseInt(partsIn[0]) * 60 + Integer.parseInt(partsIn[1]);
            int minutesOut = Integer.parseInt(partsOut[0]) * 60 + Integer.parseInt(partsOut[1]);

            return (minutesOut - minutesIn) / 60.0;
        } catch (Exception e) {
            return 0;
        }
    }
}