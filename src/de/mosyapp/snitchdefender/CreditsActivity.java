package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class CreditsActivity extends Activity {
	TextView credits;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_credits);
		credits = (TextView) findViewById(R.string.credits_text);
		
		Intent notifIntent = new Intent(this, CreateNotificationActivity.class);
		startActivity(notifIntent);
	}
	
	
	


}
