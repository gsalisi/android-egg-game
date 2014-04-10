package ca.gsalisi.games.eggs;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starting);

		Button btn_play = (Button) findViewById(R.id.btn_play);
		Button btn_settings = (Button) findViewById(R.id.btn_settings);

		btn_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// animate ending first
				startActivity(new Intent(StartingActivity.this,
						MainActivity.class));
				StartingActivity.this.overridePendingTransition(0, 0);
			}
		});

		btn_settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// animate first
				// start settings activity

			}

		});

	}

}
