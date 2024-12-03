package org.example.version2_xpresbank.Repository;

import org.example.version2_xpresbank.Entity.Transaction;
import org.example.version2_xpresbank.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountId(Long accountId);
    List<Transaction> findByDestinationAccountId(Long accountId);
    List<Transaction> findByStatus(String status);
    List<Transaction> findByCreatedBy(User user);
}
