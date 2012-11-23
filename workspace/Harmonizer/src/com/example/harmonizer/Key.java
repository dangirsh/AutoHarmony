package com.example.harmonizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Key {

	private List<Note> notes = new ArrayList<Note>();
	private static final int MAX_OCTAVES = 8;
	
	private Scale scale;
	
	public Key(Scale argScale){
		scale = argScale;
		List<Note.Name> noteNames = scale.getNotes();
		for(int octave = 1; octave <= MAX_OCTAVES; octave++){
			List<Note> octaveNotes = new ArrayList<Note>();
			for(Note.Name name : noteNames){
				octaveNotes.add(new Note(name, octave));
			}
			notes.addAll(octaveNotes);
		}
	}
	
	//Maybe make an Interval class and use it here
	public Note getNote(Note start, int numHalfSteps) throws OutOfRangeException, OutOfKeyException{
		int index = notes.indexOf(start);
		if(index > 0){
			int otherIndex = index + numHalfSteps;
			if(otherIndex < notes.size()){
				return notes.get(otherIndex);
			}
			else{
				throw new OutOfRangeException(this, start, numHalfSteps);
			}
		}
		else{
			throw new OutOfKeyException(this, start);
		}
	}
	
	public String toString(){
		return scale.toString();
	}
	
}
