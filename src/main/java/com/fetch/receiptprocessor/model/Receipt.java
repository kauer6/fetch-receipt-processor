package com.fetch.receiptprocessor.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class Receipt {
	
	@NotBlank
	//@Pattern(regexp = "^\\S+$")
	private String retailer;
	
	@NotBlank
	private String purchaseDate;
	
	@NotBlank
	private String purchaseTime;
	
	@NotEmpty
	private List<Item> items;
	
	@NotBlank
	@Pattern(regexp = "^\\d+\\.\\d{2}$")
	private String total;
	
	
	public String getRetailer() {
		return retailer;
	}
	public void setRetailer(String retailer) {
		this.retailer = retailer;
	}
	public String getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public String getPurchaseTime() {
		return purchaseTime;
	}
	public void setPurchaseTime(String purchaseTime) {
		this.purchaseTime = purchaseTime;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
}
