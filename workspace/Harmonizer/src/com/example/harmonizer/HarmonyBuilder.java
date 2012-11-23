package com.example.harmonizer;

import java.util.List;

public class HarmonyBuilder {

	private Key key;
	private Style style;
	private History history;
	
	// will be called from the main activity
	public List<Note> buildHarmony(Note lead){
		List<Note> harmony = style.harmonize(lead, key, history);
	}
	
	public Key getKey() {
		return key;
	}

	public void setStyle(Style argStyle) {
		style = argStyle;
	}

	
	public void setKey(Key argKey){
		key = argKey;
	}
	
}
