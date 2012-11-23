package com.example.harmonizer.music;

public class Note {

	// order matters
	public static enum Name {
		C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B
	}

	// maybe change to enum length
	public static final int NUM_NAMES = 12;

	private int octave;
	private Note.Name name;

	public Note(Note.Name argName, int argOctave) {
		octave = argOctave;
		name = argName;
	}
	
	public static int compareNames(Note.Name name1, Note.Name name2){
		return name1.name().compareTo(name2.name());
	}

	public int getOctave() {
		return octave;
	}

	public Note.Name getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.getName().name() + " " + octave;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Note)) {
			return false;
		} else {
			Note otherNote = (Note) o;
			return (this.getName().equals(otherNote.getName()) && this
					.getOctave() == otherNote.getOctave());
		}

	}
}
