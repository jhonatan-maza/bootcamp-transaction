package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Commission;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommissionService {
    public Flux<Commission> findAll();
    public Flux<Commission> findByAccountNumber(String accountNumber);
    public Mono<Commission> findByNumber(String number);
    public Mono<Commission> save(Commission commission);
    public Mono<Commission> update(Commission commission);
    public Mono<Void> delete(String code);
}
