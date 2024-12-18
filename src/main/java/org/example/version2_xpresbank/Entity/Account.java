package org.example.version2_xpresbank.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.version2_xpresbank.Entity.Enums.AccountStatus;

import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private double balance;

    @SuppressWarnings("unused")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @SuppressWarnings("unused")
    @OneToMany(mappedBy = "sourceAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> outgoingTransactions;

    @SuppressWarnings("unused")
    @OneToMany(mappedBy = "destinationAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> incomingTransactions;
}


