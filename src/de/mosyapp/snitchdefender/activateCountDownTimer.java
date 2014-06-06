package de.mosyapp.snitchdefender;

import android.os.CountDownTimer;
import android.util.Log;



public class activateCountDownTimer extends CountDownTimer {
	
	private boolean onFinishCheck = false;
	private long countTime;
	
	
    public activateCountDownTimer(long startTime, long interval) {
        super(startTime, interval);  
        onFinishCheck = false;
       
        Log.i("main", "werte in timerklasse: " + startTime);
    }

    @Override
    public void onFinish() { 
    	onFinishCheck = true;
    }
    
    public boolean onFinishCheck () {
    		return onFinishCheck;
    }
   

    
    
    public void onTick(long millisUntilFinished){
     
    }
    
    
    public long onTick1(long millisUntilFinished) {   
    	countTime = millisUntilFinished / 1000;
    	//countdown.setText("countdown: " + millisUntilFinished / 1000);
    	Log.i("main", "countTime in countklasse: " + countTime);
    	return countTime;
    }
    
    public long getTick1(){
    	return countTime;
    }
    
    /*
    public long onTickCheck(){
    	return countTime;
    }
    */
    
    public void Cancel(){
    	
    }

}