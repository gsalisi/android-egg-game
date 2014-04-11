package ca.gsalisi.games.eggs;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class StartingActivity extends Activity {
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starting);

		ImageView playButton = (ImageView) findViewById(R.id.btn_play);
		
		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// animate ending first
				
				
				startActivity(new Intent(StartingActivity.this,
						MainActivity.class));
				StartingActivity.this.overridePendingTransition(0, 0);
			}
		});
		
		ImageButton btn_settings = (ImageButton) findViewById(R.id.btn_soundfx);
		
		
		btn_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// animate first
				// start settings activity

			}

		});
		
		

	}
	

}
