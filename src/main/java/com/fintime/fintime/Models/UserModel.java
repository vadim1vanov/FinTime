package com.fintime.fintime.Models;


import com.fintime.fintime.Enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Setter
@Getter
@Entity
@Builder
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String username;
    private String password;
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "count_accounts")
    private Long countAccounts=0L;

}
