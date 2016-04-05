package com.ciego.sensorappnativa;

import java.util.Timer;
import java.util.TimerTask;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.ciego.sensorappnativa.data.UserData;

public class SplashScreenActivity extends ActionBarActivity {

	private static final long SPLASH_SCREEN_DELAY = 0;
	
	
	SharedPreferences preferences;
	
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getSupportActionBar().hide();
		setContentView(R.layout.activity_splash_screen);
		
		/*preferences = getSharedPreferences("datosAcceso", Context.MODE_PRIVATE);
		
		int userId = preferences.getInt("userId", 0);
		String userPass = preferences.getString("userPass", "");
		String regId = preferences.getString("regId", "");*/
		
		if(!UserData.isLogged(getApplicationContext())){
			intent = new Intent(SplashScreenActivity.this, MainActivity.class);
			TimerTask task = new TimerTask() {				
				@Override
				public void run() {
					// TODO Auto-generated method stub					
					startActivity(intent);
					SplashScreenActivity.this.finish();
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, SPLASH_SCREEN_DELAY);
		}else{
			intent = new Intent(SplashScreenActivity.this, DashbordActivity.class);
			TimerTask task = new TimerTask() {				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startActivity(intent);
					SplashScreenActivity.this.finish();
				}
			};
			Timer timer = new Timer();
			timer.schedule(task, SPLASH_SCREEN_DELAY);
		}
	}
}
