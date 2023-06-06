package com.fetch.receiptprocessor.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.receiptprocessor.model.ErrorResponse;
import com.fetch.receiptprocessor.model.GetPointsResponse;
import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.ProcessReceiptResponse;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptNotFound;

import jakarta.validation.Valid;

@RestController
public class ReceiptController {
	
	HashMap<String, Integer> receipts = new HashMap<String, Integer>();
	ObjectMapper mapper = new ObjectMapper();
	
	@PostMapping("/receipts/process")
	public ResponseEntity<ProcessReceiptResponse> processReciepts(@Valid @RequestBody Receipt receipt) {
		UUID uuid = UUID.randomUUID();
		ProcessReceiptResponse receiptResponse = new ProcessReceiptResponse();
		receiptResponse.setId(uuid.toString());
		receipts.put(receiptResponse.getId(), calculatePoints(receipt));
		return new ResponseEntity<>(receiptResponse, HttpStatus.OK);
	}
	
	@GetMapping("/receipts/{id}/points")
	public ResponseEntity<GetPointsResponse> getPoints(@PathVariable String id) throws ReceiptNotFound {
		
		if (receipts.containsKey(id)) {
			Integer points = receipts.get(id);
			GetPointsResponse response = new GetPointsResponse();
			response.setPoints(points);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		throw new ReceiptNotFound();
		
		
	}
	
	private int calculatePoints(Receipt receipt) {
		int points = 0;
		//calculate points for retailer name
		for (int i = 0; i < receipt.getRetailer().length(); i++) {
			if (Character.isLetter(receipt.getRetailer().charAt(i))) {
				points++;
			}
		}
		
		//calculate if total is a round dollar amount with no cents for 50 points.
		if (Double.parseDouble(receipt.getTotal()) % 1 == 0.0) {
			points += 50;
		}
		
		//calculate if total is a multiple of 0.25 for 25 points
		if (Double.parseDouble(receipt.getTotal()) % 0.25 == 0.0) {
			points += 25;
		}
		
		//calculate for 2  items on the receipt for 5 points a pair.
		points += receipt.getItems().size() / 2 * 5;
		
		//Calculate if the item description is a multiple of 3.
		for (Item item : receipt.getItems()) {
			if (item.getShortDescription().trim().length() % 3 == 0) {
				points += Math.ceil(Double.parseDouble(item.getPrice()) * 0.2);
			}
		}
		
		//calculate if purchase date is odd for 6 points. This would be better using regex.
		String date = receipt.getPurchaseDate();
		if (Integer.parseInt(date.substring(8, 10)) % 2 != 0) {
			points+=6;
		}
		
		//calculate to see if time of purchase is between 2 and 4 pm for 10 points.
		LocalTime t = LocalTime.parse(receipt.getPurchaseTime());
		LocalTime d = LocalTime.parse("16:00");
		int timeDifference = (d.getHour() - t.getHour());
		if (timeDifference > 0 && timeDifference <= 1) {
			points += 10;
		}
		if (timeDifference == 2 && t.getMinute() != 0) {
			points += 10;
		}

		return points;
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleInvalidReceipt(MethodArgumentNotValidException exception) {
		ErrorResponse response = new ErrorResponse();
		response.setDescription("The receipt is invalid");
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ReceiptNotFound.class)
	public ResponseEntity<ErrorResponse> handleReceiptNotFound(ReceiptNotFound exception) {
		ErrorResponse response = new ErrorResponse();
		response.setDescription("No receipt found for that id");
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
}
