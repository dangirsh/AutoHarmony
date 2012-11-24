package com.example.harmonizer;

import java.util.ArrayList;
import java.util.List;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.OutOfKeyException;
import com.example.harmonizer.music.OutOfRangeException;

public class MajorTriadStyle extends Style {

	@Override
	/*
	 * For a super easy way to generate chords, just stack in
	 * thirds above the provided note. No need to factor in prevLead or
	 * prevHarm(s)
	 * 
	 * function harmonizeV1(lead) -> harm:
	 * 
	 * notes = C D E F G A B (and octaves below and above, etc.)
	 * 
	 * goodNotes = every other note starting from lead (repeat a second time
	 * from first goodNote)
	 * 
	 * return goodNotes (3 in total)
	 * 
	 * so let lead = D.
	 * 
	 * start at lead, skip, good note 1 = F.
	 * 
	 * start at gn1, skip, good note 2 = A.
	 * 
	 * return D, F, A.
	 */
	public List<Note> harmonize(Note lead, Key key, History history) {
		Note voiceOne = null;
		Note voiceTwo = null;
		try {
			voiceOne = key.getNote(lead, 2);
			voiceTwo = key.getNote(lead, 4);
		} catch (OutOfRangeException e) {
			//e.printStackTrace();
		} catch (OutOfKeyException e) {
			//e.printStackTrace();
		}
		List<Note> harmony = new ArrayList<Note>();
		harmony.add(voiceOne);
		harmony.add(voiceTwo);
		//NOTE: Do not include the lead with the harmony, unless you specifically want a double note.
		return harmony;
	}

}
