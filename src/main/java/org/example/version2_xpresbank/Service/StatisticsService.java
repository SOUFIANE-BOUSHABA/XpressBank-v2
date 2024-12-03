package org.example.version2_xpresbank.Service;

import org.example.version2_xpresbank.Repository.AccountRepository;
import org.example.version2_xpresbank.Repository.TransactionRepository;
import org.example.version2_xpresbank.Repository.UserRepository;
import org.example.version2_xpresbank.VM.StatisticsVM;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;


    public StatisticsService(UserRepository userRepository,
                             TransactionRepository transactionRepository,
                             AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public StatisticsVM getStatistics() {
        long totalUsers = userRepository.count();
        long totalTransactions = transactionRepository.count();
        double totalAccountBalance = accountRepository.sumAllBalances();

        return new StatisticsVM(totalUsers, totalTransactions, totalAccountBalance);
    }
}
