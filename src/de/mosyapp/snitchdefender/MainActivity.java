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
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements SensorEventListener {
	
	// Variablen f�r den Benachrichtigungsservice
	private ServiceConnection mConnection;
	private boolean mIsBound;
	private CountDownTimer activateTimer;
	
	//Variablen f�r den Aktivierungscountdown
	private boolean hasStarted = false;
	private boolean countDownCheck = false;
	
	// Variablen f�r Alarmverwaltung
	private Alarm alarm;
	private boolean vibrationActivated;				// Check, ob Vibration in den Einstellungen eingeschaltet ist
	private boolean flashlightActivated;        	// Check, ob die LED in den Einstellungen eingeschaltet ist
	private boolean buttonPressed = false;			// Check Variable sobald der Button gedr�ckt wurde
	
	// Sensorberechnungen
	private SensorManager sensorManager;
	private float sensorWerte[] = new float[3];
	private float x_compare, y_compare, z_compare;
	private boolean sensor_Check = false;   		//Check Variable sobald X-Wert den Grenzwert �berschreitet
	private float limitValue;
	private float xmax, ymax, zmax; 				// Sensor Grenzwert
	private float x,y,z;
	
	//Layout
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
		
		// Verbindung zum Benachrichtigungs-Service aufbauen
		mConnection = new ServiceConnection() {	
			@Override
			public void onServiceConnected(ComponentName className, IBinder service) {
				((CreateNotificationService.LocalBinder)service).getService();
				Log.i("CreditsActivity", "service connected");
			}
			
			@Override
			public void onServiceDisconnected(ComponentName arg0) {
				Log.i("CreditsActivity", "service disconnected");
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
		limitValue = Float.parseFloat(preferences.getString("sensitivity_list", "3"));
		Log.i("prefs", "(sp) limitValue: " + limitValue);

		// Aufrufen, ob Vibration in den Einstellungen aktiviert ist
		vibrationActivated = preferences.getBoolean("notifications_vibrate_key", true);
		Log.i("prefs", "(sp) vibrationActivated: " + vibrationActivated);
		
		// Aufrufen, ob LED in den Einstellungen aktiviert ist
		flashlightActivated = preferences.getBoolean("notifications_flashlight_key", true);
		Log.i("prefs", "(sp) flashlightActivated: " + flashlightActivated);
		
		super.onResume();        
	    registerReceiver(br, new IntentFilter(activateCountDownTimer.COUNTDOWN_BR));
	}

	
	public void onSensorChanged(SensorEvent event){
		
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			
			x = event.values[0];
			y = event.values[1];
			z = event.values[2];
			
			// folgende Werte bzw. TextViews sind eigentlich nur zur Anzeige gedacht
			// sollen dann nat�rlich weg
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
	
	//�berpr�fung: wurde der Aktivierbutton gedr�ckt UND ein Sensor Grenzwert �berschritten?
	// -> Sound ausl�sen
	public void activateAlarms(){
		if(buttonPressed == true && sensor_Check == true && countDownCheck == true){
			alarm.startSound();
			alarm.startVibration(vibrationActivated);
			alarm.startFlashLight(flashlightActivated);
		}
	}
	
	public void addButtonListener() { 
		imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		imageButton1.setOnClickListener(new OnClickListener() {
 
			public void onClick(View v) {					
			 //Speichern der aktuell gemessen Sensorwerte in ein Array
				sensorWerte[0] = x;
				sensorWerte[1] = y;
				sensorWerte[2] = z;
			
				xArray.setText("xA: "+ sensorWerte[0]);
				yArray.setText("yA: "+ sensorWerte[1]);
				zArray.setText("zA: "+ sensorWerte[2]);
				Log.i("infos", "sensorwerte ins array geladen");

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
					updateNotification(false);
					countDownCheck = false;
				}
			}
		});
	}
	
	
	public void startCountDownTimer(){
		if(hasStarted == false){
			startService(new Intent(this, activateCountDownTimer.class));
			hasStarted = true;
		}
		else if(hasStarted == true){
			stopService(new Intent(this, activateCountDownTimer.class));
			countdown.setText("deaktiviert!");
			super.onDestroy();
			hasStarted = false;
		}
	}
	
	private BroadcastReceiver br = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {            
	        setActivationCountDown(intent);
	        setActivationCountDown2(intent);
	    }
	};
	
	
	
	private void setActivationCountDown(Intent intent) {
	    if (intent.getExtras() != null) {
	        long millisUntilFinished = intent.getLongExtra("countdown", 0);
	        long countDownFinal = intent.getLongExtra("countdownFinished", 0);
	        countdown.setText("Aktivierung in " + millisUntilFinished + " Sekunden!");
	        Log.i("infos","count vor if: " + countDownFinal);    
	        if(countDownFinal == 12){
	        	Log.i("infos","count nach if: " + countDownFinal);      
	        	 countdown.setText("aktiviert!");
	        	 //alarm.startVibrationOnActivate();
	        }
	    }
	}
	
	private void setActivationCountDown2(Intent intent) {
		if (intent.getExtras() != null) {
	        long countdownFinished2 = intent.getLongExtra("countdownFinished2", 0); 
	        if(countdownFinished2 == 22){
	        	Log.i("infos","countdownfinished2:" + countdownFinished2);   
	        	Log.i("infos","funktioniert!!");   
	        	countdown.setText("");
	        	countDownCheck = true;
	        }
	    }
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    unregisterReceiver(br);
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		Log.i("main", "Hier muss noch ein Best�tigungsdialog kommen");
	}
	
	// Beenden der Activity sorgt f�r folgende Dinge
	@Override
	public void onDestroy() {	
		super.onDestroy();

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
			this.finish(); 								// Activity normal schlie�en, wenn Alarm nicht aktiv
		}
	}
	
	public void onStop() {
	    try {
	        unregisterReceiver(br);
	    } catch (Exception e) {
	    }
	    super.onStop();
	}

	public void updateNotification(boolean isActive) {
		Intent intent = new Intent (MainActivity.this, CreateNotificationService.class);
		intent.putExtra("isDefendActive", isActive);
		unbindService(mConnection);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
