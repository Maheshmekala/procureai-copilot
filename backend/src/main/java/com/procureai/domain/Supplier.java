package com.procureai.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private Double annualSpend;
    private String contactEmail;
    private Integer riskScore;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Supplier() {}
    public Supplier(String name, String category, Double annualSpend) {
        this.name = name; this.category = category; this.annualSpend = annualSpend;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getAnnualSpend() { return annualSpend; }
    public void setAnnualSpend(Double annualSpend) { this.annualSpend = annualSpend; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String email) { this.contactEmail = email; }
    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
