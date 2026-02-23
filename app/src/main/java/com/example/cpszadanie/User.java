package com.example.cpszadanie;

public class User {
    private int id;
    private String fio;
    private String position;
    private String department;
    private int role; // 0 - сотрудник, 1 - руководитель, 2 - табельщик
    private String login;
    private String password;
    private String phone;
    private String email;

    public User(int id, String fio, String position, String department, int role,
                String login, String password, String phone, String email) {
        this.id = id;
        this.fio = fio;
        this.position = position;
        this.department = department;
        this.role = role;
        this.login = login;
        this.password = password;
        this.phone = phone;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFio() { return fio; }
    public void setFio(String fio) { this.fio = fio; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}