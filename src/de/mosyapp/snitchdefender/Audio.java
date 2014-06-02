package de.mosyapp.snitchdefender;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.util.Log;

public class Audio  {
	Context context;
	SoundPool sp;
	int soundId;
	int soundIdTemp;
	boolean loaded;
	boolean activated = true;
	
	public Audio(Context context){
		//super(1, 4, 0); //1 = Anzahl gleichzeitiger Streams, 4 = Type: Alarm, 0 = Qualität: Standard
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
   
    
}