package org.example.version2_xpresbank.Repository;

import org.example.version2_xpresbank.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
