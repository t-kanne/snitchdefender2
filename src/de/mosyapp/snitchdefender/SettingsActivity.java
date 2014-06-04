package de.mosyapp.snitchdefender;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	static float sensorSensibility;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        ListPreference listPref = (ListPreference) findPreference("sensitivity_list");
        String listPrefSelected = sp.getString("sensitivity_list", null);
        String listPrefEntry = (String) listPref.getEntry();
        listPref.setSummary(listPrefEntry);
        setSensorSensibility(listPrefSelected);
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
		
	}
	
	public void setSensorSensibility(String value){
		sensorSensibility = Float.parseFloat(value);
		
	}
	
	public float getSensorSensibility(){
		return sensorSensibility;
	}

}
