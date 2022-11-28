package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {
}
