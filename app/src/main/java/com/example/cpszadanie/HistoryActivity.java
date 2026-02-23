package com.example.cpszadanie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private Button btnBack;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(50, 50, 50, 50);


        TextView title = new TextView(this);
        title.setText("ИСТОРИЯ ОТМЕТОК");
        title.setTextSize(24);
        title.setTextColor(0xFF000000);
        title.setPadding(0, 0, 0, 30);
        mainLayout.addView(title);


        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(container);


        btnBack = new Button(this);
        btnBack.setText("НАЗАД");
        btnBack.setTextSize(18);
        btnBack.setPadding(20, 20, 20, 20);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mainLayout.addView(btnBack);

        setContentView(mainLayout);


        loadData();
    }

    private void loadData() {
        User currentUser = JsonHelper.getCurrentUser(this);
        if (currentUser == null) {
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        List<TimeRecord> records = JsonHelper.loadTimeRecords(this);

        boolean found = false;

        for (TimeRecord record : records) {
            if (record.getUserId() == currentUser.getId()) {
                found = true;


                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(20, 20, 20, 20);
                card.setBackgroundColor(0xFFF0F0F0);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 10);
                card.setLayoutParams(params);

                // Дата
                TextView dateText = new TextView(this);
                dateText.setText("📅 " + record.getDate());
                dateText.setTextSize(16);
                dateText.setTextColor(0xFF000000);
                dateText.setTypeface(null, android.graphics.Typeface.BOLD);
                card.addView(dateText);

                // Приход
                TextView inText = new TextView(this);
                inText.setText("⏰ Приход: " + (record.getTimeIn() != null ? record.getTimeIn() : "—"));
                inText.setTextSize(14);
                inText.setTextColor(0xFF666666);
                card.addView(inText);

                // Уход
                TextView outText = new TextView(this);
                outText.setText("⏰ Уход: " + (record.getTimeOut() != null ? record.getTimeOut() : "—"));
                outText.setTextSize(14);
                outText.setTextColor(0xFF666666);
                card.addView(outText);

                // Часы
                if (record.getHours() != null) {
                    TextView hoursText = new TextView(this);
                    hoursText.setText("⏱ Отработано: " + String.format("%.1f", record.getHours()) + " ч");
                    hoursText.setTextSize(14);
                    hoursText.setTextColor(0xFF4CAF50);
                    hoursText.setTypeface(null, android.graphics.Typeface.BOLD);
                    card.addView(hoursText);
                }

                container.addView(card);
            }
        }

        if (!found) {
            TextView emptyText = new TextView(this);
            emptyText.setText("Нет записей");
            emptyText.setTextSize(18);
            emptyText.setTextColor(0xFF666666);
            emptyText.setPadding(0, 50, 0, 50);
            emptyText.setGravity(android.view.Gravity.CENTER);
            container.addView(emptyText);
        }
    }
}