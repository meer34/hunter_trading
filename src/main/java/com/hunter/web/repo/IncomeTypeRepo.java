package com.hunter.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hunter.web.model.IncomeType;

public interface IncomeTypeRepo extends JpaRepository<IncomeType, Long>{
	
}
