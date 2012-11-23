package com.example.harmonizer.music;

public class MidiTooLowException extends Exception {

	public MidiTooLowException(int midiVal) {
		super("Midi value " + midiVal + " is too low!");
	}	
	
}
