package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Commission;
import com.nttdata.bootcamp.service.CommissionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping(value = "/commission")
public class CommissionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommissionController.class);
<<<<<<< HEAD

=======
    @Autowired
    private TransactionService transactionService;
>>>>>>> 0d511108547aba06161a3ae4ec2d3c1f35191238
    @Autowired
    private CommissionService commissionService;


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
    @CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetCommission")
    //Transaction  by transactionNumber
    @GetMapping("/findByCommissionNumber/{numberTransaction}")
    public Mono<Commission> findCommissionByTransactionNumber(@PathVariable("numberTransaction") String numberTransaction) {
        LOGGER.info("Searching transaction by numberTransaction: " + numberTransaction);
        return commissionService.findByNumber(numberTransaction);
    }

    @CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetCommission")
    //Transaction  by transactionNumber
    @GetMapping("/findAllCommissionByDate/{numberAccount}/{date1}/{date2}")
    public Flux<Commission> findAllCommissionByDate(@PathVariable("numberAccount") String numberAccount,
            @PathVariable("date1") Date date1,@PathVariable("date2") Date date2) {
        LOGGER.info("Searching commission by accountNumber: " + numberAccount);
        return commissionService.findByDate(numberAccount, date1, date2);
    }

    @CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetCommission")
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
    @CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetCommission")
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

    @CircuitBreaker(name = "transaction", fallbackMethod = "fallBackGetCommission")
    //Delete customer
    @DeleteMapping("/deleteCommission/{numberCode}")
    public Mono<Void> deleteCommission(@PathVariable("numberCode") String numberCode) {
        LOGGER.info("Deleting Commission by numberTransaction: " + numberCode);
        Mono<Void> delete = commissionService.delete(numberCode);
        return delete;

    }
    private Mono<Commission> fallBackGetCommission(Exception e){
        Commission activeStaff= new Commission();
        Mono<Commission> staffMono= Mono.just(activeStaff);
        return staffMono;
    }
}
