package com.example.harmonizer.music;

public class OutOfRangeException extends Exception {

	public OutOfRangeException(Key k, Note n, int numHalfSteps){
		super("" + numHalfSteps + " half steps above " + n.toString() + 
				"is out of the range of key " + k.toString() + ". See Key.MAX_OCTAVES.");
			
	}
	
}
