package com.hunter.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hunter.web.model.Customer;

public interface CustomerRepo extends JpaRepository<Customer, Long>{
	
}
