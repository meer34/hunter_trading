package com.hunter.web.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hunter.web.model.Product;

public interface ProductRepo extends JpaRepository<Product, Long>{

	@Query("SELECT DISTINCT name FROM Product")
	List<String> findProductNames();

	@Query("SELECT DISTINCT size FROM Product where name = :name")
	List<String> findSizesForName(String name);

	@Query("SELECT DISTINCT colour FROM Product where name = :name AND size = :size")
	List<String> findColoursForNameAndSize(String name,String size);

	@Query("SELECT id, brand FROM Product where name = :name AND size = :size AND colour = :colour")
	List<String[]> findBrandsForNameAndSizeAndColour(String name,String size, String colour);

	@Query("SELECT SUM(p.quantity) - COALESCE(SUM(sop.quantity), 0) FROM Product p LEFT JOIN StockOutProduct sop ON p.id = sop.stockInProduct "
			+ "WHERE p.id = :productId")
	String findQuantityForProductId(Long productId);

	@Modifying
	@Transactional
	@Query("delete FROM Product p where p.stockIn = (FROM StockIn si where si.id = :stockInId) AND p.id NOT IN :currentChildIds "
			+ "AND NOT EXISTS (SELECT 1 FROM StockOutProduct sop where sop.stockInProduct = p.id)") //sop.name = p.name AND sop.size = p.size AND sop.colour = p.coolour AND sop.brand = p.brand
	void deleteStockInOrphanChilds(Long stockInId, List<Long> currentChildIds);

	@Modifying
	@Transactional
	@Query("delete FROM StockOutProduct sop where sop.stockOut = (FROM StockOut so where so.id = :stockOutId) AND sop.id NOT IN :currentChildIds")
	void deleteStockOutOrphanChilds(long stockOutId, List<Long> currentChildIds);
	
	Product findFirstByScanCodeOrderByIdDesc(String scanCode);


}
