package com.increff.pos.service;

// TODO: 29/01/23 move to some other folder
public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ApiException(String string) {
		super(string);
	}

}
