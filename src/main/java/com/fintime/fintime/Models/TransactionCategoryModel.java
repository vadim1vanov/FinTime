package com.fintime.fintime.Models;


import com.fintime.fintime.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "transaction_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCategoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "category_name")
    private String name;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
}
