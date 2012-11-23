package com.example.harmonizer.test;

import com.example.harmonizer.Helpers;
import com.example.harmonizer.music.MidiTooLowException;
import com.example.harmonizer.music.Note;

import junit.framework.TestCase;

public class HelperTest extends TestCase {

	public void testMidiToNote() {
		try {
			assertTrue(isEqual(21, Note.Name.A, 0));
			assertTrue(isEqual(35, Note.Name.B, 1));
			assertTrue(isEqual(69, Note.Name.A, 4));
			assertTrue(isEqual(100, Note.Name.E, 7));
			assertTrue(isEqual(108, Note.Name.C, 8));
			assertTrue(isEqual(94, Note.Name.As, 6));
		} catch (MidiTooLowException e) {
			e.printStackTrace();
		}
		
		//test too low exception cases
		try {
			assertTrue(isEqual(20, Note.Name.A, 0));
			fail();
		} catch (MidiTooLowException e) { //should happen
		}
		
		try {
			assertTrue(isEqual(-1, Note.Name.A, 0));
			fail();
		} catch (MidiTooLowException e) { //should happen
		}
	}
	
	private boolean isEqual(int midiVal, Note.Name noteName, int octave) throws MidiTooLowException{
		Note note = new Note(noteName, octave);
		return Helpers.midiToNote(midiVal).equals(note);
	}

}
