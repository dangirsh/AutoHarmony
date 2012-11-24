package com.example.harmonizer;

import java.util.ArrayList;
import java.util.List;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.OutOfKeyException;
import com.example.harmonizer.music.OutOfRangeException;

public class MedievalStyle extends Style {

	@Override
	/*
	 * As a basic level, Medieval harmony is based on open fifths.  This style will double the current pitch at the fifth and octave.
	 */
	public List<Note> harmonize(Note lead, Key key, History history) {
		Note voiceOne = null;
		Note voiceTwo = null;
		try {
			voiceOne = key.getNote(lead, 4);
			voiceTwo = key.getNote(lead, 8);
		} catch (OutOfRangeException e) {
			//e.printStackTrace();
		} catch (OutOfKeyException e) {
			//e.printStackTrace();
		}
		List<Note> harmony = new ArrayList<Note>();
		harmony.add(voiceOne);
		harmony.add(voiceTwo);
		return harmony;
	}

}