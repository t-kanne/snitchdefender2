package de.mosyapp.snitchdefender;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class activateCountDownTimer extends Service {

    public static final String COUNTDOWN_BR = "de.mosyapp.snitchdefender.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;
    CountDownTimer cdt2 = null;
    CountDownTimer cdt3 = null;
    private int count;
    private Alarm alarm;
   

    @Override
        public void onCreate() {       
            super.onCreate();

            cdt = new CountDownTimer(5200, 1000) {
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