package com.hunter.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hunter.web.model.Expense;

public interface ExpenseRepo extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
	
	@Query("FROM Expense expense WHERE expense.expenseType = (FROM ExpenseType expenseType WHERE expenseType.id = :expenseTypeId)")
	Page<Expense> findAllForType(Pageable pageable, Long expenseTypeId);

}
