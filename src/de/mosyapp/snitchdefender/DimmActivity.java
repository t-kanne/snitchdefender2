package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class DimmActivity extends Activity {
	private int pushToExit = 0;
	private int pusherLeft;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
					
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = 1F;
		getWindow().setAttributes(layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_dimm);
	}
	
	@Override
	public void onBackPressed() {	
		stopService(new Intent(this, ActivateCountDownTimer.class));
		
		if (pushToExit == 3) {
	        super.onBackPressed();
	        finish();
	        return;
	    }

	    this.pushToExit++;
	    
	    final Toast toast = Toast.makeText(this, "Weiter drücken", Toast.LENGTH_LONG);
	    toast.show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	        	toast.cancel();
	        }
	    }, 500);
	    
	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	        	pushToExit = 0;
	        }
	    }, 8000); 
	}
	
	
}
