package org.example.version2_xpresbank.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.version2_xpresbank.Entity.Enums.InvoiceStatus;

import java.util.Date;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private String description;

    @SuppressWarnings("unused")
    @Column(nullable = false)
    private double amount;

    @SuppressWarnings("unused")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dueDate;

    @SuppressWarnings("unused")
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date issueDate;

    @SuppressWarnings("unused")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
