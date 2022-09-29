package com.hunter.web.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.hunter.web.model.StockIn;

public interface StockInRepo extends JpaRepository<StockIn, Long>, JpaSpecificationExecutor<StockIn>{
	
	Page<StockIn> findAllByOrderByIdDesc(Pageable pageable);
	
	@Query("FROM StockIn si WHERE EXISTS (SELECT 1 FROM Product p WHERE p.stockIn = si.id AND p.name = :name AND p.size = :pSize AND p.colour = :colour AND p.brand = :brand )")
	Page<StockIn> findAllStockInsByProductDetails(Pageable pageable, String name, String pSize, String colour, String brand);

}
