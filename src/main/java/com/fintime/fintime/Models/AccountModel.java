package com.fintime.fintime.Models;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "accounts")
@Builder
public class AccountModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "account_name")
    private String accountName;
    private String currency;
    private BigDecimal balance;
    private String status;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
