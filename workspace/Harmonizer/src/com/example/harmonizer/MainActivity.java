/**
 * Sample code for "Making Musical Apps" by Peter Brinkmann
 * http://shop.oreilly.com/product/0636920022503.do
 */

package com.example.harmonizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import com.example.harmonizer.music.Key;
import com.example.harmonizer.music.MidiTooLowException;
import com.example.harmonizer.music.Note;
import com.example.harmonizer.music.Note.Name;
import com.example.harmonizer.music.Scale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.NumberPicker.OnValueChangeListener;

public class MainActivity extends Activity {

	private static final String TAG = "GuitarTuner";
	private PdUiDispatcher dispatcher;
	private PdService pdService = null;

	private TextView leadValueLabel;
	private TextView harmonyValueLabel;
	
	private HarmonyBuilder harmonyBuilder;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();
				loadPatch();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGui();
		initHarmonyBuilder();
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
	}

	private void initGui() {
		setContentView(R.layout.main);
		leadValueLabel = (TextView) findViewById(R.id.lead_pitch_value);
		harmonyValueLabel = (TextView) findViewById(R.id.harmony_value);
		initStyleChooser();
		initNoteChooser();
		initScaleChooser();
		initBpmChooser();
	}
	
	private void initStyleChooser(){
		Spinner styleChooser = (Spinner) findViewById(R.id.style_chooser);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.styles, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		styleChooser.setAdapter(adapter);
		styleChooser.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				String styleString = parent.getItemAtPosition(pos).toString();
				Log.e("style", styleString);
				Style newStyle = Helpers.strToStyle(styleString);
				harmonyBuilder.setStyle(newStyle);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	private void initNoteChooser(){
		Spinner noteChooser = (Spinner) findViewById(R.id.note_chooser);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.notes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		noteChooser.setAdapter(adapter);
		noteChooser.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				String noteString = parent.getItemAtPosition(pos).toString();
				Log.e("note", noteString);
				Note.Name newBaseNote = Note.Name.valueOf(noteString);
				Key key = harmonyBuilder.getKey();
				Scale scale = key.getScale();
				Scale newScale = new Scale(newBaseNote, scale.getType());
				Key newKey = new Key(newScale);
				harmonyBuilder.setKey(newKey);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
	}
	
	private void initScaleChooser(){
		Spinner scaleChooser = (Spinner) findViewById(R.id.scale_chooser);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.scales, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		scaleChooser.setAdapter(adapter);
		scaleChooser.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				String scaleTypeString = parent.getItemAtPosition(pos).toString();
				Log.e("scaletype", scaleTypeString);
				Scale.Type newScaleType = Scale.Type.valueOf(scaleTypeString.toUpperCase());
				Key key = harmonyBuilder.getKey();
				Scale scale = key.getScale();
				Scale newScale = new Scale(scale.getBaseNote(), newScaleType);
				Key newKey = new Key(newScale);
				harmonyBuilder.setKey(newKey);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {				
			}
		});
	}
	
	private void initBpmChooser(){
		NumberPicker bpmChooser = (NumberPicker) findViewById(R.id.bmp_picker);
		bpmChooser.setMaxValue(300);
		bpmChooser.setMinValue(30);
		bpmChooser.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				float msecPerBeat = Helpers.bpmToMspb(newVal);
				Log.e("msecpbeat", ""+msecPerBeat);
				PdBase.sendFloat("msecptick", msecPerBeat);
			}
		});
	}	
	
	private void  initPd() throws IOException {
		// Configure the audio glue
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		pdService.startAudio();
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				Log.e("pitch", ""+x);
				Note n;
				try {
					n = Helpers.midiToNote((int) x);
					leadValueLabel.setText(n.toString());
					harmonize(x);
				} catch (MidiTooLowException e) {
					//e.printStackTrace();
				}
			}
		});
	}

	public void initHarmonyBuilder(){
		Scale scale = new Scale(Note.Name.C, Scale.Type.MAJOR);
		Key key = new Key(scale);
		Style style = new MajorTriadStyle();
		harmonyBuilder = new HarmonyBuilder(key, style); 
	}
	
	public void harmonize(float midiVal){
		Note lead = null;
		try {
			lead = Helpers.midiToNote((int) midiVal);
			List<Note> harmony = harmonyBuilder.buildHarmony(lead);
			String harmonyString = "";
			for(int i = 0; i < harmony.size(); i++){
				Note n = harmony.get(i);
				if(n != null){
					float harmonyMidiVal = Helpers.noteToMidi(n);
					PdBase.sendFloat("voice"+i, harmonyMidiVal); //play the harmony
					harmonyString += n.toString() + ", ";
				}
			}
			harmonyValueLabel.setText(harmonyString);
		}
		catch (MidiTooLowException e) {
			//e.printStackTrace();
		}
		
	}
	
	private void loadPatch() throws IOException {	
		Resources res = getResources();
		File patchFile = null;
		try {
			InputStream in = res.openRawResource(R.raw.main);
			patchFile = IoUtils.extractResource(in, "main.pd", getCacheDir());
			PdBase.openPatch(patchFile);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}
}