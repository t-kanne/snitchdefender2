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

public class ShakeWakeUpService extends Service implements SensorEventListener{

    private Context mContext;
    SensorManager mSensorEventManager;
    Sensor mSensor;

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("shakewakeupservice","trying re-registration");
                mSensorEventManager.unregisterListener(ShakeWakeUpService.this);
                mSensorEventManager.registerListener(ShakeWakeUpService.this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("shake service startup","registering for shake");

        mContext = getApplicationContext();
        mSensorEventManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);

        mSensor = mSensorEventManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorEventManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        mSensorEventManager.unregisterListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
    	return null;
    }


    public void onShake() {

    }


         public void onAccuracyChanged(Sensor sensor, int accuracy) { }


           public void onSensorChanged(SensorEvent event) {
        	   if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
        		   Log.i("shakewakeupservice","sensordaten werden eingelesen");
        	   }
          }
  }