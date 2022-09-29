package com.hunter.web.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class StockOutProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long stockInProduct;
	private int quantity;
	private double rate;
	private double amount;

	@OneToOne
	@JoinColumn(name="product")
	private Product product;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="stockOut")
	private StockOut stockOut;

	public StockOutProduct(String[] arr, StockOut stockOut, Product product) {
		if(arr[0] != null && !"".equals(arr[0])) this.id = Long.parseLong(arr[0]);
		this.stockInProduct = Long.parseLong(arr[1]);
		this.product = product;
		this.stockOut = stockOut;
		
		this.quantity = Integer.valueOf(arr[2]!=""? arr[2]: "0");
		this.rate = Double.valueOf(arr[3]!=""? arr[3]: "0");
		this.amount = Double.valueOf(arr[4]!=""? arr[4]: "0");
	}

}
