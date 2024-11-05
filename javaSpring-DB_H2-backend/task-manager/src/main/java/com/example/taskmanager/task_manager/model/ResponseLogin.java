package com.example.taskmanager.task_manager.model;

public class ResponseLogin {

    private String jwt; // Modifica: utilizza direttamente una stringa per il JWT
    private String role;

    public ResponseLogin(String role, String jwt) {
        this.role = role;
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
