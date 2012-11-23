package com.example.harmonizer;

public class MidiTooLowException extends Exception {

	public MidiTooLowException(int midiVal) {
		super("Midi value " + midiVal + " is too low!");
	}	
	
}
