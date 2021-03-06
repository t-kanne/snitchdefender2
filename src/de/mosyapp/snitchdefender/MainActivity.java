/*/////////////////////////////////////////////////////////////////////////////////////////////
MOSY APP// SNITCHDEFENDER
produced by appBert & programmierKanne
	
//////////////////////////////////////////////////////////////
*/
 
package de.mosyapp.snitchdefender;



import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements SensorEventListener, AnimationListener {
	private boolean doubleBackToExitPressedOnce;
	private boolean isLockScreenDisabled;
	
	// Variablen f�r die Anruf�berwachung
	private BroadcastReceiver callReceiver;
	private boolean didPhoneRing;
	private boolean hasHookedOff = false;
	private String callState;
	
	// Variablen f�r den Benachrichtigungsservice
	private ServiceConnection mConnection;
	private boolean mIsBound;
	
	// Variablen f�r den Aktivierungscountdown
	private boolean hasStarted = false;
	private boolean countDownCheck = false;
	private CountdownReceiver cdr;
	private CountdownReceiver cdr2;
	private IntentFilter intentFilter;
	private IntentFilter intentFilter2;
	public boolean isReceiverActive = false;
	
	// Variablen f�r Alarmverwaltung
	private Alarm alarm;
	private boolean vibrationActivated;				// Check, ob Vibration in den Einstellungen eingeschaltet ist
	private boolean flashlightActivated;        	// Check, ob die LED in den Einstellungen eingeschaltet ist
	private boolean buttonPressed = false;			// Check Variable sobald der Button gedr�ckt wurde
	private boolean closeThisApp;					// Wird momentan noch ben�tigt, da sich sonst aktivierter Alarm nicht per Backbutton beenden l�sst
	
	// Sensorberechnungen
	private SensorManager sensorManager;
	private float sensorWerte[] = new float[3];
	private boolean sensor_Check = false;   		//Check Variable sobald X-Wert den Grenzwert �berschreitet
	private float limitValue;
	private float xmax, ymax, zmax; 				// Sensor Grenzwert
	private float x,y,z;

	//Layout
	private boolean menuKeyPressed = false;
	ImageView header1, pushtoactivate;
	ImageButton activateButton;
	TextView countdown;
	Animation pushtoactivatefade;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_main);
		
		// ActionBar gesondert f�r MainActivity �ndern
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
		// Verbindung zum Benachrichtigungs-Service aufbauen
		mConnection = new ServiceConnection() {	
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				((CreateNotificationService.LocalBinder)service).getService();
				Log.i("main", "notificationservice connected");
			}
			
			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				Log.i("main", "notificationservice disconnected");
			}
		};
		
		// Layout einrichten
		countdown = (TextView)findViewById(R.id.countdown);
		activateButton = (ImageButton) findViewById(R.id.activateButton);
		pushtoactivate = (ImageView) findViewById(R.id.pushtoactivate);
		
		// Sensor initialisieren
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 500000); //Wert in Mikrosekunden
		
		addButtonListener();
		
		alarm = new Alarm(this);
		alarm.loadSound();	
		
		cdr = new CountdownReceiver();
		intentFilter = new IntentFilter(ActivateCountDownTimer.COUNTDOWN_BR);
		registerReceiver(cdr, intentFilter);
		cdr2 = new CountdownReceiver();
		intentFilter2 = new IntentFilter(DeactivateCountDownTimer.COUNTDOWN_BR);
		registerReceiver(cdr2, intentFilter2);
	
		pushtoactivatefade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pushtoactivatefade);
		pushtoactivatefade.setAnimationListener(this);
		pushtoactivate.startAnimation(pushtoactivatefade);	
		pushtoactivate.setVisibility(View.VISIBLE);
		
		// CallReceiver-Service starten
		Intent callReceiverIntent = new Intent(this, CallReceiverService.class);
		startService(callReceiverIntent);
		
	}
	
	
	// Beim Starten wird Benachrichtigung an diese Activity gebunden.
	@Override
	protected void onStart() {
		super.onStart();
		bindService(new Intent(this, CreateNotificationService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		Log.i("infos", "mIsBound: " + mIsBound);
	}

	// App Einstellungen werden laufend abgefragt
	@Override
	protected void onResume() {
		super.onResume();	
		
		// Einstellungen aufrufen aus SettingsActivity
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Aufrufen der Sensibilit�t aus den Einstellungen
		limitValue = Float.parseFloat(preferences.getString("sensitivity_key", "3"));
		Log.i("prefs", "(sp) limitValue: " + limitValue);

		// Aufrufen, ob Vibration in den Einstellungen aktiviert ist
		vibrationActivated = preferences.getBoolean("notifications_vibrate_key", true);
		Log.i("prefs", "(sp) vibrationActivated: " + vibrationActivated);
		
		// Aufrufen, ob LED in den Einstellungen aktiviert ist
		flashlightActivated = preferences.getBoolean("notifications_flashlight_key", true);
		Log.i("prefs", "(sp) flashlightActivated: " + flashlightActivated);
		
		// Aufrufen, ob Bildschirm gedimmt werden soll
		isLockScreenDisabled = preferences.getBoolean("pref_lockscreen_mode_key", false);
		Log.i("dimm", "lockScreenDisabled: " + isLockScreenDisabled);
		
		// Countdown-Receiver registrieren
		registerReceiver(cdr, intentFilter);
		registerReceiver(cdr2, intentFilter2);
		
		// CallReceiver registrieren
		IntentFilter callRecIntFilter = new IntentFilter("android.intent.action.MAIN");
		callReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				callState = intent.getStringExtra("state");
				handleCallState(callState);
			}
		};
		this.registerReceiver(callReceiver, callRecIntFilter);
		
		Log.i("main", "onresume()");
	}

	public void onSensorChanged(SensorEvent event){
		
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
		
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
		
		//Betrag ermitteln
		xmax = Math.abs(x);
		ymax = Math.abs(y);
		zmax = Math.abs(z);		
		}
	  compareSensorData();
	}
	
	
	//Verarbeiten der im Array gespeicherten X,Y Werte
	public void compareSensorData(){

		float x_array = sensorWerte[0];
		float x_array2 = Math.abs(x_array);
		float x_array_compare = x_array2 + limitValue;
		
		float y_array = sensorWerte[1];
		float y_array2 = Math.abs(y_array);
		float y_array_compare2 = y_array2 - limitValue;
		float y_array_compare = y_array2 + limitValue;
		
		float z_array = sensorWerte[2];
		float z_array2 = Math.abs(z_array);
		float z_array_compare2 = z_array2 - limitValue;
		float z_array_compare = z_array2 + limitValue;
		
		if(xmax > x_array_compare || ymax > y_array_compare || ymax < y_array_compare2 || zmax > z_array_compare || zmax < z_array_compare2){
			sensor_Check = true;
		}
		else if(xmax < x_array_compare || ymax < y_array_compare){
			sensor_Check = false;
		}
	
		activateAlarms();	
	}
	
	
	public void addButtonListener() { 
		activateButton = (ImageButton) findViewById(R.id.activateButton);
		activateButton.setOnClickListener(new OnClickListener() {
 
			public void onClick(View v) {					
				
				// Nach dem Countdown-Timer werden aktuelle Positionsdaten gespeichert.
				// --> ausgelagert in setActualSensorData ()		
					
				if(buttonPressed == false){
					buttonPressed = true;
					alarm.startVibrationOnActivate();
					updateNotification(true);	
					pushtoactivate.clearAnimation();
				    pushtoactivate.setVisibility(View.INVISIBLE);			  
				}
				else if(buttonPressed == true){
					Log.i("infos", "Gleich wird gestoppt");
					stopAlarms();
					alarm.startVibrationOnActivate();
					activateButton.setImageResource(R.drawable.hover);
				}
				startCountDownTimer();
			}
		});
	}
	
	//�berpr�fung: wurde der Aktivierbutton gedr�ckt UND ein Sensor Grenzwert �berschritten?
	// -> Sound ausl�sen
	public void activateAlarms(){
		if(buttonPressed && sensor_Check && countDownCheck && closeThisApp == false){
			alarm.startSound();
			alarm.startVibration(vibrationActivated);
			alarm.startFlashLight(flashlightActivated);
		}
	}
	
	public void stopAlarms(){
		alarm.stopSound();
		alarm.stopVibration(vibrationActivated);
		alarm.stopFlashLight();
		updateNotification(false);
		countDownCheck = false;
		buttonPressed = false;
	}
	
	public void startCountDownTimer(){
	
		if(hasStarted == false){
			buttonPressed = true;
			stopService(new Intent(this, DeactivateCountDownTimer.class));
			startService(new Intent(this, ActivateCountDownTimer.class));
			hasStarted = true;
			Log.i("main", "startCountDownTimer() if hasStarted == false");
		}
		else if(hasStarted == true){
			stopService(new Intent(this, ActivateCountDownTimer.class));
			countdown.setText("deaktiviert!");
			startService(new Intent(this, DeactivateCountDownTimer.class));
			hasStarted = false;
			Log.i("main", "startCountDownTimer() if hasStarted == true");
		}
	}
	
	public class CountdownReceiver extends BroadcastReceiver {
		@Override
	    public void onReceive(Context context, Intent intent) {
	        setActivationCountDown(intent);
	        setActivationCountDown2(intent);
	        setDeactivationCountDown3(intent);
	    }
	}

	
	private void setActivationCountDown(Intent intent) {
	    if (intent.getExtras() != null) {
	        long millisUntilFinished = intent.getLongExtra("countdown", 0);
	        long countDownFinal = intent.getLongExtra("countdownFinished", 0);
	        countdown.setText("Aktivierung in " + millisUntilFinished + " Sekunden!");      
	        if(countDownFinal == 12){    
	        	 countdown.setText("aktiviert!");
	        	 activateButton.setImageResource(R.drawable.hover2);
	        }
	    }
	}
	
	private void setActivationCountDown2(Intent intent) {
		if (intent.getExtras() != null) {
	        long countdownFinished2 = intent.getLongExtra("countdownFinished2", 0); 
	        if(countdownFinished2 == 22){
	        	countdown.setText("");
	        	countDownCheck = true;
	        	setActualSensorData();
	        	if (isLockScreenDisabled) {
		        	Intent dimmIntent = new Intent(this,DimmActivity.class);
		        	startActivity(dimmIntent);
	        	}
	        }
	    }
	}
	
	private void setDeactivationCountDown3(Intent intent) {
		if (intent.getExtras() != null) {
	        long deactivationCountDown = intent.getLongExtra("countDownOnFinish", 0); 
	        if(deactivationCountDown == 33){  
	        	countdown.setText("");
	        	stopService(new Intent(this, DeactivateCountDownTimer.class));
	            pushtoactivate.setVisibility(View.VISIBLE);	
	        	pushtoactivate.startAnimation(pushtoactivatefade);	
	        }
	    }
	}
	
	public void setActualSensorData () {
		 //Speichern der aktuell gemessen Sensorwerte in ein Array
			sensorWerte[0] = x;
			sensorWerte[1] = y;
			sensorWerte[2] = z;
			Log.i("infos", "sensorwerte ins array geladen");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings){
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
		if (id == R.id.action_help){
			Intent intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	public void onBackPressed() {	
		stopService(new Intent(this, ActivateCountDownTimer.class));
		
		if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, R.string.main_activity_close_app, Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	}
	
	// Beenden der Activity sorgt f�r folgende Dinge
	@Override
	public void onDestroy() {	
		super.onDestroy();
		Log.i("main", "onDestroy() aufgerufen");
		
		try {
			unregisterReceiver(cdr);
			unregisterReceiver(cdr2);
			unregisterReceiver(callReceiver);
		} catch (IllegalArgumentException e) {
			Log.i("main", "Mind. einer der Receiver war nicht registriert");
		}
		
		if (mIsBound) {
			unbindService(mConnection);					// Benachrichtigung ausschalten
			mIsBound = false;
		}	
		if (alarm.isAlarmActivated() == true) {
			Log.i("infos", "App zwangsbeendet");		// Zwangsbeenden, wenn Alarm noch aktiv
			System.exit(0);	
		}
		else {
			Log.i("infos", "App normal beendet");
			closeThisApp = true;
			this.finish(); 								// Activity normal schlie�en, wenn Alarm nicht aktiv
		}
		stopService(new Intent(this,CallReceiverService.class));
	}
	

	public void updateNotification(boolean isActive) {
		Intent intent = new Intent (MainActivity.this, CreateNotificationService.class);
		intent.putExtra("isDefendActive", isActive);
		
		try {
			unbindService(mConnection);
		} 
		catch (IllegalArgumentException e) {
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
		finally {
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    ActionBar actionbar = getSupportActionBar();
		
	    switch(keycode) {
	    	case KeyEvent.KEYCODE_MENU:
	        	
	        	if (menuKeyPressed == false) {
		        	actionbar.hide();
		    	    menuKeyPressed = true;    
	        	}
	        	else {
	        		actionbar.show();
	        		menuKeyPressed = false;
	        	}
	    }
	    return super.onKeyDown(keycode, e);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    Log.i("main", "onPause() aufgerufen");  
	    
	    this.unregisterReceiver(this.cdr);
	    this.unregisterReceiver(this.cdr2);
	}

	// Telefonstatus wird �berwacht. Wenn Anruf eingeht, dann Alarm deaktivieren.
	// Wenn Anruf beendet/abgelehnt/ignoriert, dann Countdown wieder aktivieren.
	// Daten kommen per Intent aus IncomingCallTracker (BroadcastReceiver)
	
	public void handleCallState(String callState) {
			
		try {
			if (callState.equalsIgnoreCase("RINGING")) {
				Log.i("phone", "phonestatelistener ringing aufgerufen");
		        if (buttonPressed){
		        	stopAlarms(); 
		        	activateButton.setImageResource(R.drawable.hover);
		         	didPhoneRing = true;
		        }
			}
			if (callState.equalsIgnoreCase("IDLE")) {
				Log.i("phone", "phonestatelistener idle aufgerufen");
		        if (hasStarted == true && didPhoneRing && buttonPressed == false && hasHookedOff == false) {
		        	hasStarted = false;
		        	//finishActivity(1);
		        	stopService(new Intent(MainActivity.this, ActivateCountDownTimer.class));
		        	startCountDownTimer();
		        }
		        if (hasStarted == true && didPhoneRing && buttonPressed == false && hasHookedOff == true) {
		        	hasStarted = false;
		        	stopService(new Intent(MainActivity.this, ActivateCountDownTimer.class));
		        }
			}
			 
		    if (callState.equalsIgnoreCase("OFFHOOK")) {
		    	Log.i("phone", "phonestatelistener offhook aufgerufen");
		    	this.moveTaskToBack(true);
				hasHookedOff = true;
			}
		}
		catch (NullPointerException e) {
			Log.i("main", "handleCallState() hat eine Exception geworfen");
		}
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
	}

	@Override
	public void onAnimationStart(Animation arg0) {
	}
	 	
}
