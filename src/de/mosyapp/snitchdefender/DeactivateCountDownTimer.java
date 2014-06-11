package de.mosyapp.snitchdefender;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class DeactivateCountDownTimer extends Service {

    public static final String COUNTDOWN_BR = "de.mosyapp.snitchdefender.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer1 = null;
  
   

    @Override
        public void onCreate() {       
            super.onCreate();
            Log.i("Deactivatecountdowntimer","onCreate()");
            countDownTimer1 = new CountDownTimer(800, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                	Log.i("deactivatecountdowntimer","onTick()");
                }

                @Override
                public void onFinish() {
                	long count = 33;
                	Log.i("deactivatecountdowntimer","onfinish()");
                	bi.putExtra("countDownOnFinish", count);
                	sendBroadcast(bi);
                }
            };
            countDownTimer1.start();
        }

    
    
        @Override
        public void onDestroy() {
        	countDownTimer1.cancel();
            Log.i("DeactivateCountDownTimer", "onDestroy() aufgerufen");
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