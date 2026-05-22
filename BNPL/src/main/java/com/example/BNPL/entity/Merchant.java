package com.example.BNPL.entity;



import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "merchants")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Merchant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantId;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    private String category;
    private String registrationDetails;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    private List<Product> products;
}
