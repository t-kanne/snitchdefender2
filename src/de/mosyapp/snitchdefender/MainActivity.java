/*/////////////////////////////////////////////////////////////////////////////////////////////
MOSY APP// SNITCHDEFENDER
produced by appBert & programmierKanne
	
//////////////////////////////////////////////////////////////
*/
 
package de.mosyapp.snitchdefender;


import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements SensorEventListener {
	private boolean doubleBackToExitPressedOnce;
	private boolean isLockScreenDisabled;
	
	// Variablen für den Benachrichtigungsservice
	private ServiceConnection mConnection;
	private boolean mIsBound;
	
	//Variablen für den Aktivierungscountdown
	private boolean hasStarted = false;
	private boolean countDownCheck = false;
	private CountdownReceiver cdr;
	private CountdownReceiver cdr2;
	private IntentFilter intentFilter;
	private IntentFilter intentFilter2;
	private boolean isReceiverActive = false;
	
	// Variablen für Alarmverwaltung
	private Alarm alarm;
	private boolean vibrationActivated;				// Check, ob Vibration in den Einstellungen eingeschaltet ist
	private boolean flashlightActivated;        	// Check, ob die LED in den Einstellungen eingeschaltet ist
	private boolean buttonPressed = false;			// Check Variable sobald der Button gedrückt wurde
	private boolean closeThisApp;					// Wird momentan noch benötigt, da sich sonst aktivierter Alarm nicht per Backbutton beenden lässt
	
	// Sensorberechnungen
	private SensorManager sensorManager;
	private float sensorWerte[] = new float[3];
	private float x_compare, y_compare, z_compare;
	private boolean sensor_Check = false;   		//Check Variable sobald X-Wert den Grenzwert überschreitet
	private float limitValue;
	private float xmax, ymax, zmax; 				// Sensor Grenzwert
	private float x,y,z;
	
	//Layout
	private boolean menuKeyPressed = false;
	ImageView imageLogo1, imageLine1;
	ImageButton imageButton1;
	TextView xValue, yValue, zValue, check1; 
	TextView max_view_x, max_view_y, max_view_z; 
	TextView xArray, yArray, zArray; 
	TextView countdown;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_main);
		
		// ActionBar gesondert für MainActivity ändern
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		
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
		imageLine1 = (ImageView) findViewById(R.id.imageLine1);
		imageLogo1 = (ImageView) findViewById(R.id.imageLogo1);

		xValue=(TextView)findViewById(R.id.xcoor);
		yValue=(TextView)findViewById(R.id.ycoor); 
		zValue=(TextView)findViewById(R.id.zcoor); 
		
		max_view_x = (TextView)findViewById(R.id.max_x_text);
		max_view_y = (TextView)findViewById(R.id.max_y_text);
		max_view_z = (TextView)findViewById(R.id.max_z_text);
		
		check1 = (TextView)findViewById(R.id.check1);
		countdown = (TextView)findViewById(R.id.countdown);
		
		xArray = (TextView)findViewById(R.id.xArray);
		yArray = (TextView)findViewById(R.id.yArray);
		zArray = (TextView)findViewById(R.id.zArray);
		
		// Sensor initialisieren
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		addButtonListener();
		
		alarm = new Alarm(this);
		alarm.loadSound();	
		
		cdr = new CountdownReceiver();
		intentFilter = new IntentFilter(ActivateCountDownTimer.COUNTDOWN_BR);
		registerReceiver(cdr, intentFilter);
		cdr2 = new CountdownReceiver();
		intentFilter2 = new IntentFilter(DeactivateCountDownTimer.COUNTDOWN_BR);
		registerReceiver(cdr2, intentFilter2);
		
	
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
		
		// Aufrufen der Sensibilität aus den Einstellungen
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
		
		registerReceiver(cdr, intentFilter);
		registerReceiver(cdr2, intentFilter2);
		
		Log.i("main", "onresume()");
	}

	
	public void onSensorChanged(SensorEvent event){
		
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			
			// folgende Werte bzw. TextViews sind eigentlich nur zur Anzeige gedacht
			// sollen dann natürlich weg
			xValue.setText("X: "+ x);
			yValue.setText("Y: "+ y);
			zValue.setText("Z: "+ z);
			
			//Betrag ermitteln
			xmax = Math.abs(x);
			ymax = Math.abs(y);
			zmax = Math.abs(z);

			// Anzeige der Maximalwerte
				if(xmax > x_compare){
					max_view_x.setText("max-x: " + xmax);
					x_compare = xmax;
				}
				
				if(ymax > y_compare){
					max_view_y.setText("max-y: " + ymax);
					y_compare = ymax;
				}
				
				if(zmax > z_compare){
					max_view_z.setText("max-z: " + zmax);
					z_compare = zmax;
				}
				
				check1.setText("check: " + buttonPressed);
				
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
	
	//Überprüfung: wurde der Aktivierbutton gedrückt UND ein Sensor Grenzwert überschritten?
	// -> Sound auslösen
	public void activateAlarms(){
		if(buttonPressed && sensor_Check && countDownCheck && closeThisApp == false){
			alarm.startSound();
			alarm.startVibration(vibrationActivated);
			alarm.startFlashLight(flashlightActivated);
		}
	}
	
	public void addButtonListener() { 
		imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		imageButton1.setOnClickListener(new OnClickListener() {
 
			public void onClick(View v) {					
				
				// Nach dem Countdown-Timer werden aktuelle Positionsdaten gespeichert.
				// --> ausgelagert in setActualSensorData ()
				startCountDownTimer();
				
				if(buttonPressed == false){
					buttonPressed = true;
					alarm.startVibrationOnActivate();
					updateNotification(true);					
				}
				else if(buttonPressed == true){
					Log.i("infos", "Gleich wird gestoppt");
					alarm.stopSound();
					alarm.stopVibration(vibrationActivated);
					alarm.stopFlashLight();
					
					buttonPressed = false;
					alarm.startVibrationOnActivate();
					updateNotification(false);
					countDownCheck = false;
				}
			}
		});
	}

	public void startCountDownTimer(){
	
		if(hasStarted == false){
			stopService(new Intent(this, DeactivateCountDownTimer.class));
			startService(new Intent(this, ActivateCountDownTimer.class));
			hasStarted = true;
		}
		else if(hasStarted == true){
			stopService(new Intent(this, ActivateCountDownTimer.class));
			countdown.setText("deaktiviert!");
			startService(new Intent(this, DeactivateCountDownTimer.class));
			hasStarted = false;
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
	        }
	    }
	}
	
	
	
	public void setActualSensorData () {
		 //Speichern der aktuell gemessen Sensorwerte in ein Array
			sensorWerte[0] = x;
			sensorWerte[1] = y;
			sensorWerte[2] = z;
		
			xArray.setText("xA: "+ sensorWerte[0]);
			yArray.setText("yA: "+ sensorWerte[1]);
			zArray.setText("zA: "+ sensorWerte[2]);
			Log.i("infos", "sensorwerte ins array geladen");
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    Log.i("main", "onPause() aufgerufen");    
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
	
	// Beenden der Activity sorgt für folgende Dinge
	@Override
	public void onDestroy() {	
		super.onDestroy();
		Log.i("main", "onDestroy() aufgerufen");
		
		try {
			unregisterReceiver(cdr);
		} catch (IllegalArgumentException e) {
			Log.i("main", "Receiver nicht registriert");
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
			this.finish(); 								// Activity normal schließen, wenn Alarm nicht aktiv
		}
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
		// TODO Auto-generated method stub
		
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

}
