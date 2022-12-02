package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Commission;
import com.nttdata.bootcamp.entity.Transaction;
import com.nttdata.bootcamp.repository.CommissionRepository;
import com.nttdata.bootcamp.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CommissionServiceImpl implements CommissionService{

    @Autowired
    private CommissionRepository commissionRepository;
    @Override
    public Flux<Commission> findAll() {
        Flux<Commission> commissions = commissionRepository.findAll();
        return commissions;
    }

    @Override
    public Flux<Commission> findByAccountNumber(String accountNumber) {
        Flux<Commission> transactions = commissionRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber));
        return transactions;
    }

    @Override
    public Mono<Commission> findByNumber(String number) {
        Mono<Commission> commissionMono = commissionRepository
                .findAll()
                .filter(x -> x.getCode().equals(number))
                .next();
        return commissionMono;
    }

    @Override
    public Mono<Commission> save(Commission datacommission) {
        Mono<Commission> activeMono= findByNumber(datacommission.getCode())
                .flatMap(__ -> Mono.<Commission>error(new Error("La commission " + datacommission.getCode() + " YA EXISTE")))
                .switchIfEmpty(commissionRepository.save(datacommission));
        return activeMono;
    }

    @Override
    public Mono<Commission> update(Commission datacommission) {
        Mono<Commission> commissionMono = findByNumber(datacommission.getCode());
        try {
            datacommission.setDni(commissionMono.block().getDni());
            datacommission.setAmount(commissionMono.block().getAmount());
            datacommission.setCreationDate(commissionMono.block().getCreationDate());
            return commissionRepository.save(datacommission);
        }catch (Exception e){
            return Mono.<Commission>error(new Error("The commission number" + datacommission.getCode() + " does not exists"));
        }
    }

    @Override
    public Mono<Void> delete(String code) {
        Mono<Commission> commissionMono = findByNumber(code);
        try {
            Commission transaction = commissionMono.block();
            return commissionRepository.delete(transaction);
        }
        catch (Exception e){
            return Mono.<Void>error(new Error("The commission number" + code+ " does not exists"));
        }
    }
}
