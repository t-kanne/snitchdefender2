package de.mosyapp.snitchdefender;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;
import android.preference.PreferenceManager;

public class DimmActivity extends ActionBarActivity {
	private boolean isLockScreenDisabled;
	
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
			 
			setContentView(R.layout.activity_dimm);
		}
		else {
			finish();
		}

	}

}
