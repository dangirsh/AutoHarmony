package com.example.harmonizer.test;

import java.util.Arrays;
import java.util.List;

import com.example.harmonizer.History;
import com.example.harmonizer.MajorTriadStyle;
import com.example.harmonizer.Style;
import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.Scale;

import junit.framework.TestCase;

public class SimpleStyleTest extends TestCase {

	Scale scale = new Scale(Note.Name.C, Scale.Type.MAJOR);
	Key key = new Key(scale);
	History history = new History(); //not used by MajorTriadStyle
	Style simpleStyle = new MajorTriadStyle();
	
	public void testHarmonize() {
		// test with lead = middle c
		Note C4 = new Note(Note.Name.C, 4);
		List<Note> harmonyC4 = simpleStyle.harmonize(C4, key, history);
		assertTrue(harmonyC4.equals(Arrays.asList(
					new Note(Note.Name.E, 4),
					new Note(Note.Name.G, 4)
				)
			)
		);
		
		// test with lead = D4
		Note D4 = new Note(Note.Name.D, 4);
		List<Note> harmonyD4 = simpleStyle.harmonize(D4, key, history);
		assertTrue(harmonyD4.equals(Arrays.asList(
					new Note(Note.Name.F, 4),
					new Note(Note.Name.A, 4)
				)
			)
		);
		
		// test hitting next octave with lead = G4
				Note G4 = new Note(Note.Name.G, 4);
				List<Note> harmonyG4 = simpleStyle.harmonize(G4, key, history);
				assertTrue(harmonyG4.equals(Arrays.asList(
							new Note(Note.Name.B, 4),
							new Note(Note.Name.D, 5)
						)
					)
				);
	}

}
