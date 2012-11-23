//package com.example.guitartuner;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.puredata.android.io.AudioParameters;
//import org.puredata.android.service.PdService;
//import org.puredata.android.utils.PdUiDispatcher;
//import org.puredata.core.PdBase;
//import org.puredata.core.PdListener;
//import org.puredata.core.utils.IoUtils;
//
//import android.os.Bundle;
//import android.os.IBinder;
//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.util.Log;
//import android.view.Menu;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class GuitarTunerActivity extends Activity implements OnClickListener {
//
//	private static final String TAG = "GuitarTuner";
//	private PdUiDispatcher dispatcher;
//	Button aButton, bButton, dButton, eButton, gButton, eeButton;
//
//	private PdService pdService = null;
//	private TextView pitchLabel;
//
//	private final ServiceConnection pdConnection = new ServiceConnection() {
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			pdService = ((PdService.PdBinder) service).getService();
//			try {
//				initPd();
//				//loadPatch();
//			} catch (IOException e) {
//				Log.e(TAG, e.toString());
//				finish();
//			}
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			// this method will never be called
//		}
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		initGui();
//		bindService(new Intent(this, PdService.class), pdConnection,
//				BIND_AUTO_CREATE);
//	}
//
//	private void initGui() {
//		setContentView(R.layout.main);
//		eButton = (Button) findViewById(R.id.e_button);
//		eButton.setOnClickListener(this);
//		aButton = (Button) findViewById(R.id.a_button);
//		aButton.setOnClickListener(this);
//		dButton = (Button) findViewById(R.id.d_button);
//		dButton.setOnClickListener(this);
//		gButton = (Button) findViewById(R.id.g_button);
//		gButton.setOnClickListener(this);
//		bButton = (Button) findViewById(R.id.b_button);
//		bButton.setOnClickListener(this);
//		eeButton = (Button) findViewById(R.id.ee_button);
//		eeButton.setOnClickListener(this);
//		pitchLabel = (TextView) findViewById(R.id.pitch_label);
//	}
//
//	private void initPd() throws IOException {
//		// Configure the audio glue
//		int sampleRate = AudioParameters.suggestSampleRate();
//		// Create and install the dispatcher
//		pdService.initAudio(sampleRate, 1, 2, 10.0f);
//		dispatcher = new PdUiDispatcher();
//		PdBase.setReceiver(dispatcher);
//		dispatcher.addListener("pitch", new PdListener.Adapter() {
//			@Override
//			public void receiveFloat(String source, final float x) {
//				pitchLabel.setText("Pitch: " + x);
//			}
//		});
//
//	}
//
//	private void loadPatch() throws IOException {
//		File dir = getFilesDir();
//		IoUtils.extractZipResource(getResources().openRawResource(R.raw.tuner),
//				dir, true);
//		File patchFile = new File(dir, "tuner.pd");
//		PdBase.openPatch(patchFile.getAbsolutePath());
//	}
//
//	private void triggerNote(int n) {
//		PdBase.sendFloat("midinote", n);
//		PdBase.sendBang("trigger");
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.e_button:
//			triggerNote(40); // E is MIDI note 40.
//			break;
//		case R.id.a_button:
//			triggerNote(45); // A is MIDI note 45.
//			break;
//		case R.id.d_button:
//			triggerNote(50);
//			break;
//		case R.id.g_button:
//			triggerNote(55);
//			break;
//		case R.id.b_button:
//			triggerNote(59);
//			break;
//		case R.id.ee_button:
//			triggerNote(64);
//			break;
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		unbindService(pdConnection);
//	}
//
//}

/**
 * 
 * @author Peter Brinkmann (peter.brinkmann@gmail.com)
 * 
 * For information on usage and redistribution, and for a DISCLAIMER OF ALL
 * WARRANTIES, see the file, "LICENSE.txt," in this distribution.
 *
 * simple test case for {@link PdService}
 * 
 */

package com.example.guitartuner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.puredata.android.service.PdPreferences;
import org.puredata.android.service.PdService;
import org.puredata.core.PdBase;
import org.puredata.core.PdReceiver;
import org.puredata.core.utils.IoUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


public class GuitarTunerActivity extends Activity implements OnClickListener {

	private static final String TAG = "Pd Test";

	private CheckBox left, right, mic;
	private Button prefs;
	private TextView logs;

	private PdService pdService = null;

	private PdReceiver receiver = new PdReceiver() {

		private void pdPost(String msg) {
			//
		}

		@Override
		public void print(String s) {
			Log.e("print", s);
		}

		@Override
		public void receiveBang(String source) {
			pdPost("bang");
		}

		@Override
		public void receiveFloat(String source, float x) {
			pdPost("float: " + x);
		}

		@Override
		public void receiveList(String source, Object... args) {
			pdPost("list: " + Arrays.toString(args));
		}

		@Override
		public void receiveMessage(String source, String symbol, Object... args) {
			pdPost("message: " + Arrays.toString(args));
		}

		@Override
		public void receiveSymbol(String source, String symbol) {
			pdPost("symbol: " + symbol);
		}
	};

	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			initPd();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// this method will never be called
		}
	};

	@Override
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initGui();
		bindService(new Intent(this, PdService.class), pdConnection, BIND_AUTO_CREATE);
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cleanup();
	}

	private void initGui() {
		setContentView(R.layout.main);
		mic = (CheckBox) findViewById(R.id.mic_box);
		mic.setOnClickListener(this);
	}

	private void initPd() {
		Resources res = getResources();
		File patchFile = null;
		try {
			PdBase.setReceiver(receiver);
			PdBase.subscribe("android");
			InputStream in = res.openRawResource(R.raw.tuner);
			patchFile = IoUtils.extractResource(in, "tuner.pd", getCacheDir());
			PdBase.openPatch(patchFile);
			startAudio();
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}

	private void startAudio() {
		String name = getResources().getString(R.string.app_name);
		try {
			pdService.initAudio(-1, -1, -1, -1);   // negative values will be replaced with defaults/preferences
			pdService.startAudio(new Intent(this, GuitarTunerActivity.class), R.drawable.icon, name, "Return to " + name + ".");
		} catch (IOException e) {
			Log.e("Adsf", e.toString());
		}
	}

	private void cleanup() {
		try {
			unbindService(pdConnection);
		} catch (IllegalArgumentException e) {
			// already unbound
			pdService = null;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mic_box:
			PdBase.sendFloat("mic", mic.isChecked() ? 1 : 0);
			break;
		default:
			break;
		}
	}

}
