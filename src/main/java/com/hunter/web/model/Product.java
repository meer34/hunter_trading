package com.hunter.web.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hunter.data.controller.DeleteEventListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(DeleteEventListener.class)
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(updatable=false)
	private Long remoteId;
	private boolean synced;
	
	private String name;
	private String scanCode;
	private String size;
	private String colour;
	private String brand;
	
	private String productType;
	private String productCode;
	private double mrp;
	private double sellRate;
	
	private double rate;
	private int quantity;
	private double amount;
	
	@OneToMany(mappedBy="product")
	@JsonIgnore
	private List<StockOutProduct> stockOutProductList;
	
	@ManyToOne
	@JoinColumn(name="stockIn")
	private StockIn stockIn;
	
	public Product(String[] arr, StockIn stockIn) {
		if(arr[0] != null && !"".equals(arr[0])) this.id = Long.parseLong(arr[0]);
		this.name = arr[1];
		this.size = arr[2];
		this.colour = arr[3];
		this.brand = arr[4];
		
		this.productType = arr[5];
		this.productCode = arr[6];
		this.mrp = Double.valueOf(arr[7]!=""? arr[7]: "0");
		this.sellRate = Double.valueOf(arr[8]!=""? arr[8]: "0");
		
		this.quantity = Integer.valueOf(arr[9]!=""? arr[9]: "0");
		this.rate = Double.valueOf(arr[10]!=""? arr[10]: "0");
		this.amount = Double.valueOf(arr[11]!=""? arr[11]: "0");
		this.scanCode = arr[12];
		
		this.stockIn = stockIn;
	}
	
	@Override
	public String toString() {
		return new StringBuilder(String.valueOf(id))
				.append("~").append(name)
				.append("~").append(scanCode)
				.append("~").append(size)
				.append("~").append(colour)
				.append("~").append(brand)
				.append("~").append(quantity)
				.append("~").append(rate)
				.append("~").append(amount)
				.toString();
	}

}
