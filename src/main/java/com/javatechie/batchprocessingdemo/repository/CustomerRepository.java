package com.javatechie.batchprocessingdemo.repository;

import com.javatechie.batchprocessingdemo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Integer> {
}
