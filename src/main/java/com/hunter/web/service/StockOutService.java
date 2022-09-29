package com.hunter.web.service;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hunter.web.model.StockOut;
import com.hunter.web.repo.ProductRepo;
import com.hunter.web.repo.StockOutRepo;
import com.hunter.web.specification.StockOutSearchSpecification;
import com.hunter.web.util.SearchSpecificationBuilder;

@Service
public class StockOutService {

	@Autowired private StockOutRepo stockOutRepo;
	@Autowired private ProductRepo rollRepo;

	public StockOut saveStockOutToDB(StockOut stockOut) {
		stockOut.processParts(rollRepo);
		return stockOutRepo.save(stockOut);
	}

	public StockOut findStockOutById(Long id) {
		return stockOutRepo.findById(id).get();
	}

	public Page<StockOut> getAllStockOuts(Integer pageNo, Integer pageSize) {
		return stockOutRepo.findAllByOrderByIdDesc(PageRequest.of(pageNo, pageSize));
	}
	
	public Page<StockOut> getAllStockOutsForProduct(String name, String pSize, String colour, String brand, Integer pageNo, Integer pageSize) {
		return stockOutRepo.findAllStockOutsByProductDetails(PageRequest.of(pageNo, pageSize), name,pSize, colour, brand);
	}
	
	public Page<StockOut> getAllStockOutsForCustomer(Long custId, Integer pageNo, Integer pageSize) {
		return stockOutRepo.findAllStockOutsByCustomerId(PageRequest.of(pageNo, pageSize), custId);
	}

	public Page<StockOut> searchStockOutByDateAndKeyword(String keyword, 
			String fromDate, String toDate, int pageNo, Integer pageSize) throws ParseException {

		PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
		StockOutSearchSpecification spec = (StockOutSearchSpecification) SearchSpecificationBuilder.build(fromDate, toDate, keyword, StockOut.class);
		return stockOutRepo.findAll(spec, pageRequest);

	}

	public void deleteStockOutById(Long id) {
		stockOutRepo.deleteById(id);
	}

}
