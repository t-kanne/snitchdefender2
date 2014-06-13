package de.mosyapp.snitchdefender;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager.WakeLock;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

public class TheService extends Service implements SensorEventListener {
	
    public static final String TAG = TheService.class.getName();
    public static final int SCREEN_OFF_RECEIVER_DELAY = 1000;
    float x,y,z;
    private SensorManager mSensorManager = null;
    private WakeLock mWakeLock = null;

   
    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerListener();
        PowerManager manager =(PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        Log.i("THESERVICE", "ONCREATE() GESTARTET");
    }
    
    private void registerListener() {
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterListener() {
        mSensorManager.unregisterListener(this);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive("+intent+")");

            if (!intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                return;
             
            }
            
            Runnable runnable = new Runnable() {
                public void run() {
                	
                    Log.i(TAG, "Runnable executing.");
                    unregisterListener();
                    registerListener();
                }
            };
            

            new Handler().postDelayed(runnable, SCREEN_OFF_RECEIVER_DELAY);
        }
       
        
    };

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged().");
    }
    
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "onSensorChanged().");
        Log.i("onSensorChanged()", "blabla hier vergleich der sensordaten");
        	if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
        		x = event.values[0];
				y = event.values[1];
				z = event.values[2];
				Log.i("onSensorChanged()", "sensordaten gespeichert");
				Log.i("onSensorChanged()", "DATEN: " + x);

        	}
    }

   

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterListener();
        if(mWakeLock.isHeld()) {
            mWakeLock.release();
            Log.i("", "Wakelock released");
        }
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        
        if(!mWakeLock.isHeld()){
            mWakeLock.acquire();
        }
        Log.i("TheService", "onStartCommand()");
        return START_STICKY;
    }
}




