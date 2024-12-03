package org.example.version2_xpresbank.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean active;
    private String role;

    private int age;
    private double monthlyIncome;
    private int creditScore;
    private double debtToIncomeRatio;
    private int bankingDuration;
}
