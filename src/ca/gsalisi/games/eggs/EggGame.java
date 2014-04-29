package ca.gsalisi.games.eggs;

import java.util.Random;

import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

public class EggGame {

	private MainActivity main;
	private AnimationListener animGoldListener;
	private AnimationListener animWhiteListener;
	private Animation eggAnimation;
	private int eggDelayTime;
	private boolean animationStarted;
	
	private Handler eggDelayHandler;
	private Handler levelHandler;
	private Handler eggIntervalHandler;
	private Handler countdownHandler;
	
	private int changeCount;
	private int prevPosition;
	
	private Runnable eggDelayRunnable;
	private Runnable levelRunnable;
	private Runnable eggIntervalRunnable;
	private Runnable countdownRunnable;
	
	protected int level;
	protected int scoreCount;
	protected int numberOfLives;
	protected CountDownTimer countdownTimer;
	
	public boolean gameInSession;
	public boolean handlerStarted;
	private int countdown;
	private AnimationListener animBadListener;
	private int animDuration;
	
	final private static int NULL = 0;
	final private static int DEFAULT_NUMBER_OF_LIVES = 3;
	final private static int NO_PREVIOUS_POSITION=-1;
	final private static int LEFT = 0;
	final private static int CENTER = 1;
	final private static int RIGHT = 2;
	final private static int EGG_DELAY_TIME_DEFAULT = 1300;
	final private static int EGG_DELAY_TIME_DECREMENT = 175;
	final private static int EGG_DELAY_TIME_DECREMENT_MED = 100;
	final private static int EGG_DELAY_TIME_DECREMENT_SMALL = 25;
	final private static int MAX_LEVEL = 50;
	final private static int TIME_PER_LEVEL = 10000;
	final private static int TIMING_RANDOMIZER = 150;
	final private static int DURATION_DEFAULT = 2000;
	final private static int DURATION_DECREMENT = 60;
	final private static int DURATION_DECREMENT_MED = 20;
	final private static int DURATION_DECREMENT_SMALL = 5;
	final private static String WHITE = "white";
	final private static String CRACKED = "cracked";
	final private static String GOLD = "gold";
	
	//Egg Game Constructor
	public EggGame(MainActivity mainActivity) {
	
		main = mainActivity;
		scoreCount = NULL;
		numberOfLives = DEFAULT_NUMBER_OF_LIVES;
		handlerStarted = false;
		gameInSession = false;

		
	}//end of constructor

	//starts the game
	protected void startGame() {
		
		//signals that the game started but no timers yet
		gameInSession = true;
		
		//initiate game variables
		prevPosition = NO_PREVIOUS_POSITION;
		changeCount = NULL;
		level = NULL;
		eggDelayTime = EGG_DELAY_TIME_DEFAULT;
		animDuration = DURATION_DEFAULT;
		main.bringChickensToFront();
		main.updateLevel(1);
		
		startCountdown();
		
		
	}// end startGame
	
