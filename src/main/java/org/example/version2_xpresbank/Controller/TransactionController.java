package org.example.version2_xpresbank.Controller;

import org.example.version2_xpresbank.DTO.CreateTransactionDTO;
import org.example.version2_xpresbank.Entity.User;
import org.example.version2_xpresbank.Service.AuthService;
import org.example.version2_xpresbank.Service.TransactionService;
import org.example.version2_xpresbank.Utils.PermissionUtils;
import org.example.version2_xpresbank.VM.TransactionVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {

    private final TransactionService transactionService;
    private final PermissionUtils permissionUtils;
    private final AuthService authService;

    @Autowired
    public TransactionController(TransactionService transactionService, PermissionUtils permissionUtils ,  AuthService authService) {
        this.transactionService = transactionService;
        this.permissionUtils = permissionUtils;
        this.authService = authService;

    }

    @PostMapping("/create")
    public ResponseEntity<TransactionVM> createTransaction(@RequestHeader("Authorization") String authorizationHeader,
                                                           @RequestBody CreateTransactionDTO createTransactionDTO) {
        if (createTransactionDTO.getType() == null || createTransactionDTO.getType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        User createdByUser = authService.getUserFromSession(token);
        permissionUtils.checkUserPermission(authorizationHeader);
        TransactionVM transaction = transactionService.createTransaction(createTransactionDTO, createdByUser);
        return ResponseEntity.ok(transaction);
    }


    @PutMapping("/approve/{transactionId}")
    public ResponseEntity<?> approveTransaction(@RequestHeader("Authorization") String authorizationHeader,
                                                @PathVariable Long transactionId) {
        try {
            System.out.println("Started approving transaction: " + transactionId);

            String token = authorizationHeader.substring("Bearer ".length()).trim();
            System.out.println("Extracted token: " + token);

            User user = authService.getUserFromSession(token);
            System.out.println("User fetched from token: " + user.getUsername());

            permissionUtils.isAdminOrEmployee(user);
            System.out.println("User is authorized to approve transactions.");

            TransactionVM approvedTransaction = transactionService.approveTransaction(transactionId);
            System.out.println("Transaction approved successfully");

            return ResponseEntity.ok(approvedTransaction);
        } catch (Exception e) {
            System.out.println("Error occurred while approving transaction: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    @PutMapping("/reject/{transactionId}")
    public ResponseEntity<TransactionVM> rejectTransaction(@RequestHeader("Authorization") String authorizationHeader,   @PathVariable Long transactionId) {

        String token = authorizationHeader.substring("Bearer ".length()).trim();
        User user = authService.getUserFromSession(token);
        permissionUtils.isAdminOrEmployee(user);
        TransactionVM rejectedTransaction = transactionService.rejectTransaction(transactionId);
        return ResponseEntity.ok(rejectedTransaction);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TransactionVM>> getAllTransactions(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        User user = authService.getUserFromSession(token);
        permissionUtils.isAdminOrEmployee(user);
        List<TransactionVM> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user-transactions")
    public ResponseEntity<List<TransactionVM>> getUserTransactions(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length()).trim();
        User user = authService.getUserFromSession(token);
        List<TransactionVM> transactions = transactionService.getUserTransactions(user);
        return ResponseEntity.ok(transactions);
    }






}
