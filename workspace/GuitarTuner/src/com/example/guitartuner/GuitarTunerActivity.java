package com.example.guitartuner;

import java.io.File;
import java.io.IOException;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GuitarTunerActivity extends Activity implements OnClickListener {

	private static final String TAG = "GuitarTuner";
	private PdUiDispatcher dispatcher;
	Button aButton, bButton, dButton, eButton, gButton, eeButton;

	private PdService pdService = null;

	private final ServiceConnection pdConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder) service).getService();
			try {
				initPd();
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
		eButton = (Button) findViewById(R.id.e_button);
		eButton.setOnClickListener(this);
		aButton = (Button) findViewById(R.id.a_button);
		aButton.setOnClickListener(this);
		dButton = (Button) findViewById(R.id.d_button);
		dButton.setOnClickListener(this);
		gButton = (Button) findViewById(R.id.g_button);
		gButton.setOnClickListener(this);
		bButton = (Button) findViewById(R.id.b_button);
		bButton.setOnClickListener(this);
		eeButton = (Button) findViewById(R.id.ee_button);
		eeButton.setOnClickListener(this);
	}

	private void initPd() throws IOException {
		// Configure the audio glue
		int sampleRate = AudioParameters.suggestSampleRate();
		// Create and install the dispatcher
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, float x) {
				Log.i(TAG, "pitch: " + x);
			}
		});

	}

	private void loadPatch() throws IOException {
		File dir = getFilesDir();
		IoUtils.extractZipResource(getResources().openRawResource(R.raw.tuner),
				dir, true);
		File patchFile = new File(dir, "tuner.pd");
		PdBase.openPatch(patchFile.getAbsolutePath());
	}

	private void triggerNote(int n) {
		PdBase.sendFloat("midinote", n);
		PdBase.sendBang("trigger");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.e_button:
			triggerNote(40); // E is MIDI note 40.
			break;
		case R.id.a_button:
			triggerNote(45); // A is MIDI note 45.
			break;
		case R.id.d_button:
			triggerNote(50);
			break;
		case R.id.g_button:
			triggerNote(55);
			break;
		case R.id.b_button:
			triggerNote(59);
			break;
		case R.id.ee_button:
			triggerNote(64);
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(pdConnection);
	}

}
