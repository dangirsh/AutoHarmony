package com.example.harmonizer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.puredata.android.io.AudioParameters;
import org.puredata.android.service.PdService;
import org.puredata.android.utils.PdUiDispatcher;
import org.puredata.core.PdBase;
import org.puredata.core.PdListener;
import org.puredata.core.utils.IoUtils;
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
import android.widget.Button;
import android.widget.TextView;

public class GuitarTunerActivity extends Activity{

	private static final String TAG = "GuitarTuner";
	private PdUiDispatcher dispatcher;
	private TextView pitchLabel;
	private PdService pdService = null;

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
		pitchLabel = (TextView) findViewById(R.id.lead_pitch_value);
		Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				PdBase.sendBang("grab_pitch");
			}
		});
	}

	private void  initPd() throws IOException {
		int sampleRate = AudioParameters.suggestSampleRate();
		pdService.initAudio(sampleRate, 1, 2, 10.0f);
		dispatcher = new PdUiDispatcher();
		PdBase.setReceiver(dispatcher);
		dispatcher.addListener("pitch", new PdListener.Adapter() {
			@Override
			public void receiveFloat(String source, final float x) {
				pitchLabel.setText(""+x);
				Log.e("floatrec", ""+x);
			}

			@Override
			public void receiveBang(String source) {
				Log.e("bangrec", source);
			}
			
		});
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