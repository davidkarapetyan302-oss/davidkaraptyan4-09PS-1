package com.example.cpszadanie;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    private static final String USERS_FILE = "users.json";
    private static final String RECORDS_FILE = "time_records.json";
    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_CURRENT_USER = "current_user";



    public static List<User> loadUsers(Context context) {
        List<User> users = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(USERS_FILE);
            InputStreamReader reader = new InputStreamReader(fis);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                User user = new User(
                        obj.getInt("id"),
                        obj.getString("fio"),
                        obj.getString("position"),
                        obj.getString("department"),
                        obj.getInt("role"),
                        obj.getString("login"),
                        obj.getString("password"),
                        obj.optString("phone", ""),
                        obj.optString("email", "")
                );
                users.add(user);
            }
            return users;
        } catch (Exception e) {
            return createDefaultUsers(context);
        }
    }

    private static List<User> createDefaultUsers(Context context) {
        List<User> defaultUsers = new ArrayList<>();


        defaultUsers.add(new User(1, "Иванов Иван", "Инженер", Constants.DEPARTMENTS[0], 0,
                "ivanov", "123", "+7-111-222-33-44", "ivanov@mail.ru"));
        defaultUsers.add(new User(2, "Петров Петр", "Начальник отдела", Constants.DEPARTMENTS[0], 1,
                "petrov", "123", "+7-111-222-33-55", "petrov@mail.ru"));
        defaultUsers.add(new User(3, "Сидорова Анна", "Табельщик", Constants.DEPARTMENTS[5], 2,  // HR
                "sidorova", "123", "+7-111-222-33-66", "sidorova@mail.ru"));

        saveUsers(context, defaultUsers);
        return defaultUsers;
    }

    public static void saveUsers(Context context, List<User> users) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (User user : users) {
                JSONObject obj = new JSONObject();
                obj.put("id", user.getId());
                obj.put("fio", user.getFio());
                obj.put("position", user.getPosition());
                obj.put("department", user.getDepartment());
                obj.put("role", user.getRole());
                obj.put("login", user.getLogin());
                obj.put("password", user.getPassword());
                obj.put("phone", user.getPhone());
                obj.put("email", user.getEmail());
                jsonArray.put(obj);
            }

            FileOutputStream fos = context.openFileOutput(USERS_FILE, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(jsonArray.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static List<TimeRecord> loadTimeRecords(Context context) {
        List<TimeRecord> records = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(RECORDS_FILE);
            InputStreamReader reader = new InputStreamReader(fis);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                TimeRecord record = new TimeRecord(
                        obj.getInt("id"),
                        obj.getInt("userId"),
                        obj.getString("date"),
                        obj.has("timeIn") ? obj.getString("timeIn") : null,
                        obj.has("timeOut") ? obj.getString("timeOut") : null,
                        obj.has("hours") ? obj.getDouble("hours") : null
                );
                records.add(record);
            }
            return records;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveTimeRecords(Context context, List<TimeRecord> records) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (TimeRecord record : records) {
                JSONObject obj = new JSONObject();
                obj.put("id", record.getId());
                obj.put("userId", record.getUserId());
                obj.put("date", record.getDate());
                if (record.getTimeIn() != null) obj.put("timeIn", record.getTimeIn());
                if (record.getTimeOut() != null) obj.put("timeOut", record.getTimeOut());
                if (record.getHours() != null) obj.put("hours", record.getHours());
                jsonArray.put(obj);
            }

            FileOutputStream fos = context.openFileOutput(RECORDS_FILE, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(jsonArray.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static int getNextUserId(Context context) {
        List<User> users = loadUsers(context);
        int maxId = 0;
        for (User user : users) {
            if (user.getId() > maxId) maxId = user.getId();
        }
        return maxId + 1;
    }

    public static int getNextRecordId(Context context) {
        List<TimeRecord> records = loadTimeRecords(context);
        int maxId = 0;
        for (TimeRecord record : records) {
            if (record.getId() > maxId) maxId = record.getId();
        }
        return maxId + 1;
    }

    public static void saveCurrentUser(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", user.getId());
            obj.put("fio", user.getFio());
            obj.put("position", user.getPosition());
            obj.put("department", user.getDepartment());
            obj.put("role", user.getRole());
            obj.put("login", user.getLogin());
            obj.put("password", user.getPassword());
            obj.put("phone", user.getPhone());
            obj.put("email", user.getEmail());
            prefs.edit().putString(KEY_CURRENT_USER, obj.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static User getCurrentUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CURRENT_USER, null);
        if (json == null) return null;
        try {
            JSONObject obj = new JSONObject(json);
            return new User(
                    obj.getInt("id"),
                    obj.getString("fio"),
                    obj.getString("position"),
                    obj.getString("department"),
                    obj.getInt("role"),
                    obj.getString("login"),
                    obj.getString("password"),
                    obj.optString("phone", ""),
                    obj.optString("email", "")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}