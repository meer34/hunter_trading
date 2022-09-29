package com.hunter.web.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.hunter.web.repo.ProductRepo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class StockIn {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String billNo;
	private Integer totalQuantity;
	private Double totalPrice;
	private String remarks;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="mahajan")
	private Party mahajan;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	@Transient
	private List<String> stockInParts;

	@OneToMany(mappedBy="stockIn", cascade = CascadeType.ALL)
	private List<Product> productList;

	public void processParts(ProductRepo rollRepo) {
		
		this.totalQuantity = 0;
		this.totalPrice = 0.0;
		
		if(this.stockInParts != null) {
			this.productList = new ArrayList<>();
			Product newProduct = null;

			List<Long> currentChildIds = new ArrayList<>();

			for (String stockInPartString : stockInParts) {
				String[] arr = stockInPartString.split("\\|\\~\\|", -1);
				newProduct = new Product(arr, this);
				
				this.productList.add(newProduct);
				currentChildIds.add(newProduct.getId());
				
				totalQuantity += Integer.valueOf(arr[5]!=""? arr[5]: "0");
				totalPrice += Double.valueOf(arr[7]!=""? arr[7]: "0");
			}

			if(this.id != 0) rollRepo.deleteStockInOrphanChilds(this.id, currentChildIds);

		}
	}

}
