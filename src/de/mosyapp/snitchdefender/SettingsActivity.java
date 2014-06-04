package de.mosyapp.snitchdefender;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.util.Log;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private static float sensorSensibility;
	private static boolean vibrationActivated;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        
        //Einstellungen für die Bewegungsempfindlichkeit aufrufen und verarbeiten
        ListPreference listPref = (ListPreference) findPreference("sensitivity_list");
        String listPrefSelected = sp.getString("sensitivity_list", null);
        String listPrefEntry = (String) listPref.getEntry();
        listPref.setSummary(listPrefEntry);
        setSensorSensibility(listPrefSelected);
        Log.i("infos", "(onCreate) limitValue: "+listPrefSelected);
        
        //Einstellungen für den Vibrationsalarm aufrufen und verarbeiten
        CheckBoxPreference checkPref = (CheckBoxPreference) findPreference("notifications_vibrate_key");
        setVibration(checkPref.isChecked());
    }
	
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
    
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
	
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
	    Log.i("infos", "onSharedPreferenceChanged aufgerufen"); 
		Preference pref = findPreference(key);
	        if (pref instanceof ListPreference) {
	            ListPreference listPref = (ListPreference) pref;
	            listPref.setSummary(listPref.getEntry());
	            setSensorSensibility(listPref.getValue());
	        }
	        if (pref instanceof CheckBoxPreference) {
	        	CheckBoxPreference checkPref = (CheckBoxPreference) pref;
	        	setVibration(checkPref.isChecked());
	        }
		
	}
	
	public void setSensorSensibility(String value) {
		sensorSensibility = Float.parseFloat(value);
		Log.i("infos", "(setSensorSensibility) limitValue: " + sensorSensibility);
		
	}
	
	public float getSensorSensibility() {
		return sensorSensibility;
	}
	
	public void setVibration(boolean vibrationActivated) {
		this.vibrationActivated = vibrationActivated;
		Log.i("infos", "Vibration: "+ vibrationActivated);
	}
	
	public boolean getVibration() {
		return vibrationActivated;
	}

}
