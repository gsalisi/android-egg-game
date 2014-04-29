package ca.gsalisi.games.eggs;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.TextView;

public class StartingActivity extends Activity {
	 
	
	private boolean soundOn;
	private SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starting);
		Log.d("GS", "On first activity");
		Typeface typeface = Typeface.createFromAsset(getAssets(),
		        "fonts/roostheavy.ttf");
		((TextView) findViewById(R.id.titleView)).setTypeface(typeface);
		Animation swayingAnim = AnimationUtils.loadAnimation(this, R.anim.sway_main);

		ImageView playButton = (ImageView) findViewById(R.id.btn_play);
		playButton.startAnimation(swayingAnim);
		
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
