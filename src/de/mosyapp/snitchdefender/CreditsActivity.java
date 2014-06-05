package de.mosyapp.snitchdefender;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class CreditsActivity extends Activity {
	private ServiceConnection mConnection;
	protected CreateNotificationService mBoundService;
	private boolean mIsBound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_credits);
		
		
		mConnection = new ServiceConnection() {
			
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				mBoundService = ((CreateNotificationService.LocalBinder)service).getService();
				Log.i("CreditsActivity", "service connected");
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				mBoundService = null;
				Log.i("CreditsActivity", "service disconnected");
			}
		};

	}
	
	
	
	@Override
	protected void onStart() {
		super.onStart();
		
		bindService(new Intent(this, CreateNotificationService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		Log.i("infos", "mIsBound: " + mIsBound);
	}


	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
		}
	}



}