	private void startCountdown() {
		// TODO Auto-generated method stub
		// creates a delay before the start of the game
		main.countdownView.setVisibility(View.VISIBLE);
		main.reset_btn.setEnabled(false);
		main.countdownView.bringToFront();
		//initialize the animation for flashing count down
		final Animation fadeOut = AnimationUtils.loadAnimation(main, R.anim.fadeout);
				
		countdown = 3;
		countdownHandler = new Handler();
		countdownRunnable = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(countdown == 3 ){
					main.hScroll.smoothScrollTo(main.convertToPixel(120),0);
				}
				if(countdown>0){
					main.playSoundEffect(5, 100);
					main.countdownView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 300);
					main.countdownView.setText(String.valueOf(countdown));
					main.countdownView.startAnimation(fadeOut);
					countdownHandler.postDelayed(countdownRunnable, 1000);
					countdown--;
				}else{
					main.playSoundEffect(6, 100);
					main.countdownView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 180);
					main.countdownView.setText("Go!");
					main.countdownView.startAnimation(fadeOut);
					if(gameInSession){
						initiateGameHandlers();
						countdownHandler.removeCallbacks(countdownRunnable);
					}
				}
			}
		};
				
		countdownHandler.postDelayed(countdownRunnable, 300);
	}

	//initiate game handlers
	private void initiateGameHandlers() {
		
		levelHandler = new Handler();
		levelRunnable = new Runnable() {

			@Override
			public void run() {
				
				decrementEggDelayTime();
				decrementAnimDuration();
				main.updateLevel(level+1);
				
				if(level == NULL){	
					createEggFallHandler();	
				}
				if(level <= MAX_LEVEL ){
					level++;
				}
				if(gameInSession){
					levelHandler.postDelayed(levelRunnable, TIME_PER_LEVEL);
				}
			}
			// EGG ANIMATION DURATION CONTROLLER
			private void decrementAnimDuration() {
				
				Random rand = new Random();
				if( level % 2 == 0 && level < 20){
					if( level < 7 ){
						animDuration -= (DURATION_DECREMENT + rand.nextInt(TIMING_RANDOMIZER));
					}else if( level < 15 ){
						animDuration -= (DURATION_DECREMENT_MED + rand.nextInt(TIMING_RANDOMIZER));
					}else{
						animDuration -= (DURATION_DECREMENT_SMALL + rand.nextInt(TIMING_RANDOMIZER));
					}
				}
			}
			// EGG DELAYS CONTROLLER
			private void decrementEggDelayTime() {
				
				if( level % 2 == 1 && eggDelayTime >= 300){
					
					if (level < 7) {
						eggDelayTime -= EGG_DELAY_TIME_DECREMENT;
					}else if( level < 15 ){
						eggDelayTime -= EGG_DELAY_TIME_DECREMENT_MED;
					}else{
						eggDelayTime -= EGG_DELAY_TIME_DECREMENT_SMALL;
					}
				}
			}
		};

		levelHandler.post(levelRunnable);
		
		//enable reset button when the handlers are running 
		main.reset_btn.setEnabled(true);
		handlerStarted = true;
	}

	// handler for individual egg fall event
	protected void createEggFallHandler() {

		eggIntervalHandler = new Handler();
		eggIntervalRunnable = new Runnable() {

			@Override
			public void run() {
				//generate random position
				int position = generateRandomPosition();
				//start the egg fall
				startEggFall(position);
				if(gameInSession){
					//set delay time of every egg fall
					eggIntervalHandler.postDelayed(eggIntervalRunnable, eggDelayTime);
				}
			}
		};
		if(gameInSession){
			eggIntervalHandler.post(eggIntervalRunnable);
		}
		

	}// end startEggFallTimer

	// Generates a random position for the egg fall
	// 0 for left; 1 for center; 2 for right
	protected int generateRandomPosition() {
		
		Random rand = new Random();
		int pos = rand.nextInt(3);
		//conditions prevent same position > 3 times
		if(prevPosition == pos){
			changeCount++;
		}else{
			changeCount = NULL;
		}
		if(changeCount == 3){
			
			if(pos == RIGHT){
				pos = rand.nextInt(1);
			}else if(pos == CENTER){
				pos = (rand.nextFloat() > 0.5) ? RIGHT : LEFT;
			}else{
				pos = (rand.nextFloat() > 0.5) ? CENTER : RIGHT;
			}
			changeCount = NULL;
			
		}
		prevPosition = pos;
		return pos;
				
	

	}// end generateRandomPosition

	//method that creates and animates the egg
	protected void startEggFall(int position) {
		
		//set to final so it's accessible inside runnable
		final int pos = position;
		
		//create a random delay form 0 to 200 milliseconds 
		//for every egg fall
		Random r = new Random();
		int delayEggFall = r.nextInt(TIMING_RANDOMIZER);
		
//		Log.d("Delay Egg Fall", "Egg fall delay: " +String.valueOf(delayEggFall));
		eggDelayHandler = new Handler();
		eggDelayRunnable = new Runnable(){

			@Override
			public void run() {
				//shakes chicken
				main.shakeChicken(pos);
				
				//randomize type of egg
				String color = getTypeOfEgg();
				
				//creates the egg
				final ImageView eggView = main.createEgg(pos, color);
				
				//set falling animation
				eggAnimation = AnimationUtils.loadAnimation(main,
						R.anim.eggdrop);
				//set animation duration
				eggAnimation.setDuration(animDuration);
				
				//start animation
				eggView.startAnimation(eggAnimation);
				animationStarted = true;
				
				//set Listener
				setMyAnimListener(eggAnimation, pos, eggView, color);
							
			}

		};
		eggDelayHandler.postDelayed(eggDelayRunnable, delayEggFall);

	}// end startEggFall()
	
	private String getTypeOfEgg() {
		
		Random rand = new Random();
		Float randF = rand.nextFloat();
		String color;
		
		if(level <= 5){
		
			color = WHITE;
		
		}else if( level > 5 && level <= 10){
			
			if(randF >= 0.9){
				color = GOLD;
			}else{
				color = WHITE;
			}
			return color;
		
		}else{
			
			if(randF >= 0.9){
				color = GOLD;
			}else if(randF<0.9 && randF>=0.8){
				color = CRACKED;
			}else{
				color = WHITE;
			}
			
		}
		
		return color;
	}

	protected void setMyAnimListener(Animation eggAnimation2, final int pos, 
									final ImageView eggView,  String color) {
		
		animWhiteListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				if(handlerStarted){//condition prevents showing of broken egg
					eggView.setVisibility(View.GONE);
					main.checkIfScored(pos, 1);
					animationStarted = false;
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		};
		//set the listener
		animGoldListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				if(handlerStarted){//condition prevents showing of broken egg
					eggView.setVisibility(View.GONE);
					main.checkIfScored(pos, 3);
					animationStarted = false;
				}
			}
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
		};
		animBadListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation arg0) {
				if(handlerStarted){//condition prevents showing of broken egg
					eggView.setVisibility(View.GONE);
					main.checkIfScored(pos, -5);
					animationStarted = false;
				}
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			
		};
		if(color.equals("white")){
			eggAnimation.setAnimationListener(animWhiteListener);
		}else if(color.equals("gold")){
			eggAnimation.setAnimationListener(animGoldListener);
		}else{
			eggAnimation.setAnimationListener(animBadListener);
		}
	}

	//restarts the game
	public void resetGame() {
		Log.d("resetGame", "RESET");
		
		animationStarted = false;
		numberOfLives = 3;
		main.updateLives(numberOfLives);
		scoreCount = 0;
		main.updateScore(scoreCount);
		
		startGame();
		
	}//end resetGame();

	//stops the game
	public void stopGame() {
		Log.d("stopGame", "STOPPED");
		
		gameInSession = false;
		level = 0; //important! this prevents remaining task scheduled 
					//on handlers to continue 
		
		cancelTimers();
		
		if(animationStarted){
			Log.d("stopGame","animation cleared!");
			eggAnimation.cancel();
			main.eggView.clearAnimation();
		}
		//sensorManager.unregisterListener(eventListener);
		
		
	}//end of stopGame()

	//cancels timers
	void cancelTimers() {
		Log.d("cancelTimer", "TIMER CANCELLED");
		
		eggDelayHandler.removeCallbacks(eggDelayRunnable);
		levelHandler.removeCallbacks(levelRunnable);
		eggIntervalHandler.removeCallbacks(eggIntervalRunnable);
		countdownHandler.removeCallbacks(countdownRunnable);
		handlerStarted = false;
	
	}//end of cancelTimers()
	
}
