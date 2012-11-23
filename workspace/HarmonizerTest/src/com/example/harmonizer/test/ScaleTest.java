package com.example.harmonizer.test;

import java.util.List;
import java.util.Arrays;


import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.Scale;

import junit.framework.TestCase;

public class ScaleTest extends TestCase {

	private Scale testMajorScale = new Scale(Note.Name.E, Scale.Type.MAJOR);
	private Scale testMinorScale = new Scale(Note.Name.E, Scale.Type.MINOR);
	private Scale testChromaticScale = new Scale(Note.Name.E, Scale.Type.CHROMATIC);;
	
	private List<Note.Name> trueMajorNotes = Arrays.asList(
			Note.Name.E,
			Note.Name.Fs,
			Note.Name.Gs,
			Note.Name.A,
			Note.Name.B,
			Note.Name.Cs,
			Note.Name.Ds
	);
	
	private List<Note.Name> trueMinorNotes = Arrays.asList(
			Note.Name.E,
			Note.Name.Fs,
			Note.Name.G,
			Note.Name.A,
			Note.Name.B,
			Note.Name.C,
			Note.Name.D
	);
	
	private List<Note.Name> trueChromaticNotes = Arrays.asList(
			Note.Name.E,
			Note.Name.F,
			Note.Name.Fs,
			Note.Name.G,
			Note.Name.Gs,
			Note.Name.A,
			Note.Name.As,
			Note.Name.B,
			Note.Name.C,
			Note.Name.Cs,
			Note.Name.D,
			Note.Name.Ds
	);
	
	// keep these in the same order as trueNoteLists!
	private List<Scale> testScales = Arrays.asList(
			testMajorScale,
			testMinorScale,
			testChromaticScale
	);
	
	@SuppressWarnings("unchecked")
	private List<List<Note.Name>> trueNotesList = Arrays.asList(
			trueMajorNotes,
			trueMinorNotes,
			trueChromaticNotes
	);
		
	public void testGetBaseNote() {
		for(Scale testScale : testScales) {
			assertTrue(testScale.getBaseNote().equals(Note.Name.E));
		}
	}

	public void testGetNotes() {
		for(int i = 0; i < testScales.size(); i++) {
			Scale testScale = testScales.get(i);
			List<Note.Name> trueNotes = trueNotesList.get(i);
			assertTrue((testScale.getNotes()).equals(trueNotes));
		}
	}

	public void testGetNumNotes() {
		for(int i = 0; i < testScales.size(); i++) {
			Scale testScale = testScales.get(i);
			List<Note.Name> trueNotes = trueNotesList.get(i);
			assertTrue(testScale.getNumNotes() == trueNotes.size());
		}
	}

	public void testGetNthNote() {
		for(int i = 0; i < testScales.size(); i++) {
			Scale testScale = testScales.get(i);
			List<Note.Name> trueNotes = trueNotesList.get(i);
			for(int n = 0; n < testScale.getNumNotes(); n++){
				assertTrue(testScale.getNthNote(n) == trueNotes.get(n));
			}
		}
	}

	public void testGetType() {
		assertTrue(testMajorScale.getType().equals(Scale.Type.MAJOR));
		assertTrue(testMinorScale.getType().equals(Scale.Type.MINOR));
		assertTrue(testChromaticScale.getType().equals(Scale.Type.CHROMATIC));
	}

	public void testToString() {
		assertTrue(testMajorScale.toString().toLowerCase().equals("e major"));
		assertTrue(testMinorScale.toString().toLowerCase().equals("e minor"));
		assertTrue(testChromaticScale.toString().toLowerCase().equals("e chromatic"));
	}

}
