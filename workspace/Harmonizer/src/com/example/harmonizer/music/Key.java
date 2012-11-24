package com.example.harmonizer.music;

import java.util.ArrayList;
import java.util.List;


public class Key {

	private List<Note> notes = new ArrayList<Note>();
	private static final int MAX_OCTAVES = 8;
	
	private Scale scale;

	public Key(Scale argScale){
		scale = argScale;
		int octave = 0;
		int counter = 0;
		while(octave < MAX_OCTAVES){
			Note.Name noteName = scale.getNthNote(counter);
			notes.add(new Note(noteName, octave));
			// roll over scale counter if necessary
			counter = (counter + 1) % scale.getNumNotes();
			// next octave if we passed the next C;
			Note.Name nextNoteName = scale.getNthNote(counter);
			if( (Note.compareNames(nextNoteName, Note.Name.C) >= 0) &&
					(Note.compareNames(noteName, Note.Name.C) < 0)){
				octave++;
			}
		}
	}
	
	public Note getNote(Note start, int numSteps) throws OutOfRangeException, OutOfKeyException{
		int index = notes.indexOf(start);
		if(index > 0){
			int otherIndex = index + numSteps;
			if(otherIndex < notes.size()){
				return notes.get(otherIndex);
			}
			else{
				throw new OutOfRangeException(this, start, numSteps);
			}
		}
		else{
			throw new OutOfKeyException(this, start);
		}
	}

	public Scale getScale() {
		return scale;
	}
	
	@Override
	public String toString(){
		return scale.toString();
	}

	@Override 
	public boolean equals(Object o){
		if(o instanceof Key){
			Key otherKey = (Key) o;
			return otherKey.scale.equals(this.scale);
		}
		return false;
	}
	
}
