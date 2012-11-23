package com.example.harmonizer;

public class Note {

	public enum Name {
		A, As, B, C, Cs, D, Ds, E, F, Fs, G, Gs
	}
	
	private int octave;
	private Note.Name name;
	
	public Note(Note.Name argName, int argOctave){
		octave = argOctave;
		name = argName;
	}
	
	public int getOctave(){
		return octave;
	}
	
	public Note.Name getName(){
		return name;
	}
	
}
