package com.hunter.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hunter.web.model.Income;

public interface IncomeRepo extends JpaRepository<Income, Long>, JpaSpecificationExecutor<Income> {
	
	@Query("FROM Income income WHERE income.incomeType = (FROM IncomeType incomeType WHERE incomeType.id = :incomeTypeId)")
	Page<Income> findAllForType(Pageable pageable, Long incomeTypeId);

}
