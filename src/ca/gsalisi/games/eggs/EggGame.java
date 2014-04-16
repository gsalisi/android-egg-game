package ca.gsalisi.games.eggs;

import java.util.Random;
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
	
//	private SensorManager sensorManager;
//	private SensorEventListener eventListener;
//	private Sensor rtnVectorSensor;
	
	private AnimationListener animListener;
	private Animation eggAnimation;
	private int eggDelayTime;
	private boolean animationStarted;
	
	private Handler eggDelayHandler;
	private Handler levelHandler;
	private Handler eggIntervalHandler;
	
	private Runnable eggDelayRunnable;
	private Runnable levelRunnable;
	private Runnable eggIntervalRunnable;
	
	protected int level;
	protected int scoreCount;
	protected int numberOfLives;
	protected CountDownTimer countdownTimer;
	
	public boolean gameInSession;

	

	//Egg Game Constructor
	public EggGame(MainActivity mainActivity) {
	
		main = mainActivity;
		scoreCount = 0;
		numberOfLives = 3;

//		sensorManager = main.sensorManager;
//		eventListener = main.eventListener;
//		rtnVectorSensor = main.rtnVectorSensor;
		
	}//end of constructor

	//starts the game
	protected void startGame() {
		
		//signals that the game started but no timers yet
		gameInSession = true;
		
		//put the basket in center
		main.hScroll.post(new Runnable(){

			@Override
			public void run() {
				main.hScroll.smoothScrollTo(main.convertToPixel(120),0);	
			}
			
		});
		
		// creates a delay before the start of the game
		main.countdownView.setVisibility(View.VISIBLE);
		main.reset_btn.setEnabled(false);
		main.countdownView.bringToFront();
		//initialize the animation for flashing count down
		final Animation fadeOut = AnimationUtils.loadAnimation(main, R.anim.fadeout);
		
		countdownTimer = new CountDownTimer( 4000, 1000 ){

			@Override
			public void onFinish() {
				main.countdownView.setVisibility(View.GONE);
				if(gameInSession){
					initiateGameHandlers();
				}
			}

			@Override
			public void onTick(long num) {
				num /= 1000;
				main.countdownView.setText(String.valueOf(num));
				main.countdownView.startAnimation(fadeOut);	
			}
			
		};
		countdownTimer.start();
		

	}// end startGame
	

	private void initiateGameHandlers() {
		
		main.bringChickensToFront();
		//sensorManager.registerListener(eventListener, rtnVectorSensor, 50000);

		level = 0;
		eggDelayTime = 2500;

		levelHandler = new Handler();
		levelRunnable = new Runnable() {

			@Override
			public void run() {
			
				if (level != 0 && eggDelayTime >= 800) {
					eggDelayTime -= 100;
					level++;
					Log.d("LevelRunnable", "Increased Speed!");
				}
				if(level == 0){
						
					createEggFallHandler();
					level++;
					Log.d("LevelRunnable", "Created createEggHandler!");	
				}
				if(gameInSession){
					levelHandler.postDelayed(levelRunnable, 8000);
				}
			}
		};

		levelHandler.post(levelRunnable);
		
		//enable reset button when the handlers are running 
		main.reset_btn.setEnabled(true);
		
	}

	protected void createEggFallHandler() {

		eggIntervalHandler = new Handler();
		eggIntervalRunnable = new Runnable() {

			@Override
			public void run() {
				Log.d("EggInterval Handler", "Egg fall called!");
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
	protected int generateRandomPosition() {
		Random rand = new Random();
		return rand.nextInt(3); // 0 for left; 1 for center; 2 for right

	}// end generateRandomPosition

	//method that creates and animates the egg
	protected void startEggFall(int position) {
		//set to final so it's accessible inside runnable
		final int pos = position;
		
		//create a random delay form 0 to 800 milliseconds 
		//for every egg fall
		Random r = new Random();
		int delayEggFall = r.nextInt(800);
		
		Log.d("Delay Egg Fall", "Egg fall delay: " +String.valueOf(delayEggFall));
		eggDelayHandler = new Handler();
		eggDelayRunnable = new Runnable(){

			@Override
			public void run() {
				//creates the egg
				final ImageView eggView = main.createEgg(pos);
				
				//set falling animation
				eggAnimation = AnimationUtils.loadAnimation(main,
						R.anim.eggdrop);
				//set a random duration for egg fall ranging from 
				//1.6-2 seconds
				Random rand = new Random();
				int duration = rand.nextInt(400) + 1600;
				
				eggAnimation.setDuration(duration);
				
				eggView.startAnimation(eggAnimation);
				animationStarted = true;
				animListener = new AnimationListener() {

					@Override
					public void onAnimationEnd(Animation animation) {
						
						eggView.setVisibility(View.GONE);
						main.checkIfScored(pos);
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationStart(Animation animation) {
						//animate chicken here
					}

				};
				//set the listener
				eggAnimation.setAnimationListener(animListener);
						
			}
			
		};
		eggDelayHandler.postDelayed(eggDelayRunnable, delayEggFall);
			

	}// end startEggFall()

	//restarts the game
	public void resetGame() {
		Log.d("resetGame", "RESET");
		numberOfLives = 3;
		main.updateLives(numberOfLives);
		scoreCount = 0;
		main.updateScore(scoreCount);
		
		startGame();
		
	}//end resetGame();

	//stops the game
	public void stopGame() {
		Log.d("stopGame", "STOPPED");
		
		level = 0; //important! this prevents remaining task scheduled 
					//on master timer and level timer to continue 
		
		cancelTimers();
		
		if(animationStarted){
			eggAnimation.cancel();
			main.eggView.clearAnimation();
		}
		//sensorManager.unregisterListener(eventListener);
		gameInSession = false;
		animationStarted = false;
		
	}//end of stopGame()

	//cancels timers
	void cancelTimers() {
		Log.d("cancelTimer", "TIMER CANCELLED");
		
		eggDelayHandler.removeCallbacks(eggDelayRunnable);
		levelHandler.removeCallbacks(levelRunnable);
		eggIntervalHandler.removeCallbacks(eggIntervalRunnable);
	
	}//end of cancelTimers()
	
}
