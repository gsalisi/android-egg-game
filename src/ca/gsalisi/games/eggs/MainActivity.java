package ca.gsalisi.games.eggs;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {

	EggGame eggGame;
	protected RelativeLayout rLayout;
	protected ImageView eggView;
	// protected ImageView eggViewLeft;
	// protected ImageView eggViewCenter;
	// protected ImageView eggViewRight;
	protected ImageView chickenViewLeft;
	protected ImageView chickenViewCenter;
	protected ImageView chickenViewRight;
	protected ImageView basketView;

	protected boolean isPlaying;
	protected SensorManager sensorManager;
	protected SensorEventListener eventListener;
	protected Sensor rtnVectorSensor;
	protected TextView scoreView;
	protected int scoreCount;

	public TextView xView;
	public TextView yView;
	public TextView zView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rLayout = (RelativeLayout) findViewById(R.id.rLayout);

		isPlaying = true;// checks if the game is in session or not

		scoreView = (TextView) findViewById(R.id.eggtitle);

		initializeGameGraphics(); // initialize graphics for main view
		initSensors(); // initialize sensors
		eggGame = new EggGame(MainActivity.this);
		eggGame.startGame();

		final Button btn = (Button) findViewById(R.id.movebtn);
		btn.setText("Pause!");

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (!isPlaying) {
					eggGame = new EggGame(MainActivity.this);
					eggGame.startGame();
					isPlaying = true;
					btn.setText("Pause!");
				} else {
					eggGame.pauseGame();
					btn.setText("Play!");
					isPlaying = false;
				}

			}
		});

	}// end OnCreate

	protected void OnPause() {

		eggGame.pauseGame();

	}
	protected void OnStop() {

		eggGame.pauseGame();

	}

	// initialize rotation vector sensor
	protected void initSensors() {

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		rtnVectorSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		eventListener = new MySensorEventListener(this);
	}

	// create views
	protected void initializeGameGraphics() {

		// scoreCount = 0; //initialize score

		// ---- INITIALIZE CHICKENS --- //

		chickenViewLeft = (ImageView) findViewById(R.id.chickenLeft);
		chickenViewCenter = (ImageView) findViewById(R.id.chickenCenter);
		chickenViewRight = (ImageView) findViewById(R.id.chickenRight);

		//---- REMOVED INITIALIZE EGGS ----//
	

		// ----  create basket view ------//

		//removed position

		RelativeLayout.LayoutParams basketLayout = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		basketLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		basketLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
		basketLayout.setMargins(0, 0, 0, 20);
		basketLayout.height = convertToPixel(60);

		basketView = new ImageView(this);
		basketView.setImageResource(R.drawable.nest);
		basketView.setLayoutParams(basketLayout);
		rLayout.addView(basketView);

		// ---------------------------------------------------
		// bring chicken views to front
		chickenViewLeft.bringToFront();
		chickenViewCenter.bringToFront();
		chickenViewRight.bringToFront();

		// initialize x,y,z textViews
		xView = (TextView) findViewById(R.id.textView2);
		yView = (TextView) findViewById(R.id.textView3);
		zView = (TextView) findViewById(R.id.textView4);

	}// end initializeGraphics()

	public ImageView createEgg(int pos) {

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		switch (pos) {
		case 0:

			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			layoutParams.setMargins(0, 50, 0, 0);
			layoutParams.height = convertToPixel(48);

			break;
		case 1:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.setMargins(50, 50, 0, 0);
			layoutParams.height = convertToPixel(48);

			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, 50, 50, 0);
			layoutParams.height = convertToPixel(48);
			break;
		default:
			break;
		}

		eggView = new ImageView(this);
		eggView.setImageResource(R.drawable.egg_test);
		eggView.setLayoutParams(layoutParams);

		rLayout.addView(eggView);

		return eggView;
	}
	
	public int convertToPixel(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	public void moveBasket(String string, int i) {
		eggGame.moveBasket(string, i);

	}

	public int getBasketPosition() {
			
		return eggGame.getBasketPosition();
	}

}