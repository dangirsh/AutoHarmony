package com.example.harmonizer;

import java.util.List;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;

public class HarmonyBuilder {

	private Key key;
	private Style style;
	private History history;
	
	public HarmonyBuilder(Key argKey, Style argStyle){
		key = argKey;
		style = argStyle;
		history = new History();
	}
	
	// will be called from the main activity at some fixed interval
	public List<Note> buildHarmony(Note lead){
		List<Note> harmony = style.harmonize(lead, key, history);
		history.addHarmony(harmony);
		return harmony;
	}
	
	public Key getKey() {
		return key;
	}

	// will be tied to user input (dropdown)
	public void setStyle(Style argStyle) {
		style = argStyle;
	}
	
	// will be tied to user input (dropdown)
	public void setKey(Key argKey){
		key = argKey;
	}
	
}
