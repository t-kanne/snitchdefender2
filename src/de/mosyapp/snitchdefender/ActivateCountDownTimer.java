package de.mosyapp.snitchdefender;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class ActivateCountDownTimer extends Service {

    public static final String COUNTDOWN_BR = "de.mosyapp.snitchdefender.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    CountDownTimer cdt2 = null;
    private Alarm alarm;
   

    @Override
        public void onCreate() {       
            super.onCreate();
       
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    		long countdownDuration = Long.parseLong(preferences.getString("countdown_key", "5"));
    		Log.i("prefs", "(sp) countdownDuration: " + countdownDuration);
    		
            Log.i("Activatecountdowntimer","onCreate()");
            cdt = new CountDownTimer((countdownDuration*1000)+200, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                	millisUntilFinished = millisUntilFinished / 1000;  
                    bi.putExtra("countdown", millisUntilFinished);
                    sendBroadcast(bi);
                }

                @Override
                public void onFinish() {
                	long count = 12;
                	bi.putExtra("countdownFinished", count);
                	sendBroadcast(bi);
                    delayCounter();
                }
            };
            cdt.start();
        }

    	public void delayCounter(){
    		alarm = new Alarm(this);
    		alarm.startVibrationOnActivate();
    		cdt2 = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {	}
                @Override
                public void onFinish() {
                	long count = 22;
                	bi.putExtra("countdownFinished2", count);
                	sendBroadcast(bi);
                }
            };
            cdt2.start();
    	}
    
        @Override
        public void onDestroy() {
            cdt.cancel();
            Log.i("ActivateCountDownTimer", "onDestroy() aufgerufen");
            super.onDestroy();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {       
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent arg0) {       
            return null;
        }
}