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
import com.example.harmonizer.music.Scale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.NumberPicker.OnValueChangeListener;

public class MainActivity extends Activity {

	private static final String TAG = "GuitarTuner";
	private PdUiDispatcher dispatcher;
	
	private TextView leadValueLabel;
	private TextView harmonyValueLabel;
	
	private HarmonyBuilder harmonyBuilder;


	private PdService pdService = null;

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();
				initHarmonyBuilder();
				loadPatch();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				finish();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGui();
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
		initKeyChooser();
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
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}

	private void initKeyChooser(){
		Spinner keyChooser = (Spinner) findViewById(R.id.key_chooser);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.keys, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		keyChooser.setAdapter(adapter);
		keyChooser.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
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
				PdBase.sendFloat("bpm", msecPerBeat);
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
				leadValueLabel.setText(""+x);
				sendToHarmonyBuilder(x);
			}
		});
	}

	public void initHarmonyBuilder(){
		Scale scale = new Scale(Note.Name.C, Scale.Type.MAJOR);
		Key key = new Key(scale);
		Style style = new MajorTriadStyle();
		harmonyBuilder = new HarmonyBuilder(key, style); 
	}
	
	public void sendToHarmonyBuilder(float midiVal){
		Note lead = null;
		try {
			lead = Helpers.midiToNote((int) midiVal);
		} catch (MidiTooLowException e) {
			//e.printStackTrace();
		}
		if(lead != null){
			List<Note> harmony = harmonyBuilder.buildHarmony(lead);
			String harmonyString = "";
			for(Note n : harmony){
				if(n != null){
					harmonyString += n.toString() + ", ";
				}
			}
			harmonyValueLabel.setText(harmonyString);
		}
		else{
			Log.e("NULLLEAD", (new Float(midiVal)).toString());
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