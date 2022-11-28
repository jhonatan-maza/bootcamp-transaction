package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Transaction;
import com.nttdata.bootcamp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Flux<Transaction> findAll() {
        Flux<Transaction> transactions = transactionRepository.findAll();
        return transactions;
    }

    @Override
    public Flux<Transaction> findByAccountNumber(String accountNumber) {
        Flux<Transaction> transactions = transactionRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return transactions;
    }

    @Override
    public Mono<Transaction> findByNumber(String Number) {
        Mono<Transaction> transaction = transactionRepository
                .findAll()
                .filter(x -> x.getTransactionNumber().equals(Number))
                .next();
        return transaction;
    }


    @Override
    public Mono<Transaction> save(Transaction dataTransaction) {
        return transactionRepository.save(dataTransaction);
    }

    @Override
    public Mono<Transaction> update(Transaction dataTransaction) {
        Mono<Transaction> transactionMono = findByNumber(dataTransaction.getTransactionNumber());
        Transaction active = transactionMono.block();
        active.setStatus(dataTransaction.getStatus());
        return transactionRepository.save(active);
    }

    @Override
    public Mono<Void> delete(String accountNumber) {
        Mono<Transaction> activeMono = findByNumber(accountNumber);
        Transaction active = activeMono.block();
        return transactionRepository.delete(active);
    }

}
