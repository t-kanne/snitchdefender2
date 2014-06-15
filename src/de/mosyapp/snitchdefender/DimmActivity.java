package de.mosyapp.snitchdefender;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Build;
import android.preference.PreferenceManager;

public class DimmActivity extends Activity {
	private boolean isLockScreenDisabled;
	private boolean doubleBackToExitPressedOnce;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Einstellungen aufrufen aus SettingsActivity
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				
		// Aufrufen der Sensibilität aus den Einstellungen
		isLockScreenDisabled = preferences.getBoolean("pref_lockscreen_mode_key", false);
		Log.i("dimm", "lockScreenDisabled: " + isLockScreenDisabled);
		
		if (isLockScreenDisabled) {	
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			
			winParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
			win.setAttributes(winParams);
			
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        
			setContentView(R.layout.activity_dimm);
		}
		else {
			finish();
		}

	}
	
	@Override
	public void onBackPressed() {	
		stopService(new Intent(this, ActivateCountDownTimer.class));
		
		if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Nochmal drücken, um zurück zur App zu gelangen", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	}
	

}
