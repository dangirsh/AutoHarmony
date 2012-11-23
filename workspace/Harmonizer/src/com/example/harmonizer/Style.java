package com.example.harmonizer;

import java.util.List;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;


public abstract class Style {

	public abstract List<Note> harmonize(Note lead, Key key, History history);
	
}
