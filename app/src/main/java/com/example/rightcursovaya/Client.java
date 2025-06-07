package com.example.rightcursovaya;

public class Client {
    private String login;

    private String password;

    private String role;

    private String specialCode;

    public Client(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setSpecialCode(String specialCode) {
        this.specialCode = specialCode;
    }

    public String getSpecialCode() {
        return specialCode;
    }

    public Client(String login, String password, String role, String specialCode) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.specialCode = specialCode;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}