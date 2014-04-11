package ca.gsalisi.games.eggs;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity {

	EggGame eggGame;
	protected RelativeLayout rLayout;
	protected ImageView eggView;
	
	protected ImageView chickenViewLeft;
	protected ImageView chickenViewCenter;
	protected ImageView chickenViewRight;
	protected ImageView basketView;
	
	protected ImageView eggBrokenLeft;
	protected ImageView eggBrokenCenter;
	protected ImageView eggBrokenRight;
	
	public ImageButton reset_btn;

	protected boolean isPlaying;
	protected SensorManager sensorManager;
	protected SensorEventListener eventListener;
	protected Sensor rtnVectorSensor;
	protected TextView scoreView;
	private int bestScore;
	
	protected TextView livesView;
	protected TextView countdownView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rLayout = (RelativeLayout) findViewById(R.id.rLayout);
		isPlaying = true;// checks if the game is in session or not
		scoreView = (TextView) findViewById(R.id.score_view);
		reset_btn = (ImageButton) findViewById(R.id.btn_reset);

		initializeGameGraphics(); // initialize graphics for main view
		initSensors(); // initialize sensors
		eggGame = new EggGame(MainActivity.this);
		eggGame.startGame();

		reset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				eggGame.stopGame();
				eggGame.resetGame();
			}
		});

	}// end OnCreate

	protected void OnPause() {
		super.onPause();
		Log.d("GS", "On PAUSE");
		eggGame.stopGame();

	}
	protected void OnStop() {
		super.onPause();
		Log.d("GS", "On Stop");
		eggGame.stopGame();

	}

	// initialize rotation vector sensor
	protected void initSensors() {

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		rtnVectorSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
		eventListener = new MySensorEventListener(this);
	}

	// create views
	protected void initializeGameGraphics() {

		// scoreCount = 0; //initialize score

		// ---- INITIALIZE CHICKENS --- //

		chickenViewLeft = (ImageView) findViewById(R.id.chickenLeft);
		chickenViewCenter = (ImageView) findViewById(R.id.chickenCenter);
		chickenViewRight = (ImageView) findViewById(R.id.chickenRight);
		
		//lives
		livesView = (TextView) findViewById(R.id.lives_view);

		//---- Initialize broken eggs ----//
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.setMargins(convertToPixel(10), 0, 0, convertToPixel(13));
		layoutParams.height = convertToPixel(44);
		
		eggBrokenLeft = new ImageView(this);
		eggBrokenLeft.setImageResource(R.drawable.egg_broken);
		eggBrokenLeft.setLayoutParams(layoutParams);
		
		
		RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE);
		layoutParams1.setMargins(0, 0, 0, convertToPixel(13));
		layoutParams1.height = convertToPixel(44);
		
		eggBrokenCenter = new ImageView(this);
		eggBrokenCenter.setImageResource(R.drawable.egg_broken);
		eggBrokenCenter.setLayoutParams(layoutParams1);
	
		RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams2.setMargins(0, 0, convertToPixel(10), convertToPixel(13));
		layoutParams2.height = convertToPixel(44);

		eggBrokenRight = new ImageView(this);
		eggBrokenRight.setImageResource(R.drawable.egg_broken);
		eggBrokenRight.setLayoutParams(layoutParams2);
		
		rLayout.addView(eggBrokenLeft);
		rLayout.addView(eggBrokenCenter);
		rLayout.addView(eggBrokenRight);
	
		eggBrokenLeft.setVisibility(View.INVISIBLE);
		eggBrokenCenter.setVisibility(View.INVISIBLE);
		eggBrokenRight.setVisibility(View.INVISIBLE);

		// ----  create basket view ------//

		//removed position

		RelativeLayout.LayoutParams basketLayout = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		basketLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		basketLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
		basketLayout.width = convertToPixel(120);

		basketView = new ImageView(this);
		basketView.setImageResource(R.drawable.nest);
		basketView.setLayoutParams(basketLayout);
		rLayout.addView(basketView);

		// ---------------------------------------------------
		// bring chicken views to front
		chickenViewLeft.bringToFront();
		chickenViewCenter.bringToFront();
		chickenViewRight.bringToFront();

		countdownView = (TextView) findViewById(R.id.countdown_view);
	}// end initializeGraphics()

	public ImageView createEgg(int position) {

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		switch (position) {
		case 0:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.setMargins(convertToPixel(25), convertToPixel(25), 0, 0);
			layoutParams.height = convertToPixel(48);
			

			break;
		case 1:
		
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			layoutParams.setMargins(0, convertToPixel(25), 0, 0);
			layoutParams.height = convertToPixel(48);
			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, convertToPixel(25), convertToPixel(25), 0);
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
	public void showBrokenEgg(int position) {
		
		Animation eggFade = AnimationUtils.loadAnimation(this,
				R.anim.fadeout);

		switch (position) {
		case 0:
			eggBrokenLeft.setVisibility(View.VISIBLE);
			eggBrokenLeft.startAnimation(eggFade);
			break;
		case 1:
			eggBrokenCenter.setVisibility(View.VISIBLE);
			eggBrokenCenter.startAnimation(eggFade);
			break;
		case 2:
			eggBrokenRight.setVisibility(View.VISIBLE);
			eggBrokenRight.startAnimation(eggFade);
			break;
		default:
			break;
		}

		
		
	}
	
	public void gameOver() {
		
		eggGame.stopGame();
		
		final Dialog overDialog = new Dialog(MainActivity.this);
		overDialog.setContentView(R.layout.game_over);
		overDialog.setCancelable(false);
		overDialog.setCanceledOnTouchOutside(false);
		overDialog.setTitle("Game Over!");
		
		ImageButton restartbtn = (ImageButton) overDialog.findViewById(R.id.btn_gameover);
		restartbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				eggGame.resetGame();
				overDialog.dismiss();
			}
		});
		overDialog.show();
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

	public void updateLives(int numberOfLives) {
		livesView.setText(String.valueOf(numberOfLives));
		
	}
	
	public void updateScore(int score) {

		String countStr = "Score: " + String.valueOf(score);
		scoreView.setText(countStr);
	}

	

	

}