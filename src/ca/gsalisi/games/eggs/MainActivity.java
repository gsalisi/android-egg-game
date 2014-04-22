package ca.gsalisi.games.eggs;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EggGame eggGame;
	private RelativeLayout rLayout;
	private ImageView chickenViewLeft;
	private ImageView chickenViewCenter;
	private ImageView chickenViewRight;
	private ImageView basketView;
	private ImageView eggBrokenLeft;
	private ImageView eggBrokenCenter;
	private ImageView eggBrokenRight;
	private TextView livesView;
	private TextView bestScoreView;
	private SharedPreferences pref;
	private Dialog overDialog;
	
	protected ImageView eggView;
	protected MyScrollView hScroll;
	protected TextView scoreView;
	protected TextView countdownView;
	protected ImageButton reset_btn;
	
	private boolean gameOverBool;
	private int xBasketPosition;
	private int bestScore;
	private Typeface typeface;
	
	protected SoundPool soundPool;
	protected int[] soundIds = new int[5];
	private boolean soundsOn;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initializeGameGraphics(); // initialize graphics for main view
		initializeSoundFx();
		//initSensors(); // initialize sensors
		soundsOn = true;
		
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
		if(!gameOverBool){
			try{
				soundPool.autoPause();
				soundPool.release();
				eggGame.stopGame();
				Log.d("On Stop", "Called cancel timers");
			} catch(Exception e) {
				Log.d("On Stop", "exception caught");
			}
		}else{
			overDialog.dismiss();
		}
		finish();
		
		
	}//end onStop()


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
		
		typeface = Typeface.createFromAsset(this.getAssets(),
		        "fonts/roostheavy.ttf");
		
		rLayout = (RelativeLayout) findViewById(R.id.rLayout);
		
		// ---- create chicken references --- //
		chickenViewLeft = (ImageView) findViewById(R.id.chickenLeft);
		chickenViewCenter = (ImageView) findViewById(R.id.chickenCenter);
		chickenViewRight = (ImageView) findViewById(R.id.chickenRight);
				
		//---- Initialize broken eggs ----//
		//needs to create a function to make the code better and shorter!!
		RelativeLayout.LayoutParams layoutParams = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		layoutParams.setMargins(convertToPixel(10), 0, 0, convertToPixel(13));
		layoutParams.height = convertToPixel(44);
		
		eggBrokenLeft = new ImageView(this);
		eggBrokenLeft.setImageResource(R.drawable.egg_broken);
		eggBrokenLeft.setLayoutParams(layoutParams);
		
		
		RelativeLayout.LayoutParams layoutParams1 = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL,
				RelativeLayout.TRUE);
		layoutParams1.setMargins(0, 0, 0, convertToPixel(13));
		layoutParams1.height = convertToPixel(44);
		
		eggBrokenCenter = new ImageView(this);
		eggBrokenCenter.setImageResource(R.drawable.egg_broken);
		eggBrokenCenter.setLayoutParams(layoutParams1);
	
		RelativeLayout.LayoutParams layoutParams2 = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
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
		TextView livesLabel = (TextView) findViewById(R.id.lives_label);
		
		livesLabel.setTypeface(typeface);
		countdownView.setTypeface(typeface);
		livesView.setTypeface(typeface);
		scoreView.setTypeface(typeface);
		bestScoreView.setTypeface(typeface);
		
	
	}// end initializeGraphics()

	//set the chicken views to the front
	void bringChickensToFront() {

		chickenViewLeft.bringToFront();
		chickenViewCenter.bringToFront();
		chickenViewRight.bringToFront();

	}//end of chickensToFront()
	private void bringBasketToFront() {
		// TODO Auto-generated method stub
		hScroll.bringToFront();
	}
	//initialize sound effects
	private void initializeSoundFx() {

		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

		soundIds[0] = soundPool.load(this, R.raw.laying_hen_white, 1);
		soundIds[1] = soundPool.load(this, R.raw.laying_hen_gold, 1);
		soundIds[2] = soundPool.load(this, R.raw.egg_dropped, 1);
		soundIds[3] = soundPool.load(this, R.raw.score, 1);
		soundIds[4] = soundPool.load(this, R.raw.score_2, 1);
		
	}
	//creates a new egg view in the required position
	public ImageView createEgg(int position, String color) {
		
		RelativeLayout.LayoutParams layoutParams = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_TOP);
		

		switch (position) {//0 for left; 1 for center; 2 for right
		case 0:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.setMargins(convertToPixel(25), convertToPixel(60), 0, 0);
			layoutParams.height = convertToPixel(35);
			

			break;
		case 1:
		
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			layoutParams.setMargins(0, convertToPixel(60), 0, 0);
			layoutParams.height = convertToPixel(35);
			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, convertToPixel(60), convertToPixel(25), 0);
			layoutParams.height = convertToPixel(35);
			break;
		default:
			break;
		}

		eggView = new ImageView(this);
		if(color=="white"){
			eggView.setImageResource(R.drawable.egg_white);
			playSoundEffect(0, 50);
		}else{
			eggView.setImageResource(R.drawable.egg_gold);
			playSoundEffect(1, 50);
		}
		eggView.setLayoutParams(layoutParams);

		rLayout.addView(eggView);
		bringChickensToFront();
		bringBasketToFront();
		
		return eggView;
		
	}//end of createEgg()




	private LayoutParams getMyLayoutParams(int rule) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(rule);
		return layoutParams;
	}

	//check if basket is in the right position
	//if it is then we add score, if not we subtract from lives
	public void checkIfScored(int position, int scoreInc) {
		boolean caught = false;
		
		xBasketPosition = hScroll.getScrollX();
		
		switch (position) {
		case 0:
			
			if(xBasketPosition > convertToPixel(210)){
				caught = true;
			}
			
			break;
		case 1:
			if(xBasketPosition < convertToPixel(150) 
						&& xBasketPosition > convertToPixel(90)){
				caught = true;
			}
			break;
		case 2:
			if(xBasketPosition < convertToPixel(40)){
				caught = true;
			}
			break;
		default:
			break;
		}
		
		if(caught){
			eggGame.scoreCount += scoreInc;
			updateScore(eggGame.scoreCount);
			if(scoreInc == 1){
				playSoundEffect(3, 80);
			}else{
				playSoundEffect(4, 80);
			}
						
		}else{
			
			showBrokenEgg(position);
			playSoundEffect(2, 80);

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
				overDialog.dismiss();
				eggGame.resetGame();
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

	public void shakeChicken(int pos) {
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake);
		
		switch (pos) {
		case 0:
			chickenViewLeft.startAnimation(anim);
			break;
		case 1:
			chickenViewCenter.startAnimation(anim);
			break;
		case 2:
			chickenViewRight.startAnimation(anim);
			break;
		default:
			break;
		}
	}
	private void playSoundEffect(int id, int vol) {
		
		if(soundsOn){
			if(id == 2){
				soundPool.play(soundIds[id], vol, vol, 1, 0, (float) 0.7);
			}else{
				soundPool.play(soundIds[id], vol, vol, 1, 0, 1);
			}
		}
	}

	

}