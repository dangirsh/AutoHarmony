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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
	private Button recordButton = null;
    private Button playButton = null;
	
	private HarmonyBuilder harmonyBuilder;
	
	private String debugStr;
	private float debugVal;

    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;    
    boolean mStartPlaying = true;
    boolean mStartRecording = true; 
    boolean somethingRecorded = false;
    
    private OnCompletionListener playComplete;
	
	
	private final ServiceConnection pdConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			pdService = ((PdService.PdBinder)service).getService();
			try {
				initPd();
				loadPatch();
			} catch (IOException e) {
				//Log.e(TAG, e.toString());
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
		if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
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
		initDebug();
	}
	
	private void initDebug() {
		Spinner debugKey = (Spinner) findViewById(R.id.debug_key);
		NumberPicker debugValPicker = (NumberPicker) findViewById(R.id.debug_val);
		Button debugSend = (Button) findViewById(R.id.sendDebug);
		EditText debugFloat = (EditText) findViewById(R.id.debugFloat);
		
		// debug key spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.debug, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		debugKey.setAdapter(adapter);
		debugKey.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1,
					int pos, long arg3) {
				debugStr = parent.getItemAtPosition(pos).toString();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		//debug val picker
		debugValPicker.setMaxValue(300);
		debugValPicker.setMinValue(0);
		debugValPicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				debugVal = newVal;
			}
		});
		
		//debug val edit text
		debugFloat.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            try{
	            		debugVal = Float.valueOf(s.toString());
	            }
	            catch(Exception e){
	            	
	            }
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
		}
		);
		
		//send button
		debugSend.setOnClickListener(new Button.OnClickListener() {
		    public void onClick(View v) {
		    		Log.e(debugStr, ""+debugVal);
	            PdBase.sendFloat(debugStr, debugVal);
		    }
		});
		
	}

	private void initControls(){
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recording.3gp";
    		mStartPlaying = true;
        mStartRecording = true;
    		recordButton = (Button) findViewById(R.id.record_button);
	    	playButton = (Button) findViewById(R.id.play_button);
	  
	    OnClickListener recClick = new OnClickListener() {
	        public void onClick(View v) {
	            onRecord(mStartRecording);
	            if (mStartRecording) {
	            		recordButton.setText("Stop");
	            } else {
	            		recordButton.setText("Record");
	            }
	            mStartRecording = !mStartRecording;
	        }
	    };

	    OnClickListener playClick = new OnClickListener() {
	        public void onClick(View v) {
	            onPlay(mStartPlaying);
	            if (mStartPlaying) {
	                playButton.setText("End preview");
	            } else {
	                playButton.setText("Preview");
	            }
	            mStartPlaying = !mStartPlaying;
	        }
	    };  
	    
	    playComplete = new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer arg0) {
				playButton.setText("Preview");
				mStartPlaying = true;
			}
	    };
	   
	    recordButton.setOnClickListener(recClick);
	    playButton.setOnClickListener(playClick);
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
				//Log.e("style", styleString);
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
				//Log.e("note", noteString);
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
				//Log.e("scaletype", scaleTypeString);
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
//		NumberPicker bpmChooser = (NumberPicker) findViewById(R.id.bmp_picker);
//		bpmChooser.setMaxValue(300);
//		bpmChooser.setMinValue(30);
//		bpmChooser.setOnValueChangedListener(new OnValueChangeListener() {
//			@Override
//			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//				float msecPerBeat = Helpers.bpmToMspb(newVal);
//				Log.e("msecpbeat", ""+msecPerBeat);
//				PdBase.sendFloat("msecpbeat", msecPerBeat);
//			}
//		});
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
				//Log.e("pitch", ""+x);
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
			//Log.e(TAG, e.toString());
			finish();
		} finally {
			if (patchFile != null) patchFile.delete();
		}
	}
	
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.setOnCompletionListener(playComplete);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
    		somethingRecorded = true;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}