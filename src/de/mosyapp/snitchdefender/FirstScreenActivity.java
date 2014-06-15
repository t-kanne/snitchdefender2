package de.mosyapp.snitchdefender;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@SuppressLint("NewApi")
public class FirstScreenActivity extends Activity {
	
	Button nextButton;

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_first_screen);
		
		nextButton = (Button) findViewById(R.id.nextbutton);
		buttonListener();
	}
	
	
	public void buttonListener() { 
		nextButton.setOnClickListener(new OnClickListener() {
 
			public void onClick(View v) {					
				Intent next = new Intent(FirstScreenActivity.this, MainActivity.class);
				startActivity(next);
				finish();	
			}
		});
	}
	

}
