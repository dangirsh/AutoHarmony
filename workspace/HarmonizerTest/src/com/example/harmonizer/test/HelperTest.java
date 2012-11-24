package com.example.harmonizer.test;

import com.example.harmonizer.Helpers;
import com.example.harmonizer.music.MidiTooLowException;
import com.example.harmonizer.music.Note;

import junit.framework.TestCase;

public class HelperTest extends TestCase {

	public void testNoteToMidi() {
		try {
			assertTrue(midiIsEqual(21, Note.Name.A, 0));
			assertTrue(midiIsEqual(35, Note.Name.B, 1));
			assertTrue(midiIsEqual(69, Note.Name.A, 4));
			assertTrue(midiIsEqual(100, Note.Name.E, 7));
			assertTrue(midiIsEqual(108, Note.Name.C, 8));
			assertTrue(midiIsEqual(94, Note.Name.As, 6));
		} catch (MidiTooLowException e) {
			e.printStackTrace();
		}
	}
	
	public void testMidiToNote() {
		try {
			assertTrue(midiIsEqual(21, Note.Name.A, 0));
			assertTrue(midiIsEqual(35, Note.Name.B, 1));
			assertTrue(midiIsEqual(69, Note.Name.A, 4));
			assertTrue(midiIsEqual(100, Note.Name.E, 7));
			assertTrue(midiIsEqual(108, Note.Name.C, 8));
			assertTrue(midiIsEqual(94, Note.Name.As, 6));
		} catch (MidiTooLowException e) {
			e.printStackTrace();
		}
	
		try {
			assertTrue(midiIsEqual(-1, Note.Name.A, 0));
			fail();
		} catch (MidiTooLowException e) { //should happen
		}
	}
	
	private boolean midiIsEqual(int midiVal, Note.Name noteName, int octave) throws MidiTooLowException{
		Note note = new Note(noteName, octave);
		return Helpers.midiToNote(midiVal).equals(note);
	}
	
	private boolean noteIsEqual(int midiVal, Note.Name noteName, int octave) throws MidiTooLowException{
		Note note = new Note(noteName, octave);
		return (Helpers.noteToMidi(note) == midiVal);
	}

}
