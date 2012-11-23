package com.example.harmonizer.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.OutOfKeyException;
import com.example.harmonizer.music.OutOfRangeException;
import com.example.harmonizer.music.Scale;

public class KeyTest {

	Key key;
	
	@Before
	public void setUp() throws Exception {
		Note.Name baseNote = Note.Name.C;
		Scale.Type type = Scale.Type.MAJOR;
		Scale scale = new Scale(baseNote, type);
		key = new Key(scale);
	}

	@Test
	public void testGetNote() {
		Note C4 = new Note(Note.Name.C, 4);
		Note D4 = new Note(Note.Name.D, 4);
		Note E4 = new Note(Note.Name.E, 4);
		Note F4 = new Note(Note.Name.F, 4);
		Note G4 = new Note(Note.Name.G, 4);
		Note A5 = new Note(Note.Name.A, 5);
		Note B5 = new Note(Note.Name.B, 5);
		Note[] notes = { C4, D4, E4, F4, G4, A5, B5 };
		//test normal ranges
		for(int i = 0; i < notes.length; i++){
			try {
				assertTrue(key.getNote(C4, i).equals(notes[i]));
			} catch (OutOfRangeException e) {
				e.printStackTrace();
			} catch (OutOfKeyException e) {
				e.printStackTrace();
			}
		}
		// test out of range
		try{
			key.getNote(C4, 1000000);
			fail(); //shouldn't get here
		}catch (OutOfRangeException e){ //this should happen
		} catch (OutOfKeyException e) {
			e.printStackTrace();
		}
		// test out of key
		Scale chromatic = new Scale(Note.Name.C, Scale.Type.CHROMATIC);
		Note Ds4 = new Note(chromatic.getNthNote(3), 4);
		try {
			key.getNote(Ds4, 2);
			fail(); //shouldn't get here
		} catch (OutOfRangeException e) {
			e.printStackTrace();
		} catch (OutOfKeyException e) { //this should happen
		}
	}

	@Test
	public void testToString() {
		assertTrue(key.toString().toLowerCase().equals("c major"));
	}

}
