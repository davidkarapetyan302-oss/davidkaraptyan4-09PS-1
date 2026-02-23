package com.example.cpszadanie;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StatsActivity extends Activity {

    private TextView tvStats;
    private Button btnBack;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        currentUser = JsonHelper.getCurrentUser(this);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);


        TextView title = new TextView(this);
        title.setText("Моя статистика");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 30);
        layout.addView(title);


        tvStats = new TextView(this);
        tvStats.setTextSize(18);
        tvStats.setPadding(0, 0, 0, 30);
        layout.addView(tvStats);


        btnBack = new Button(this);
        btnBack.setText("Назад");
        btnBack.setTextSize(16);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout.addView(btnBack);

        setContentView(layout);


        loadStats();
    }

    private void loadStats() {
        if (currentUser == null) {
            tvStats.setText("Ошибка: пользователь не найден");
            return;
        }

        java.util.List<TimeRecord> records = JsonHelper.loadTimeRecords(this);

        double totalHours = 0;
        int days = 0;

        for (TimeRecord record : records) {
            if (record.getUserId() == currentUser.getId() && record.getHours() != null) {
                totalHours += record.getHours();
                days++;
            }
        }

        String stats = "Всего отработано: " + String.format("%.1f", totalHours) + " ч\n\n";
        stats += "Количество дней: " + days + "\n\n";
        if (days > 0) {
            stats += "Среднее в день: " + String.format("%.1f", totalHours / days) + " ч";
        }

        tvStats.setText(stats);
    }
}