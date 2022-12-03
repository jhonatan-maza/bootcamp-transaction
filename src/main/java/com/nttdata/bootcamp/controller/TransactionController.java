package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Commission;
import com.nttdata.bootcamp.service.CommissionService;
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
	@Autowired
	private CommissionService commissionService;

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
	@GetMapping("/findByTransactionNumber/{numberTransaction}")
	public Mono<Transaction> findByTransactionNumber(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Searching transaction by numberTransaction: " + numberTransaction);
		return transactionService.findByNumber(numberTransaction);
	}

	//Save transaction
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
	@DeleteMapping("/deleteTransaction/{numberTransaction}")
	public Mono<Void> deleteTransaction(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Deleting transaction by numberTransaction: " + numberTransaction);
		Mono<Void> delete = transactionService.delete(numberTransaction);
		return delete;

	}

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


	@GetMapping("/findAllCommission")
	public Flux<Commission> findAllCommission() {
		Flux<Commission> commissions = commissionService.findAll();
		LOGGER.info("Registered commissions: " + commissions);
		return commissions;
	}

	//Transactions by AccountNumber
	@GetMapping("/findAllCommissionByNumber/{accountNumber}")
	public Flux<Commission> findAllCommissionByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		Flux<Commission> comissions = commissionService.findByAccountNumber(accountNumber);
		LOGGER.info("Registered commission of account number: "+accountNumber +"-" + comissions);
		return comissions;
	}

	//Transaction  by transactionNumber
	@GetMapping("/findByTransactionNumber/{numberTransaction}")
	public Mono<Commission> findCommissionByTransactionNumber(@PathVariable("numberTransaction") String numberTransaction) {
		LOGGER.info("Searching transaction by numberTransaction: " + numberTransaction);
		return commissionService.findByNumber(numberTransaction);
	}

	//Save transaction
	@PostMapping(value = "/saveCommission")
	public Mono<Commission> saveCommission(@RequestBody Commission dataCommission){
		Mono.just(dataCommission).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataCommission).onErrorResume(e -> Mono.just(dataCommission))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Commission> commissionMono = commissionService.save(dataCommission);
		return commissionMono;
	}

	//Update active
	@PutMapping("/updateCommission/{numberCode}")
	public Mono<Commission> updateCommission(@PathVariable("numberCode") String numberCode,
											   @Valid @RequestBody Commission dataCommission) {
		Mono.just(dataCommission).doOnNext(t -> {

					t.setCode(numberCode);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataCommission).onErrorResume(e -> Mono.just(dataCommission))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Commission> updateCommission = commissionService.update(dataCommission);
		return updateCommission;
	}


	//Delete customer
	@DeleteMapping("/deleteCommission/{numberCode}")
	public Mono<Void> deleteCommission(@PathVariable("numberCode") String numberCode) {
		LOGGER.info("Deleting Commission by numberTransaction: " + numberCode);
		Mono<Void> delete = transactionService.delete(numberCode);
		return delete;

	}


}
