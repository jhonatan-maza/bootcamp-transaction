package com.nttdata.bootcamp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nttdata.bootcamp.entity.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transaction")
public class TransactionController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
	@Autowired
	private TransactionService transactionService;

	//Transactions search
	@GetMapping("/")
	public Flux<Transaction> findAllActives() {
		Flux<Transaction> transactions = transactionService.findAll();
		LOGGER.info("Registered transactions: " + transactions);
		return transactions;
	}

	//Transactions search by accountNumber
	@GetMapping("/findAllTransactionByNumber/{accountNumber}")
	public Flux<Transaction> findAllTransactionByNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Transaction> transactions = transactionService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered Actives Products by customer of dni: "+accountNumber +"-" + transactions);
		return transactions;
	}

	//Search for active by AccountNumber
	@GetMapping("/findbyNumber/{numberTransaction}")
	public Mono<Transaction> findByAccountNumber(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Searching transaction by numberTransaction: " + numberTransaction);
		return transactionService.findByNumber(numberTransaction);
	}

	//Save transaction
	@PostMapping(value = "/save")
	public Mono<Transaction> saveTransaction(@RequestBody Transaction dataTransaction){
		Mono.just(dataTransaction).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransaction).onErrorResume(e -> Mono.just(dataTransaction))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transaction> transactionMono = transactionService.save(dataTransaction);
		return transactionMono;
	}

	//Update active
	@PutMapping("/update/{numberTransaction}")
	public Mono<Transaction> updateTransaction(@PathVariable("numberTransaction") String numberTransaction,
									 @Valid @RequestBody Transaction dataTransaction) {
		Mono.just(dataTransaction).doOnNext(t -> {

					t.setAccountNumber(numberTransaction);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransaction).onErrorResume(e -> Mono.just(dataTransaction))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transaction> updateTransaction = transactionService.update(dataTransaction);
		return updateTransaction;
	}


	//Delete customer
	@DeleteMapping("/delete/{numberTransaction}")
	public Mono<Void> deleteTransaction(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting transaction by numberTransaction: " + numberTransaction);
		Mono<Void> delete = transactionService.delete(numberTransaction);
		return delete;

	}
	@PostMapping(value = "/savePayment")
	public Mono<Transaction> savePayment(@RequestBody Transaction dataTransaction){
		boolean typeTransaction= dataTransaction.getDeposit();
		String typeAccount= dataTransaction.getAccountType();
		Mono<Transaction> transactionMono = transactionService.save(dataTransaction);

		if(typeTransaction && typeAccount.compareTo("CREDITO") == 0) {
			return transactionMono;
		}
		return transactionMono;

	}

}
