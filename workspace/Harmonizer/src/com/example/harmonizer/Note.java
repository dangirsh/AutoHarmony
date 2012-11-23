package com.example.harmonizer;

public class Note {

	//order matters
	public static enum Name {
		C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B
	}
	
	//maybe change to enum length
	public static final int NUM_NAMES = 12;
	
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
	
	@Override
	public String toString(){
		return this.getName().name() + " " + octave;
	}
}
