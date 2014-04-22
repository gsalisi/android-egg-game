package ca.gsalisi.games.eggs;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StartingActivity extends Activity {
	 
	
	private boolean soundOn;
	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starting);
		Log.d("GS", "On first activity");
		
		
		ImageView playButton = (ImageView) findViewById(R.id.btn_play);
		
		//----------------- here for debugging purposes
		int widthPx = (int) this.getResources().getDisplayMetrics().widthPixels;
		int heightPx = (int) this.getResources().getDisplayMetrics().heightPixels;
		DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics(); 
		float widthDp =  (int) displayMetrics.widthPixels/ displayMetrics.density;
		float heightDp =  (int) displayMetrics.heightPixels/ displayMetrics.density;
		
		Log.d("widthPx: "+String.valueOf(widthPx),"heightPx: "+String.valueOf(heightPx));
		Log.d("widthDP: "+String.valueOf(widthDp),"heightDP: "+String.valueOf(heightDp)); 
		//--------------------------------------------------------
		pref = this.getSharedPreferences("ca.gsalisi.eggs", Context.MODE_PRIVATE);
		soundOn = pref.getBoolean("soundfx", true);
		
		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Handler h = new Handler();
				h.postDelayed(new Runnable(){

					

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Intent intent = new Intent(StartingActivity.this,
								MainActivity.class);
						startActivity(intent);
						StartingActivity.this.overridePendingTransition(0,0);
					}
				}, 100);
				
			}
		});
		
		final ImageButton btn_settings = (ImageButton) findViewById(R.id.btn_soundfx);
		if(soundOn){
			btn_settings.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
		}else{
			btn_settings.setImageResource(android.R.drawable.ic_lock_silent_mode);
		}
		
		btn_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(soundOn){
					soundOn = false;
					Editor editor = pref.edit();
					editor.putBoolean("soundfx", false);
					editor.commit();
					btn_settings.setImageResource(android.R.drawable.ic_lock_silent_mode);
					
				}else{
					soundOn = true;
					Editor editor = pref.edit();
					editor.putBoolean("soundfx", true);
					editor.commit();
					btn_settings.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
				}

			}

		});
		
		

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	

}
