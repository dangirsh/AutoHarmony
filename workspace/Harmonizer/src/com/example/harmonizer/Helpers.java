package com.example.harmonizer;

import com.example.harmonizer.music.MidiTooLowException;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.Scale;

public class Helpers {

	// Use fact that Midi 21 = A0
	public static Note MidiToNote(int midiVal) throws MidiTooLowException {
		int octave = 0;
		Note.Name name;
		if (midiVal >= 24) {
			int offset = (midiVal - 24);
			octave = 1 + (offset / 12);
			int ordinal = offset % 12;
			name = Note.Name.values()[ordinal];
		} else if (midiVal == 21) {
			name = Note.Name.A;
		} else if (midiVal == 22) {
			name = Note.Name.As;
		} else if (midiVal == 23) {
			name = Note.Name.B;
		} else{
			throw new MidiTooLowException(midiVal);
		}
		return new Note(name, octave);
	}

	// update this if an Interval class is written. currently returns half step
	// counts.
	public static int[] getIntervals(Scale.Type type) {
		switch (type) {
		case MAJOR:
			int[] majorIntervals = { 2, 2, 1, 2, 2, 2, 1 };
			return majorIntervals;
		case MINOR:
			int[] minorIntervals = { 2, 1, 2, 2, 1, 2, 2 };
			return minorIntervals;
		default:
			return null;
		}
	}

}
