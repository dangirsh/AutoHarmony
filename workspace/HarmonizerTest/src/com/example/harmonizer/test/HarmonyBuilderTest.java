package com.example.harmonizer.test;

import java.util.Arrays;
import java.util.List;

import com.example.harmonizer.HarmonyBuilder;
import com.example.harmonizer.MajorTriadStyle;
import com.example.harmonizer.Style;
import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.Scale;

import junit.framework.TestCase;

public class HarmonyBuilderTest extends TestCase {

	// TODO add tests with notSoSimpleStyles that use History

	private Scale scale = new Scale(Note.Name.C, Scale.Type.MAJOR);
	private Key key = new Key(scale);
	private Style style = new MajorTriadStyle();
	private HarmonyBuilder harmonyBuilder = new HarmonyBuilder(key, style);

	public void testBuildHarmony() {
		List<Note> harmony = harmonyBuilder.buildHarmony(new Note(Note.Name.C, 4));
		assertTrue(harmony.equals(Arrays.asList(
					new Note(Note.Name.E, 4),
					new Note(Note.Name.G, 4)
				)
			)
		);
	}

	public void testGetKey() {
		assertTrue(harmonyBuilder.getKey().equals(key));
	}

	public void testSetKey() {
		Scale newScale = new Scale(Note.Name.Fs, Scale.Type.MINOR);
		Key newKey = new Key(newScale);
		harmonyBuilder.setKey(newKey);
		assertTrue(harmonyBuilder.getKey().equals(newKey));
		//reset to old val
		harmonyBuilder.setKey(key);
	}

	public void testGetStyle() {
		assertTrue(harmonyBuilder.getStyle().equals(style));
	}

	// TODO: update with different style class
	public void testSetStyle() {
		Style newStyle = new MajorTriadStyle();
		harmonyBuilder.setStyle(newStyle);
		assertTrue(harmonyBuilder.getStyle().equals(newStyle));
		//reset to old val
		harmonyBuilder.setStyle(style);
	}

}
