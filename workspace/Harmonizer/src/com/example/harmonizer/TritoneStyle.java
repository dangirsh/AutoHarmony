package com.example.harmonizer;

import java.util.ArrayList;
import java.util.List;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.OutOfKeyException;
import com.example.harmonizer.music.OutOfRangeException;
import com.example.harmonizer.music.Scale;

public class TritoneStyle extends Style {

	@Override
	/*
	 * This style will take the current note and build a tritone and an octave above.
	 */
	public List<Note> harmonize(Note lead, Key key, History history) {
		Scale scale = new Scale(lead.getName(), Scale.Type.CHROMATIC);
		Key chromatic = new Key(scale);
		Note voiceOne = null;
		Note voiceTwo = null;

		try {
			voiceOne = chromatic.getNote(lead, 6); //add the A4/d5 above
			voiceTwo = chromatic.getNote(lead, 12); //add the P8 above
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
