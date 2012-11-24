package com.example.harmonizer;

import com.example.harmonizer.music.MidiTooLowException;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.Scale;

public class Helpers {

	// Use fact that Midi 21 = A0
	public static Note midiToNote(int midiVal) throws MidiTooLowException {
		int octave = (midiVal / 12) - 1;
		if(octave < 0){
			throw new MidiTooLowException(midiVal);
		}
		int ordinal = midiVal % 12;
		Note.Name noteName = Note.Name.values()[ordinal];
		return new Note(noteName, octave);
	}

	public static int[] getIntervals(Scale.Type type) {
		switch (type) {
		case MAJOR:
			int[] majorIntervals = { 2, 2, 1, 2, 2, 2 };
			return majorIntervals;
		case MINOR:
			int[] minorIntervals = { 2, 1, 2, 2, 1, 2 };
			return minorIntervals;
		case CHROMATIC:
			int[] chromaticIntervals = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
			return chromaticIntervals;
		default:
			return null;
		}
	}

	// beats per minute -> milliseconds per beat
	public static float bpmToMspb(int bpm) {
		double bps = bpm / 60.0;
		return (float) (1000.0 / bps);
	}

	public static Style strToStyle(String styleString) {
		if(styleString.equals("Major Triad")){
			return new MajorTriadStyle();
		}
		else if(styleString.equals("Medieval")){
			return new MedievalStyle();
		}
		else if(styleString.equals("Tritone")){
			return new TritoneStyle();
		}
		else if(styleString.equals("Barbershop")){
			return new BarbershopStyle();
		}
		else{
			return null; //TODO: change to throw exception
		}
			
	}

	public static float noteToMidi(Note n) throws MidiTooLowException {
		int octave = n.getOctave();
		int ordinal = n.getName().ordinal();
		int midiVal = ((octave + 1) * 12) + ordinal;
		if(midiVal > 0){
			return (float) midiVal;
		}
		else{
			throw new MidiTooLowException(midiVal);
		}
	}

}
