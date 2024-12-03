package org.example.version2_xpresbank.VM;



import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsVM {

    private long totalUsers;
    private long totalTransactions;
    private double totalAccountBalance;


}
