package com.example.harmonizer;

import java.util.ArrayList;
import java.util.List;

public class Scale{

	public enum Type { MAJOR, MINOR };
	
	private Note.Name baseNote;
	private Scale.Type type;
	private List<Note.Name> notes;
			
	public Scale(Note.Name note, Scale.Type argType){
		baseNote = note;
		type = argType;
		int[] intervals = Helpers.getIntervals(argType);
		notes = new ArrayList<Note.Name>();
		notes.add(note);
		for(int i : intervals){
			int nextNoteOrdinal = (note.ordinal() + i) % Note.NUM_NAMES;
			Note.Name nextNote = Note.Name.values()[nextNoteOrdinal];
			notes.add(nextNote);
			note = nextNote;
		}
	}
	
	public Note.Name getBaseNote(){
		return baseNote;
	}
	
	public List<Note.Name> getNotes(){
		return notes;
	}
	
	public Scale.Type getType(){
		return type;
	}
	
	@Override
	public String toString(){
		return this.getBaseNote().name();
	}
	
}
