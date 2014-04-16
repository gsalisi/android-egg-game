package ca.gsalisi.games.eggs;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class EggGame {

	private MainActivity main;
	
//	private SensorManager sensorManager;
//	private SensorEventListener eventListener;
//	private Sensor rtnVectorSensor;
	
	private Timer eggDelayTimer;
	private Timer masterLvlTimer;
	private TimerTask eggTimerTask;
	private AnimationListener animListener;
	private Animation eggAnimation;
	private int eggDelayTime;
	private boolean animationStarted;
	private Handler eggDelayHandler;
	private Runnable eggRunnable;
	
	protected int level;
	protected int scoreCount;
	protected int numberOfLives;
	protected CountDownTimer countdownTimer;
	
	public boolean startedGame;
	public boolean timerRunning;
	

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
		startedGame = true;
		
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
				if(startedGame){
					initiateGameTimers();
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
	

	private void initiateGameTimers() {
		
		main.bringChickensToFront();
		//sensorManager.registerListener(eventListener, rtnVectorSensor, 50000);

		level = 0;
		eggDelayTime = 3000;

		final Handler handler = new Handler();
		TimerTask levelTimerTask = new TimerTask() {

			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if(timerRunning){
							if (level != 0 && eggDelayTime >= 800) {
								eggDelayTimer.cancel();
								eggDelayTimer.purge();
								eggDelayTime -= 100;
								Log.d("LevelTimerTask", "Cancelled existing eggtask!");
							}
							createEggFallTimer();
							Log.d("LevelTimerTask", "Created createEggtask!");
							eggDelayTimer.schedule(eggTimerTask, 1000, eggDelayTime);
							level++;
						}
					}
				});

			}

		};
		masterLvlTimer = new Timer();
		masterLvlTimer.schedule(levelTimerTask, 0, 10000);
		
		//enable reset button when the timers are running 
		main.reset_btn.setEnabled(true);
		timerRunning = true; 
	}

	protected void createEggFallTimer() {

		eggDelayTimer = new Timer();
		final Handler handler = new Handler();
		eggTimerTask = new TimerTask() {

			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if(timerRunning){
							Log.d("EggTimerTask", "Egg fall running!");
							int position = generateRandomPosition();
							startEggFall(position);
						}
					}
				});

			}

		};

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
		
		eggDelayHandler = new Handler();
		eggRunnable = new Runnable(){

			@Override
			public void run() {
				//creates the egg
				final ImageView eggView = main.createEgg(pos);
				
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
						if(timerRunning){
							eggView.setVisibility(View.GONE);
							main.checkIfScored(pos);
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
				eggAnimation.setAnimationListener(animListener);
						
			}
			
		};
		eggDelayHandler.postDelayed(eggRunnable, delayEggFall);
			

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
		startedGame = false;
		animationStarted = false;
		
	}//end of stopGame()

	//cancels timers
	void cancelTimers() {
		Log.d("cancelTimer", "TIMER CANCELLED");
		eggDelayHandler.removeCallbacks(eggRunnable);
		
		timerRunning = false;
		
		masterLvlTimer.cancel();
		eggDelayTimer.cancel();
		
		masterLvlTimer.purge();
		eggDelayTimer.purge();
	}//end of cancelTimers()
	
}
