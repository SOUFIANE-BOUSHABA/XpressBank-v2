package org.example.version2_xpresbank.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCreditRequestDTO {
    private double amount;
    private double interestRate;
    private String startDate;
    private String endDate;
}
