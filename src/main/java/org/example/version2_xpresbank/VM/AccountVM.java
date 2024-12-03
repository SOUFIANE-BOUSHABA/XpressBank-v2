package org.example.version2_xpresbank.VM;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVM {
    private Long id;
    private String accountNumber;
    private double balance;
    private String status;
    private String message;
}
