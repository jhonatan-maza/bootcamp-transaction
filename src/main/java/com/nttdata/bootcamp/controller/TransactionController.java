package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Commission;
import com.nttdata.bootcamp.service.CommissionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
	public Flux<Transaction> findAllTransactions() {
		Flux<Transaction> transactions = transactionService.findAll();
		LOGGER.info("Registered transactions: " + transactions);
		return transactions;
	}

	//Transactions by AccountNumber
	@GetMapping("/findAllTransactionByNumber/{accountNumber}")
	public Flux<Transaction> findAllTransactionByNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Transaction> transactions = transactionService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered Transactions of account number: "+accountNumber +"-" + transactions);
		return transactions;
	}

	//Transaction  by transactionNumber
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@GetMapping("/findByTransactionNumber/{numberTransaction}")
	public Mono<Transaction> findByTransactionNumber(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Searching transaction by numberTransaction: " + numberTransaction);
		return transactionService.findByNumber(numberTransaction);
	}

	//Save transaction
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@PostMapping(value = "/saveTransaction")
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
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@PutMapping("/updateTransaction/{numberTransaction}")
	public Mono<Transaction> updateTransaction(@PathVariable("numberTransaction") String numberTransaction,
									 @Valid @RequestBody Transaction dataTransaction) {
		Mono.just(dataTransaction).doOnNext(t -> {

					t.setTransactionNumber(numberTransaction);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransaction).onErrorResume(e -> Mono.just(dataTransaction))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transaction> updateTransaction = transactionService.update(dataTransaction);
		return updateTransaction;
	}


	//Delete customer
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@DeleteMapping("/deleteTransaction/{numberTransaction}")
	public Mono<Void> deleteTransaction(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting transaction by numberTransaction: " + numberTransaction);
		Mono<Void> delete = transactionService.delete(numberTransaction);
		return delete;

	}
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@PostMapping(value = "/saveDeposit")
	public Mono<Transaction> saveDeposit(@Valid @RequestBody Transaction dataTransaction){

		Mono.just(dataTransaction).doOnNext(t -> {
					t.setDeposit(true);
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransaction).onErrorResume(e -> Mono.just(dataTransaction))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transaction> newTransaction = transactionService.saveTransaction(dataTransaction, "deposit");
		return newTransaction;
	}
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@PostMapping(value = "/savedWithdraw")
	public Mono<Transaction> savedWithdraw(@Valid @RequestBody Transaction dataTransaction){

		Mono.just(dataTransaction).doOnNext(t -> {
					t.setWithdraw(true);
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransaction).onErrorResume(e -> Mono.just(dataTransaction))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transaction> newTransaction = transactionService.saveTransaction(dataTransaction, "withdraw");
		return newTransaction;
	}

	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@PostMapping(value = "/savePayment")
	public Mono<Transaction> savePayment(@Valid @RequestBody Transaction dataTransaction){

		Mono.just(dataTransaction).doOnNext(t -> {
					t.setDeposit(true);
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataTransaction).onErrorResume(e -> Mono.just(dataTransaction))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Transaction> newTransaction = transactionService.saveTransaction(dataTransaction, "payment");
		return newTransaction;
	}
	@CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetTransaction")
	@PostMapping(value = "/saveConsumption/{balance}")
	public Mono<Transaction> saveConsumption(@Valid @RequestBody Transaction dataTransaction, @PathVariable("balance") Double balance ){
		Mono<Transaction> newTransaction = transactionService.saveConsumption(dataTransaction, balance);
		return newTransaction;
	}

	@GetMapping("/getCountTransaction/{accountNumber}")
	//get count of transaction
	public Mono<Long> getCountTransaction(@PathVariable("accountNumber") String accountNumber){
		Flux<Transaction> transactions= findAllTransactionByNumber(accountNumber);
		return transactions.count();
	}


	private Mono<Transaction> fallBackGetCurrent(Exception e){
		Transaction activeStaff= new Transaction();
		Mono<Transaction> staffMono= Mono.just(activeStaff);
		return staffMono;
	}


}
