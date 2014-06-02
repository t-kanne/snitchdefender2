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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager sensorManager;
	
	//private MediaPlayer mp; 
	Audio audio;
	Context context = this;
	MediaPlayer mp;
	
	SoundPool sp;
	int soundId;
	boolean loaded;
	
	ImageView imageLogo1, imageLine1;
	ImageButton imageButton1;
	TextView xValue, yValue, zValue, check1; 
	TextView max_view_x, max_view_y, max_view_z; 
	float xmax, ymax, zmax;
	float xmax_abs, ymax_abs, zmax_abs;
	float x_compare, y_compare, z_compare;
	boolean sensor_Check = false;   //Check Variable sobald X-Wert den Grenzwert �berschreitet
	float limitValue = 3;  			  // Sensor Grenzwert
	boolean check = false;			  // Check Variable sobald der Button gedr�ckt wurde
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		
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
		
		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		addButtonListener();
		
		audio = new Audio(context);
		audio.loadSound();
		}


	public void onAccuracyChanged(Sensor sensor,int accuracy){}
	
	public void onSensorChanged(SensorEvent event){
		
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
			
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
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

		//aktuell nur �berpr�fung der X-Achse und Y-Achse (der Einfachheit halber und so)
		if(xmax > limitValue || ymax > limitValue){
			sensor_Check = true;
		}
		else if(xmax < limitValue || ymax < limitValue){
			sensor_Check = false;
		}
		sensorTest();
	}
	
	
	public void addButtonListener() { 
		imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		imageButton1.setOnClickListener(new OnClickListener() {
 
			public void onClick(View v) {	
				if(check == false){
					check = true;
				}
				else if(check == true){
					Log.i("infos", "Gleich wird gestoppt");
					audio.stopSound();
					check = false;
				}
				
			}
			
		});
	}
	
	//�berpr�fung: wurde der Aktivierbutton gedr�ckt UND ein Sensor Grenzwert �berschritten?
	// -> Sound ausl�sen
	public void sensorTest(){
		if(check == true && sensor_Check == true){
			audio.startSound();
			
		}

	}


}

	
	
	
