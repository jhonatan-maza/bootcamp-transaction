package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Commission;
import com.nttdata.bootcamp.entity.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CommissionRepository extends ReactiveCrudRepository<Commission, String> {
}
