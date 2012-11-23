package com.example.harmonizer.music;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.harmonizer.music.Note.Name;


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
			// next octave if we hit the next C;
			if(noteName.equals(Note.Name.C)){
				octave++;
			}
			notes.add(new Note(noteName, octave));
			// roll over scale counter if necessary
			counter = (counter + 1) % scale.getNumNotes();
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
	
	public String toString(){
		return scale.toString();
	}
	
}
