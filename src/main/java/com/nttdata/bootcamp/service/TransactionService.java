package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface TransactionService {

    public Flux<Transaction> findAll();
    public Flux<Transaction> findByAccountNumber(String accountNumber);

    public Mono<Transaction> findByNumber(String number);
    public Mono<Transaction> save(Transaction active);
    public Mono<Transaction> update(Transaction dataActive);
    public Mono<Void> delete(String accountNumber);
    public Mono<Transaction> saveTransaction(Transaction dataTransaction, String typeTransaction);
    public Mono<Transaction> saveConsumption(Transaction dataTransaction, Double balance );

}
