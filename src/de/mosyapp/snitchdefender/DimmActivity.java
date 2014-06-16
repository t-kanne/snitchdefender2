package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class DimmActivity extends Activity {
	private boolean doubleBackToExitPressedOnce;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
					
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
			
		winParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
		win.setAttributes(winParams);
			
	       requestWindowFeature(Window.FEATURE_NO_TITLE);
	       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	    		   WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_dimm);
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
