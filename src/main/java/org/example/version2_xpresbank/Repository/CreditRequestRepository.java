package org.example.version2_xpresbank.Repository;

import org.example.version2_xpresbank.Entity.CreditRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditRequestRepository extends JpaRepository<CreditRequest, Long> {

    List<CreditRequest> findByUserId(Long userId);
}
