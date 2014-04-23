package ca.gsalisi.games.eggs;

import java.util.Random;

import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

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


	//Egg Game Constructor
	public EggGame(MainActivity mainActivity) {
	
		main = mainActivity;
		scoreCount = 0;
		numberOfLives = 3;
		handlerStarted = false;
		gameInSession = false;

		
	}//end of constructor

	//starts the game
	protected void startGame() {
		
		//signals that the game started but no timers yet
		gameInSession = true;
		prevPosition = -1; //initiate variables
		changeCount = 0;
		
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
					main.countdownView.setTextSize(300);
					main.countdownView.setText(String.valueOf(countdown));
					main.countdownView.startAnimation(fadeOut);
					countdownHandler.postDelayed(countdownRunnable, 1000);
					countdown--;
				}else{
					main.playSoundEffect(6, 100);
					main.countdownView.setTextSize(180);
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
		
		
	}// end startGame
	
	//initiate game handlers
	private void initiateGameHandlers() {
		
		main.bringChickensToFront();
		//sensorManager.registerListener(eventListener, rtnVectorSensor, 50000);

		level = 0;
		eggDelayTime = 1400;

		levelHandler = new Handler();
		levelRunnable = new Runnable() {

			@Override
			public void run() {
			
				if (level != 0 && eggDelayTime >= 300) {
					main.updateLevel(level/2);
					eggDelayTime -= 100;
				}
				if(level == 0){	
					createEggFallHandler();	
				}
				if(level <= 50){
					level++;
				}
				if(gameInSession){
					levelHandler.postDelayed(levelRunnable, 10000);
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
//				Log.d("EggInterval Handler", "Egg fall called!");
				int position = generateRandomPosition();
				startEggFall(position);
				if(gameInSession){
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
		
		if(prevPosition == pos){
			changeCount++;
		}else{
			changeCount = 0;
		}
		if(changeCount == 3){
			if(pos == 2){
				pos = rand.nextInt(1);
			}else if(pos == 1){
				pos = (rand.nextFloat() > 0.5) ? 2 : 0;
			}else{
				pos = (rand.nextFloat() > 0.5) ? 1 : 2;
			}
			
			changeCount = 0;
			
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
		int delayEggFall = r.nextInt(200);
		
//		Log.d("Delay Egg Fall", "Egg fall delay: " +String.valueOf(delayEggFall));
		eggDelayHandler = new Handler();
		eggDelayRunnable = new Runnable(){

			@Override
			public void run() {
				//shakes chicken
				main.shakeChicken(pos);
				
				//creates a random object
				Random rand = new Random();
				Float randF = rand.nextFloat();
				String color = randF > 0.9 ? "gold":"white";
				color = (0.9 > randF && randF > 0.8) ? "black":"white";
				//creates the egg
				final ImageView eggView = main.createEgg(pos, color);
				
				//set falling animation
				eggAnimation = AnimationUtils.loadAnimation(main,
						R.anim.eggdrop);
				//set a random duration for egg fall ranging from 1.6-2 seconds
				int duration;
				
				duration = rand.nextInt(150) + 2700 - (level * 80);

				eggAnimation.setDuration(duration);
				
				//start animation
				eggView.startAnimation(eggAnimation);
				animationStarted = true;
				
				//set Listener
				setMyAnimListener(eggAnimation, pos, eggView, color);
				
						
			}
			
		};
		eggDelayHandler.postDelayed(eggDelayRunnable, delayEggFall);
			

	}// end startEggFall()

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
					main.checkIfScored(pos, 2);
					animationStarted = false;
				}
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {

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
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationStart(Animation arg0) {

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
