package com.example.harmonizer;

public class OutOfKeyException extends Exception {

	public OutOfKeyException(Key k, Note n){
		super(n.toString() + " is not in key " + k.toString() + "!");
	}
	
}
