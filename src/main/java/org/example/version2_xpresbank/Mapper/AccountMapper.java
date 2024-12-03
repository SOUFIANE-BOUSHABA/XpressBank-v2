package org.example.version2_xpresbank.Mapper;

import org.example.version2_xpresbank.DTO.AccountDTO;
import org.example.version2_xpresbank.Entity.Account;
import org.example.version2_xpresbank.Entity.Enums.AccountStatus;
import org.example.version2_xpresbank.VM.AccountVM;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountDTO toAccountDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .userName(account.getUser().getUsername())
                .status(account.getStatus().name())
                .build();
    }

    public Account fromAccountDTO(AccountDTO accountDTO) {
        return Account.builder()
                .accountNumber(accountDTO.getAccountNumber())
                .balance(accountDTO.getBalance())
                .status(AccountStatus.valueOf(accountDTO.getStatus()))
                .build();
    }

    public AccountVM toAccountVM(Account account, String message) {
        return AccountVM.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .status(account.getStatus().name())
                .message(message)
                .build();
    }
}
