package com.hunter.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hunter.web.model.ExpenseType;

public interface ExpenseTypeRepo extends JpaRepository<ExpenseType, Long>{
	
}
