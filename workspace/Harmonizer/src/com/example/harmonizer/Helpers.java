package com.example.harmonizer;

public class Helpers {

	public static Note MidiToNote(float midiVal){
		//TODO
		return null;
	}

	// update this if an Interval class is written. currently returns half step counts.
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
