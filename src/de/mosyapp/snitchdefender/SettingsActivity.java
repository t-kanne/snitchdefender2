package de.mosyapp.snitchdefender;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

@SuppressWarnings("deprecation")

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        
        //Einstellungen f�r die Bewegungsempfindlichkeit aufrufen und verarbeiten
        ListPreference listPref = (ListPreference) findPreference("sensitivity_key");
        String listPrefSelected = sp.getString("sensitivity_key", null);
        String listPrefEntry = (String) listPref.getEntry();
        listPref.setSummary(listPrefEntry);
        
        //Einstellungen f�r die Countdowndauer aufrufen und verarbeiten
        listPref = (ListPreference) findPreference("countdown_key");
        listPrefSelected = sp.getString("countdown_key", null);
        listPrefEntry = (String) listPref.getEntry();
        listPref.setSummary(listPrefEntry);
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
	        }
	}
	
	
}

