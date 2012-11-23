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

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity{

	private static final String TAG = "Harmonizer";
	private PdUiDispatcher dispatcher;

	private PdService pdService = null;
	private TextView leadLabel;

	private HarmonyBuilder harmonyBuilder;
	private TextView harmonyLabel;

	private final ServiceConnection pdConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();
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
		bindService(new Intent(this, PdService.class), pdConnection,
				BIND_AUTO_CREATE);
	}

	private void initGui() {
		setContentView(R.layout.main);
		leadLabel = (TextView) findViewById(R.id.lead_label);
		harmonyLabel = (TextView) findViewById(R.id.harmony_label);
	}

	private void initPd() throws IOException {
		// Configure the audio glue
		int sampleRate = AudioParameters.suggestSampleRate();
		// Create and install the dispatcher
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		pdService.startAudio();
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				leadLabel.setText("Pitch: " + x);
				sendToHarmonyBuilder(x);
			}
		});

	}
	
	public void initHarmonyBuilder(){
		Scale scale = new Scale(Note.Name.C, Scale.Type.MAJOR);
		Key key = new Key(scale);
		Style style = new SimpleStyle();
		harmonyBuilder = new HarmonyBuilder(key, style); 
	}

	public void sendToHarmonyBuilder(float midiVal){
		Note lead = null;
		try {
			lead = Helpers.midiToNote((int) midiVal);
		} catch (MidiTooLowException e) {
			e.printStackTrace();
		}
		List<Note> harmony = harmonyBuilder.buildHarmony(lead);
		String harmonyString = "";
		for(Note n : harmony){
			harmonyString += n.toString();
		}
		harmonyLabel.setText("Harmony: " + harmonyString);
		
	}
	
	
	private void loadPatch() throws IOException {	
		Resources res = getResources();
		File patchFile = null;
		try {
			InputStream in = res.openRawResource(R.raw.tuner);
			patchFile = IoUtils.extractResource(in, "tuner.pd", getCacheDir());
			PdBase.openPatch(patchFile);
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
	}

}