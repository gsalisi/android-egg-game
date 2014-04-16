package ca.gsalisi.games.eggs;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EggGame eggGame;
	private TextView livesView;
	private TextView bestScoreView;
	private int bestScore;
	private SharedPreferences pref;
	private Dialog overDialog;
	private boolean gameOverBool;
	private int xBasketPosition;
	
	protected MyScrollView hScroll;
	protected RelativeLayout rLayout;
	protected ImageView eggView;
	protected ImageView chickenViewLeft;
	protected ImageView chickenViewCenter;
	protected ImageView chickenViewRight;
	protected ImageView basketView;
	protected ImageView eggBrokenLeft;
	protected ImageView eggBrokenCenter;
	protected ImageView eggBrokenRight;
	protected SensorManager sensorManager;
	protected SensorEventListener eventListener;
	protected Sensor rtnVectorSensor;
	protected TextView scoreView;
	protected TextView countdownView;
	
	public ImageButton reset_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initializeGameGraphics(); // initialize graphics for main view
		//initSensors(); // initialize sensors
		
		//get existing high score
		pref = this.getSharedPreferences("gsalisiBest", Context.MODE_PRIVATE);
		bestScore = pref.getInt("best", 0);
		updateBest(); //update high score view
		
		gameOverBool = false; //signals the game is not in the game over state
		
		eggGame = new EggGame(MainActivity.this); //instantiate game class
		eggGame.startGame();//starts the game

		//reset button
		reset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				eggGame.stopGame();
				eggGame.resetGame();
				gameOverBool = false;
			}
		});

	}// end OnCreate
	
	//cancel timers when the window is closed or when 
	//back button or home button is pressed to prevent
	//eggs falling even when out of game
	@Override
	protected void onStop() {
		super.onStop();
		try{
			eggGame.cancelTimers();
			Log.d("On Stop", "Called cancel timers");
		} catch(Exception e) {
			Log.d("On Stop", "exception caught");
		}
		eggGame.gameInSession = false;

		
		
	}//end onStop()

	//restart a game when game is reopened
	@Override
	protected void onResume(){
		super.onResume();
		Log.d("GS", "Resume");
		
		//checks if game is at Game over phase
		if(gameOverBool){
			overDialog.dismiss();
		}
		eggGame.resetGame();
		
	}//end onResume()

	// initialize rotation vector sensor -- saved here for future references 
	// if i decide to implement two control methods as an option for players
	
//	protected void initSensors() {
//
//		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//		rtnVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
//		eventListener = new MySensorEventListener(this);
//	}
	
	// create views
	protected void initializeGameGraphics() {

		rLayout = (RelativeLayout) findViewById(R.id.rLayout);
		
		// ---- create chicken references --- //

		chickenViewLeft = (ImageView) findViewById(R.id.chickenLeft);
		chickenViewCenter = (ImageView) findViewById(R.id.chickenCenter);
		chickenViewRight = (ImageView) findViewById(R.id.chickenRight);
				
		//---- Initialize broken eggs ----//
		//needs to create a function to make the code better and shorter!!
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

		// ----------------  create basket view -------------------//

		hScroll = (MyScrollView) findViewById(R.id.hScrollView);
		basketView = (ImageView) findViewById(R.id.basketView);
		
		basketView.getLayoutParams().width = getDeviceWidth()*2 - convertToPixel(170);
		
		//-------------- create view references -----------------//

		livesView = (TextView) findViewById(R.id.lives_view);
		countdownView = (TextView) findViewById(R.id.countdown_view);
		scoreView = (TextView) findViewById(R.id.score_view);
		reset_btn = (ImageButton) findViewById(R.id.btn_reset);
		bestScoreView = (TextView) findViewById(R.id.best_view);
	
	}// end initializeGraphics()

	//set the chicken views to the front
	void bringChickensToFront() {

		chickenViewLeft.bringToFront();
		chickenViewCenter.bringToFront();
		chickenViewRight.bringToFront();

	}//end of chickensToFront()

	//creates a new egg view in the required position
	public ImageView createEgg(int position) {
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		switch (position) {//0 for left; 1 for center; 2 for right
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
		
	}//end of createEgg()
	
	//check if basket is in the right position
	//if it is then we add score, if not we subtract from lives
	public void checkIfScored(int position) {
		boolean caught = false;
		
		xBasketPosition = hScroll.getScrollX();
		
		switch (position) {
		case 0:
			
			if(xBasketPosition > convertToPixel(220)){
				caught = true;
			}
			
			break;
		case 1:
			if(xBasketPosition < convertToPixel(140) 
						&& xBasketPosition > convertToPixel(100)){
				caught = true;
			}
			break;
		case 2:
			if(xBasketPosition < convertToPixel(30)){
				caught = true;
			}
			break;
		default:
			break;
		}
		
		if(caught){
			eggGame.scoreCount += 1;
			updateScore(eggGame.scoreCount);
			
		}else{
			showBrokenEgg(position);
			eggGame.numberOfLives--;
			
			if(eggGame.numberOfLives <= 0){
				gameOver();
			}else{
				updateLives(eggGame.numberOfLives);
			}
		}
		
	}//end of checkIfScored()
	
	public void showBrokenEgg(int position) {
		
		//reveal broken egg view and then fade it out
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
		
	}// end of showBrokenEgg()
	
	//show game over dialog
	public void gameOver() {
		
		gameOverBool = true;
		
		if(eggGame.scoreCount > bestScore){//save high score if it is beaten
			bestScore = eggGame.scoreCount;
			pref = this.getSharedPreferences("gsalisiBest", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putInt("best", bestScore);
			editor.commit();
			updateBest();
		}
		eggGame.stopGame();
		
		//creates dialog
		overDialog = new Dialog(MainActivity.this);
		overDialog.setContentView(R.layout.game_over);
		overDialog.setCancelable(false);
		overDialog.setCanceledOnTouchOutside(false);
		overDialog.setTitle("Game Over!");
		
		TextView scoreOver = (TextView) overDialog.findViewById(R.id.score_view2);
		TextView bestOver = (TextView) overDialog.findViewById(R.id.best_view2);
		
		scoreOver.setText(scoreView.getText().toString());
		bestOver.setText(bestScoreView.getText().toString());
		
		ImageButton restartbtn = (ImageButton) overDialog.findViewById(R.id.btn_gameover);
		restartbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				eggGame.resetGame();
				overDialog.dismiss();
				gameOverBool = false;
			}
		});
		overDialog.show();
		
	}//end of gameOver()
	

	// updates lives text view
	public void updateLives(int numberOfLives) {
		livesView.setText(String.valueOf(numberOfLives));
		
	}
	//updates best score view
	private void updateBest() {
		bestScoreView.setText("Best: " + bestScore);
		
	}

	// updates score text view
	public void updateScore(int score) {
		String countStr = "Score: " + String.valueOf(score);
		scoreView.setText(countStr);
	}
	// converts dp to pixels.
	public int convertToPixel(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	public int convertToDp(int px){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
				getResources().getDisplayMetrics());
	}
	public int getDeviceWidth(){
		return (int) this.getResources().getDisplayMetrics().widthPixels;  // displayMetrics.density;
	}
	public int getDeviceHeight(){
		return (int) this.getResources().getDisplayMetrics().heightPixels;
	}


	

}