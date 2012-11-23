package com.example.harmonizer;

import java.util.List;

public abstract class Style {

	public abstract List<Note> harmonize(Note lead, Key key, History history);
	
}
