package com.example.boticaapp.models;

import com.google.gson.annotations.SerializedName;

public class Medicine {
    @SerializedName("id")
    private int id;

    @SerializedName("pharmacy_id")
    private int pharmacyId;      // ← Nuevo

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private double price;

    @SerializedName("stock")
    private int stock;

    @SerializedName("created_at")
    private String createdAt;

    // Getters y setters:

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getPharmacyId() {
        return pharmacyId;
    }
    public void setPharmacyId(int pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
