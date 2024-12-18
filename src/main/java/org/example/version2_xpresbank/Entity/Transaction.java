package org.example.version2_xpresbank.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.version2_xpresbank.Entity.Enums.Frequency;
import org.example.version2_xpresbank.Entity.Enums.TransactionType;

import java.util.Date;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @SuppressWarnings("unused")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private double amount;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private double transactionFee;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private String status;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextScheduledDate;

    @SuppressWarnings("unused")
    @Enumerated(EnumType.STRING)
    private Frequency frequency;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
}
