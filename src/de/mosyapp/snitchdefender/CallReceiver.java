package de.mosyapp.snitchdefender;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver{
    TelephonyManager telManager;
    Context context;

@Override
public void onReceive(Context context, Intent intent) {


    this.context=context;

    telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    
    Log.i("callReceiver", "onReceive() aufgerufen");

}

private final PhoneStateListener phoneListener = new PhoneStateListener() {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        try {
            switch (state) {
            case TelephonyManager.CALL_STATE_RINGING: {
                Log.i("callReceiver", "call state ringing");
            break;
            }
            case TelephonyManager.CALL_STATE_OFFHOOK: {
            	Log.i("callReceiver", "call state offhook");
            break;
            }
            case TelephonyManager.CALL_STATE_IDLE: {
            	Log.i("callReceiver", "call state idle");
            break;
            }
            default: { }
            }
            } catch (Exception ex) {

            }
        }
    };
 }