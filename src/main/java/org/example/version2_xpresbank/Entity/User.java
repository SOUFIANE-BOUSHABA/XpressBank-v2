package org.example.version2_xpresbank.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    @Column(nullable = false, unique = true)
    private String username;

    @SuppressWarnings("unused")
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @SuppressWarnings("unused")
    @Column(nullable = false, unique = true)
    private String email;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private boolean active;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @SuppressWarnings("unused")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Account> accounts;

    @SuppressWarnings("unused")
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> transactions;




    @SuppressWarnings("unused")
    @Column(nullable = false, columnDefinition = "integer default 18")
    private int age;

    @SuppressWarnings("unused")
    @Column(nullable = false, columnDefinition = "double precision default 1000.0")
    private double monthlyIncome;

    @SuppressWarnings("unused")
    @Column(nullable = false, columnDefinition = "integer default 600")
    private int creditScore;

    @SuppressWarnings("unused")
    @Column(nullable = false, columnDefinition = "double precision default 0.0")
    private double debtToIncomeRatio;

    @SuppressWarnings("unused")
    @Column(nullable = false, columnDefinition = "integer default 6")
    private int bankingDuration;


}
