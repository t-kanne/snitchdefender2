package de.mosyapp.snitchdefender;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.hardware.Camera.AutoFocusCallback;

public class Alarm  {
	private Context context;
	private SoundPool sp;
	private int soundId;
	private int soundIdTemp;
	private boolean loaded;
	private boolean activated = true;
	private boolean checkVibrationOn = false;
	private Vibrator vibrator;
	private Camera cam;
	private boolean isFlashOn = false;
	
	
	public Alarm (Context context){
		this.context = context;  	
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	public void loadSound(){
	    sp = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
	    
	    sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
	
			@Override
			public void onLoadComplete(SoundPool sp, int sampleId, int status) {
				loaded = true;
			}
	    	
	    });
	    soundId = sp.load(context, R.raw.sound1, 1);
	    Log.i("infos", "Sound geladen");
	}
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	public void startSound(){
		/*if (loaded = false){
			loadSound();
			startSound();
			Log.i("infos", "loaded = false");
		}
		*/
		if (loaded && activated) {
			activated = false;
			
			if (android.os.Build.VERSION.SDK_INT>=11){
				soundIdTemp = sp.play(soundId, 1, 1, 1, -1, 1f);
			}
			else{
				soundIdTemp = sp.play(soundId, 1, 1, 1, 0, 1f);
				sp.setLoop(soundIdTemp, -1);
			}
		    Log.i("infos", "Sound wird abgespielt. Id: "+soundIdTemp);
	    }
		if (!loaded){
			Log.i("infos","nicht loaded");
		}
    }
    
    public void stopSound() {
    	sp.stop(soundIdTemp);
    	activated = true;
    	Log.i("infos", "Stopp");
    }
    
    public void startVibration(boolean vibrationActivated) {
    	if (vibrationActivated == true){
    		vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
    		long[] vibArray = {1,1};
    		vibrator.vibrate(vibArray, 0);
    		checkVibrationOn = true;
    	}
    }
    
    public void stopVibration(boolean vibrationActivated){
    	if (vibrationActivated == true && checkVibrationOn == true){
    			vibrator.cancel();
    			checkVibrationOn = false;	
    	}
    }
    
    public void startVibrationOnActivate(){
    	vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
    	vibrator.vibrate(60);
    }
    
    
    
    
    
    public void startFlashLight(){
    	try{
    		if(isFlashOn == false){
    			cam = Camera.open();     
    			Parameters params = cam.getParameters();
    			params.setFlashMode(Parameters.FLASH_MODE_ON);
    			cam.setParameters(params);
    			cam.startPreview();
    			cam.autoFocus(new AutoFocusCallback() {
    				public void onAutoFocus(boolean success, Camera camera) {
    				}
    			});
    			isFlashOn = true;
    		}
    		 Log.i("infos","led an hat geklappt");
    	}
    	catch(Exception e){
    		Log.i("infos","led an hat nicht geklappt");
    	}
    	
    }
    
    public void stopFlashLight(){
    	if (isFlashOn == true) {
    	cam.stopPreview();
    	cam.release();
    	isFlashOn = false;
        Log.i("infos","led aus");
    	}
    }
   
    
}