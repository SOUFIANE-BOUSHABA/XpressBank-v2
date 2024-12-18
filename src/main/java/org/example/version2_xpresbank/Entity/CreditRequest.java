package org.example.version2_xpresbank.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.version2_xpresbank.Entity.Enums.CreditRequestStatus;

import java.util.Date;

@Entity
@Table(name = "credit_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private double amount;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private double interestRate;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @SuppressWarnings("unused")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditRequestStatus status;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private String eligibilityStatus;
}


