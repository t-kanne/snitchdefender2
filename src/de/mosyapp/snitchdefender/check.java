package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class check extends Activity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings=getSharedPreferences("prefs",0);
		boolean firstRun = settings.getBoolean("firstRun", false);
    
		if(firstRun == false){
			SharedPreferences.Editor editor=settings.edit();
			editor.putBoolean("firstRun",true);
			editor.commit();
			Intent firstScreen = new Intent(check.this,FirstScreenActivity.class);
			startActivity(firstScreen);
			finish();
		}
		else
		{
			Intent mainScreen = new Intent(check.this, MainActivity.class);
			startActivity(mainScreen);
			finish();
		}
	}
}