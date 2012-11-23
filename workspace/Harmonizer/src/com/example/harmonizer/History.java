package com.example.harmonizer;

import java.util.LinkedList;
import java.util.List;

import com.example.harmonizer.music.InvalidLookBehindException;
import com.example.harmonizer.music.Note;

public class History {

	public static final int MAX_LOOKBEHIND = 6;
	
	LinkedList<List<Note>> pastHarmonies;
	
	public History(){
		 pastHarmonies = new LinkedList<List<Note>>();
	}
	
	public void addHarmony(List<Note> harmony){
		pastHarmonies.addFirst(harmony);
		if(pastHarmonies.size() > MAX_LOOKBEHIND){
			pastHarmonies.removeLast();
		}
	}
	
	// 0 will get the most recent harmony in history,
	// 1 gets the second most recent, etc...
	public List<Note> getPrevHarmony(int lookBehind) throws InvalidLookBehindException{
		if(lookBehind >= 0 && lookBehind <= MAX_LOOKBEHIND){
			return pastHarmonies.get(lookBehind);
		}
		else{
			throw new InvalidLookBehindException(lookBehind);
		}
	}
	
}
