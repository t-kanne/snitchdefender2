/*/////////////////////////////////////////////////////////////////////////////////////////////
MOSY APP// SNITCHDEFENDER
produced by appBert & programmierKanne
MOMENTANER STAND
------------------------------------
	
	Alarm ist per Button (de)aktivierbar
	
	Zeile 1-3:
		ausgelesene Momentanwerte f�r X/Y/Z Werte werden angezeigt in den ersten drei Zeilen

	Zeile 4-6:
		maximal erreichte Werte f�r X/Y/Z
		
	Variable "check" (boolean) wird per Button true/false gesetzt
	Check Variable als "Freigabe" Variable
	sobald true, ist die Soundaktivierung m�glich bei Grenzwert�berschreitung (nicht nur m�glich, sondern sicher, alter!)
	
//////////////////////////////////////////////////////////////
*/
 
package de.mosyapp.snitchdefender;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.SoundPool;
import android.os.Bundle;
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
	private SensorManager sensorManager;
	SettingsActivity settingsActivity;
	
	//private MediaPlayer mp; 
	Alarm alarm;
	Context context = this;
	
	float sensorWerte[] = new float[3];
	
	SoundPool sp;
	int soundId;
	boolean loaded;
	
	ImageView imageLogo1, imageLine1;
	ImageButton imageButton1;
	TextView xValue, yValue, zValue, check1; 
	TextView max_view_x, max_view_y, max_view_z; 
	TextView xArray, yArray, zArray; 
	float xmax, ymax, zmax;
	float xmax_abs, ymax_abs, zmax_abs;
	float x_compare, y_compare, z_compare;
	boolean sensor_Check = false;   //Check Variable sobald X-Wert den Grenzwert �berschreitet
	float limitValue;  		 	// Sensor Grenzwert
	boolean check = false;			  // Check Variable sobald der Button gedr�ckt wurde
	float x,y,z;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		settingsActivity = new SettingsActivity();
		limitValue = settingsActivity.getSensorSensibility();
		Log.i("infos","limitValue: "+limitValue);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		imageLine1 = (ImageView) findViewById(R.id.imageLine1);
		imageLogo1 = (ImageView) findViewById(R.id.imageLogo1);

		xValue=(TextView)findViewById(R.id.xcoor);
		yValue=(TextView)findViewById(R.id.ycoor); 
		zValue=(TextView)findViewById(R.id.zcoor); 
		
		max_view_x = (TextView)findViewById(R.id.max_x_text);
		max_view_y = (TextView)findViewById(R.id.max_y_text);
		max_view_z = (TextView)findViewById(R.id.max_z_text);
		
		check1 = (TextView)findViewById(R.id.check1);
		
		xArray = (TextView)findViewById(R.id.xArray);
		yArray = (TextView)findViewById(R.id.yArray);
		zArray = (TextView)findViewById(R.id.zArray);
		
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		addButtonListener();
		
		alarm = new Alarm(context);
		alarm.loadSound();
		
		
		
		}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		limitValue = settingsActivity.getSensorSensibility();
		
		Log.i("infos","(resume) limitValue: "+limitValue);
	}



	public void onAccuracyChanged(Sensor sensor,int accuracy){}
	
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
				
				check1.setText("check: " + check);
				
			}
		compareSensorData();
			
		
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
				
				
				if(check == false){
					check = true;
				}
				else if(check == true){
					Log.i("infos", "Gleich wird gestoppt");
					alarm.stopSound();
					alarm.stopVibration(settingsActivity.getVibration());
					check = false;
				}
				
			}
			
		});
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
	
		sensorTest();
		
	}
	
	
	
	
	
	
	
	//�berpr�fung: wurde der Aktivierbutton gedr�ckt UND ein Sensor Grenzwert �berschritten?
	// -> Sound ausl�sen
	public void sensorTest(){
		if(check == true && sensor_Check == true){
			alarm.startSound();
			alarm.startVibration(settingsActivity.getVibration());
		}

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

	
	

}
