package com.hunter.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hunter.web.model.StockOut;

public interface StockOutRepo extends JpaRepository<StockOut, Long>, JpaSpecificationExecutor<StockOut>{
	
	Page<StockOut> findAllByOrderByIdDesc(Pageable pageable);
	
	@Query("FROM StockOut so where so.customer = (FROM Customer cust where cust.id = :custId)")
	Page<StockOut> findAllStockOutsByCustomerId(Pageable pageable, Long custId);
	
	@Query("FROM StockOut so WHERE EXISTS (SELECT 1 FROM StockOutProduct sop, Product p WHERE sop.product = p.id AND sop.stockOut = so.id AND p.name = :name AND p.size = :pSize AND p.colour = :colour AND p.brand = :brand )")
	Page<StockOut> findAllStockOutsByProductDetails(Pageable pageable, String name, String pSize, String colour, String brand);
	
}
