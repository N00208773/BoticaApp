package com.example.boticaapp.models;


public class User {
    private int id;
    private String email;
    private String name;
    private String role;          // "cliente", "empleado" o "admin"
    private Integer pharmacyId;   // solo para empleados
    private String createdAt;     // opcional, si lo devuelve el API

    public User() { }  // constructor vacío para Gson

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getPharmacyId() { return pharmacyId; }
    public void setPharmacyId(Integer pharmacyId) { this.pharmacyId = pharmacyId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
