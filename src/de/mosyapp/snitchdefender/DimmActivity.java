package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class DimmActivity extends Activity {
	private int pushToExit = 0;
	private int pusherLeft;
	private Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_dimm);
		
		button = (Button) findViewById(R.id.dimm_activity_button);
		button.setVisibility(View.GONE);
		
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = 1F;
		getWindow().setAttributes(layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
			
		});
	}
	
	@Override
	public void onBackPressed() {	
		stopService(new Intent(this, ActivateCountDownTimer.class));
		button.setVisibility(View.VISIBLE);
		

		
		if (pushToExit == 3) {
	        super.onBackPressed();
	        finish();
	        return;
	    }

	    this.pushToExit++;
	    pusherLeft = 4 - pushToExit;
	    
	    final Toast toast = Toast.makeText(this, String.valueOf(pusherLeft), Toast.LENGTH_LONG);
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
	        	button.setVisibility(View.GONE);
	        }
	    }, 2000); 
	}
	
	
}
