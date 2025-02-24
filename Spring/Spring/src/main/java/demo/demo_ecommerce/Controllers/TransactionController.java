package demo.demo_ecommerce.Controllers;

import demo.demo_ecommerce.entities.Transaction;
import demo.demo_ecommerce.services.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import demo.demo_ecommerce.dtos.TransactionDTO;



@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Creazione di una nuova transazione
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        logger.info("Creating new transaction...");
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(201).body(createdTransaction);
    }

    // Recupera tutte le transazioni per ordine con paginazione
    // Recupera tutte le transazioni per ordine con paginazione
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Page<TransactionDTO>> getTransactionsByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionService.getTransactionsByOrderId(orderId, pageable);
        Page<TransactionDTO> dtoPage = transactions.map(TransactionDTO::fromEntity);

        return ResponseEntity.ok(dtoPage);
    }

}
