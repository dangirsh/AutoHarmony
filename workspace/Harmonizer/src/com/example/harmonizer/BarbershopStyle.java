package com.example.harmonizer;

import java.util.ArrayList;
import java.util.List;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.OutOfKeyException;
import com.example.harmonizer.music.OutOfRangeException;
import com.example.harmonizer.music.Scale;

public class BarbershopStyle extends Style {

	@Override
	/*
	 * This style will take the current note and build a dominant seventh on top.
	 */
	public List<Note> harmonize(Note lead, Key key, History history) {
		Scale scale = new Scale(lead.getName(), Scale.Type.CHROMATIC);
		Key chromatic = new Key(scale);
		Note voiceOne = null;
		Note voiceTwo = null;
		Note voiceThree = null;

		try {
			voiceOne = chromatic.getNote(lead, 4); //add the M3 above
			voiceTwo = chromatic.getNote(lead, 7); //add the P5 above
			voiceThree = chromatic.getNote(lead, 10); //add the m7 above
		} catch (OutOfRangeException e) {
			//e.printStackTrace();
		} catch (OutOfKeyException e) {
			//e.printStackTrace();
		}
		List<Note> harmony = new ArrayList<Note>();
		harmony.add(voiceOne);
		harmony.add(voiceTwo);
		harmony.add(voiceThree);
		return harmony;
	}

}
