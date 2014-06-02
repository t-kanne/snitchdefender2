package de.mosyapp.snitchdefender;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.util.Log;

public class Audio {
	Context context;
	SoundPool sp;
	int soundId;
	boolean loaded;
	boolean activated = true;
	
	public Audio(Context context){
		this.context = context;  
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	public void loadSound(){
	    sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
	    
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
	    	sp.play(soundId, 1, 1, 1, 0, 1f);
	    	sp.setLoop(soundId, 9);
		    Log.i("infos", "Sound sollte gespielt werden");
	    }
    }
    
    public void stopSound() {
    	sp.stop(soundId);
    	activated = true;
    	Log.i("infos", "Stopp");
    }
   
    
}