package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Transaction;
import com.nttdata.bootcamp.repository.TransactionRepository;
import com.nttdata.bootcamp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

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
        Mono<Transaction> activeMono= findByNumber(dataTransaction.getTransactionNumber())
                .flatMap(__ -> Mono.<Transaction>error(new Error("La transaccion " + dataTransaction.getTransactionNumber() + " YA EXISTE")))
                .switchIfEmpty(transactionRepository.save(dataTransaction));
        return activeMono;
    }

    @Override
    public Mono<Transaction> update(Transaction dataTransaction) {

        Mono<Transaction> transactionMono = findByNumber(dataTransaction.getTransactionNumber());
        try {
            dataTransaction.setDni(transactionMono.block().getDni());
            dataTransaction.setAmount(transactionMono.block().getAmount());
            dataTransaction.setCreationDate(transactionMono.block().getCreationDate());
            return transactionRepository.save(dataTransaction);
        }catch (Exception e){
            return Mono.<Transaction>error(new Error("La transaccion " + dataTransaction.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> delete(String Number) {
        Mono<Transaction> transactionMono = findByNumber(Number);
        try {
            Transaction transaction = transactionMono.block();
            return transactionRepository.delete(transaction);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("La transaccion numero" + Number+ " NO EXISTE"));
        }
    }

    /*public Mono<Transaction> searchByPasiveAccount(Transaction dataTransaction){
        Mono<Transaction> savingTransaction = transactionRepository
                .findAll()
                .filter(x -> dataTransaction.getPassiveFixedTerm() ||dataTransaction.getPassiveSaving() || dataTransaction.getPassiveCurrentAccount())
                .next();
        return savingTransaction;

    }
    /*public Mono<Transaction> searchByActiveAccount(Transaction dataTransaction){
        Mono<Transaction> savingTransaction = transactionRepository
                .findAll()
                .filter(x -> dataTransaction.getActiveBusiness() ||  dataTransaction.getActiveStaff() || dataTransaction.getActiveCreditCard() )
                .next();
        return savingTransaction;
    }*/
    /*public Mono<Transaction> searchByActiveAccountBalance(Transaction dataTransaction, Double balance){
        Mono<Transaction> savingTransaction = transactionRepository
                .findAll()
                .filter(x -> dataTransaction.getActiveCreditCard() && dataTransaction.getAmount()<=balance)
                .next();
        return savingTransaction;
    }*/
    @Override
    public Mono<Transaction> saveTransaction(Transaction dataTransaction,String typeTransaction ) {
        /*Mono<Transaction> transaction = Mono.empty();

        if(typeAccount.equals("PASSIVE")){
            transaction = this.searchByPasiveAccount(dataTransaction);
        }else if(typeAccount.equals("ACTIVE")){
            transaction = this.searchByActiveAccount(dataTransaction);
        }*/

        dataTransaction.setStatus("active");

        if(typeTransaction.equals("deposit")){
            dataTransaction.setDeposit(true);
            dataTransaction.setWithdraw(false);
        }
        else if(typeTransaction.equals("withdraw")){
            dataTransaction.setDeposit(false);
            dataTransaction.setWithdraw(true);
        }
        else if(typeTransaction.equals("payment")){
            dataTransaction.setDeposit(true);
            dataTransaction.setWithdraw(false);
        }


        /*return transaction
                .flatMap(__ -> Mono.<Transaction>error(new Error("No se encontro la cuenta bancaria")))
                .switchIfEmpty(transactionRepository.save(dataTransaction));*/
        return transactionRepository.save(dataTransaction);



    }
    @Override
    public Mono<Transaction> saveConsumption(Transaction dataTransaction, Double balance ) {

        Mono<Transaction> transaction = Mono.empty();
        if(balance<dataTransaction.getAmount())
            transaction= Mono.just(dataTransaction);

        dataTransaction.setStatus("active");
        dataTransaction.setDeposit(false);
        dataTransaction.setWithdraw(true);
        dataTransaction.setCreationDate(new Date());
        dataTransaction.setModificationDate(new Date());


        return transaction
                .flatMap(__ -> Mono.<Transaction>error(new Error("The customer doesnt have enought credit for this transaction")))
                .switchIfEmpty(transactionRepository.save(dataTransaction));


    }


}
