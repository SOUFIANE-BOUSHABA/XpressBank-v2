package org.example.version2_xpresbank.Service;

import jakarta.transaction.Transactional;
import org.example.version2_xpresbank.DTO.CreateTransactionDTO;
import org.example.version2_xpresbank.Entity.Account;
import org.example.version2_xpresbank.Entity.Enums.Frequency;
import org.example.version2_xpresbank.Entity.Enums.TransactionType;
import org.example.version2_xpresbank.Entity.Transaction;
import org.example.version2_xpresbank.Entity.User;
import org.example.version2_xpresbank.Exception.AccountNotFoundException;
import org.example.version2_xpresbank.Exception.InsufficientFundsException;
import org.example.version2_xpresbank.Mapper.TransactionMapper;
import org.example.version2_xpresbank.Repository.AccountRepository;
import org.example.version2_xpresbank.Repository.TransactionRepository;
import org.example.version2_xpresbank.VM.TransactionVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Autowired
    public TransactionService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }


    @Transactional
    public TransactionVM createTransaction(CreateTransactionDTO createTransactionDTO, User createdByUser) {
        Account sourceAccount = accountRepository.findByAccountNumber(createTransactionDTO.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(createTransactionDTO.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

        if (sourceAccount.getBalance() < createTransactionDTO.getAmount()) {
            throw new InsufficientFundsException("Insufficient funds in source account");
        }

        Frequency frequency = null;
        if (createTransactionDTO.getFrequency() != null) {
            frequency = Frequency.valueOf(createTransactionDTO.getFrequency().toUpperCase());
        }

        Transaction transaction = transactionMapper.toEntity(createTransactionDTO, sourceAccount, destinationAccount);
        transaction.setCreatedBy(createdByUser);

        if (frequency != null) {
            transaction.setFrequency(frequency);
        }

        transaction.setEndDate(createTransactionDTO.getEndDate());

        if (createTransactionDTO.getType().equalsIgnoreCase("SCHEDULED")) {
            transaction.setNextScheduledDate(new Date());
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toTransactionVM(savedTransaction, "Transaction created successfully, awaiting approval");
    }




    @Transactional
    public TransactionVM approveTransaction(Long transactionId) {
        try {
            System.out.println("Started approveTransaction for ID: " + transactionId);

            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));
            System.out.println("Transaction fetched successfully with status: " + transaction.getStatus());

            if (!transaction.getStatus().equals("PENDING")) {
                throw new IllegalStateException("Transaction is not in a pending state");
            }

            Account sourceAccount = transaction.getSourceAccount();
            System.out.println("Fetched source account with balance: " + sourceAccount.getBalance());

            Account destinationAccount = transaction.getDestinationAccount();
            System.out.println("Fetched destination account");

            if (sourceAccount.getBalance() < transaction.getAmount() + transaction.getTransactionFee()) {
                throw new InsufficientFundsException("Insufficient funds to approve the transaction");
            }

            sourceAccount.setBalance(sourceAccount.getBalance() - transaction.getAmount() - transaction.getTransactionFee());
            destinationAccount.setBalance(destinationAccount.getBalance() + transaction.getAmount());

            System.out.println("Updating account balances...");

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transaction.setStatus("APPROVED");
            Transaction approvedTransaction = transactionRepository.save(transaction);

            System.out.println("Transaction approved and saved successfully");

            return transactionMapper.toTransactionVM(approvedTransaction, "Transaction approved successfully");
        } catch (Exception e) {
            System.out.println("Error in approveTransaction method: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }




    @Transactional
    public TransactionVM rejectTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with ID: " + transactionId));

        if (!transaction.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Transaction is not in a pending state");
        }

        transaction.setStatus("REJECTED");
        Transaction rejectedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionVM(rejectedTransaction, "Transaction rejected successfully");
    }

    @Transactional
    public List<TransactionVM> getAllTransactions() {
        List<Transaction> allTransactions = transactionRepository.findAll();
        return allTransactions.stream()
                .map(transaction -> transactionMapper.toTransactionVM(transaction, "Transaction fetched successfully"))
                .collect(Collectors.toList());
    }







    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void processRecurringTransactions() {
        List<Transaction> recurringTransactions = transactionRepository.findByStatus("PENDING");
        Date currentDate = new Date();

        for (Transaction transaction : recurringTransactions) {
            if (transaction.getType() == TransactionType.SCHEDULED &&
                    transaction.getNextScheduledDate() != null &&
                    transaction.getNextScheduledDate().before(currentDate) &&
                    (transaction.getEndDate() == null || transaction.getEndDate().after(currentDate))) {

                Date nextDate = calculateNextScheduledDate(transaction);
                transaction.setNextScheduledDate(nextDate);

                if (nextDate != null && (transaction.getEndDate() == null || nextDate.before(transaction.getEndDate()))) {
                    transaction.setStatus("PENDING");
                } else {
                    transaction.setStatus("COMPLETED");
                }

                transactionRepository.save(transaction);
            }
        }
    }

    private Date calculateNextScheduledDate(Transaction transaction) {
        if (transaction.getFrequency() == Frequency.MONTHLY) {
            return new Date(transaction.getNextScheduledDate().getTime() + 30L * 24 * 60 * 60 * 1000);
        } else if (transaction.getFrequency() == Frequency.WEEKLY) {
            return new Date(transaction.getNextScheduledDate().getTime() + 7L * 24 * 60 * 60 * 1000);
        } else {
            return null;
        }
    }


    @Transactional
    public List<TransactionVM> getUserTransactions(User user) {
        List<Transaction> userTransactions = transactionRepository.findByCreatedBy(user);
        return userTransactions.stream()
                .map(transaction -> transactionMapper.toTransactionVM(transaction, "Transaction fetched successfully"))
                .collect(Collectors.toList());
    }


}
