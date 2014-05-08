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
	
	private GameGraphics gameGraphics;
	private SoundHandler soundHandler;
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
	final private static int EGG_DELAY_TIME_DECREMENT = 200;
	final private static int EGG_DELAY_TIME_DECREMENT_MED = 75;
	final private static int EGG_DELAY_TIME_DECREMENT_SMALL = 10;
	final private static int MAX_LEVEL = 50;
	final private static int TIME_PER_LEVEL = 10000;
	final private static int TIMING_RANDOMIZER = 150;
	final private static int DURATION_DEFAULT = 1800;
	final private static int DURATION_DECREMENT = 60;
	final private static int DURATION_DECREMENT_MED = 20;
	final private static int DURATION_DECREMENT_SMALL = 5;
	final private static String WHITE = "white";
	final private static String CRACKED = "cracked";
	final private static String GOLD = "gold";
	
	//Egg Game Constructor
	public EggGame(MainActivity mainActivity, GameGraphics gameGraphics, SoundHandler soundHandler) {
		this.main = mainActivity;
		this.soundHandler = soundHandler;
		this.gameGraphics = gameGraphics;
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
		gameGraphics.bringChickensToFront();
		gameGraphics.updateLevel(1);

		startCountdown();
		
		
	}// end startGame
	
	//count down before eggs starts falling
	private void startCountdown() {

		gameGraphics.getCountdownView().setVisibility(View.VISIBLE);
		main.reset_btn.setEnabled(false);
		gameGraphics.getCountdownView().bringToFront();
		
		//initialize the animation for flashing count down
		final Animation fadeOut = AnimationUtils.loadAnimation(main, R.anim.fadeout);
				
		countdown = 3;
		countdownHandler = new Handler();
		countdownRunnable = new Runnable(){

			@Override
			public void run() {
				if(countdown == 3 ){
					gameGraphics.getBasketScrollView().smoothScrollTo(gameGraphics.convertToPx(120),0);
				}
				if(countdown>0){
					soundHandler.playSoundEffect(5, 50);
					gameGraphics.getCountdownView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 300);
					gameGraphics.getCountdownView().setText(String.valueOf(countdown));
					gameGraphics.getCountdownView().startAnimation(fadeOut);
					countdownHandler.postDelayed(countdownRunnable, 1000);
					countdown--;
				}else{
					soundHandler.playSoundEffect(6, 50);
					gameGraphics.getCountdownView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 180);
					gameGraphics.getCountdownView().setText("Go!");
					gameGraphics.getCountdownView().startAnimation(fadeOut);
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
				gameGraphics.updateLevel(level+1);
				
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
				//get random egg position
				int position = generateRandomPosition();
				
				startEggFall(position);//start an egg fall
				
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
		
		//create a random delay for every egg fall
		Random r = new Random();
		int delayEggFall = r.nextInt(TIMING_RANDOMIZER);
		
//		Log.d("Delay Egg Fall", "Egg fall delay: " +String.valueOf(delayEggFall));
		eggDelayHandler = new Handler();
		eggDelayRunnable = new Runnable(){

			@Override
			public void run() {
				//shakes chicken
				gameGraphics.shakeChicken(pos);
				
				//randomize type of egg
				String color = getTypeOfEgg();
				
				//creates the egg
				final ImageView eggView = gameGraphics.createEgg(pos, color);
				if (color == GOLD){ 
					soundHandler.playSoundEffect(1, 50);
				}
				//egg animation
				eggAnimation = AnimationUtils.loadAnimation(main,
						R.anim.eggdrop);
				eggAnimation.setDuration(animDuration);
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
					checkIfScored(pos, 1);
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
					checkIfScored(pos, 3);
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
					checkIfScored(pos, -5);
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
	
	public void checkIfScored(int position, int scoreInc) {
		boolean caught = false;
		
		int xBasketPosition = gameGraphics.getBasketScrollView().getScrollX();
		int	widthReference = gameGraphics.getWidthReference();
		
		xBasketPosition = gameGraphics.convertToDp(xBasketPosition);
				
		int leftCond = widthReference - 47;
		int centerCondL = (widthReference / 2) + 48;
		int centerCondR = (widthReference / 2) - 48;
		int rightCond = 47;
		
		switch (position) {
		case 0:
			
			if(xBasketPosition > leftCond){
				caught = true;
			}
			
			break;
		case 1:
			if(xBasketPosition < centerCondL 
						&& xBasketPosition > centerCondR){
				caught = true;
			}
			break;
		case 2:
			if(xBasketPosition < rightCond){
				caught = true;
			}
			break;
		default:
			break;
		}
		
		if(caught){
			
			scoreCount += scoreInc;
			gameGraphics.updateScore(scoreCount);
			if(scoreInc == 1){
				soundHandler.playSoundEffect(3, 50);
			}else if(scoreInc == 3){
				soundHandler.playSoundEffect(4, 50);
			}else{
				soundHandler.playSoundEffect(0, 50);
			}
						
		}else{
			
			gameGraphics.showBrokenEgg(position, scoreInc);
			soundHandler.playSoundEffect(2, 50);
			
			if(scoreInc != -5){
				
				numberOfLives--;	
				if(numberOfLives <= 0){
					main.gameOver();
				}else{
					gameGraphics.updateLives(numberOfLives);
				}
			}

		}
		
	}//end of checkIfScored()

	//restarts the game
	public void resetGame() {
		Log.d("resetGame", "RESET");
		
		animationStarted = false;
		numberOfLives = 3;
		gameGraphics.updateLives(numberOfLives);
		scoreCount = 0;
		gameGraphics.updateScore(scoreCount);
		
		startGame();
		
	}//end resetGame();

	//stops the game
	public void stopGame() {
		Log.d("stopGame", "STOPPED");
		
		gameInSession = false;
		level = 0;
		
		cancelTimers();
		
		if(animationStarted){
			Log.d("stopGame","animation cleared!");
			eggAnimation.cancel();
			gameGraphics.getEggView().clearAnimation();
		}
		
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
