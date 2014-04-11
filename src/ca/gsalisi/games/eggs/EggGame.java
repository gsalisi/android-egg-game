package ca.gsalisi.games.eggs;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
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
import android.widget.TextView;

public class EggGame {

	MainActivity main;
	protected SensorManager sensorManager;
	protected SensorEventListener eventListener;
	protected Sensor rtnVectorSensor;
	protected Timer eggDelayTimer;
	protected Timer masterLvlTimer;
	protected TimerTask eggTimerTask;
	protected AnimationListener animListener;


	protected ImageView basketView;
	
	protected Animation eggAnimation;
	protected TextView scoreView;
	protected int level;
	protected int scoreCount;
	protected int xBasketPosition;
	protected int rightMargin;
	protected int leftMargin;
	protected int eggDelayTime;
	private int rightBound = 118;
	private int leftBound = -118;
	protected int numberOfLives;
	private boolean running;
	

	public EggGame(MainActivity mainActivity) {
		// constructor
		main = mainActivity;
		sensorManager = main.sensorManager;
		eventListener = main.eventListener;
		rtnVectorSensor = main.rtnVectorSensor;
		basketView = main.basketView;
		scoreView = main.scoreView;
		scoreCount = 0;
		rightMargin = main.convertToPixel(60) * (-1);
		leftMargin = main.convertToPixel(60) * (-1); 
		xBasketPosition = 0;
		numberOfLives = 3;

	}

	protected void startGame() {
		// creates a delay for every egg drop
		main.countdownView.setVisibility(View.VISIBLE);
		main.reset_btn.setEnabled(false);
		main.countdownView.bringToFront();
		
		final Animation fadeOut = AnimationUtils.loadAnimation(main, R.anim.fadeout);
		new CountDownTimer( 4000, 1000 ){

			@Override
			public void onFinish() {
				//main.countdownView.setText("0");
				main.countdownView.setVisibility(View.GONE);
				initiateGameTimers();
				
			}

			@Override
			public void onTick(long num) {
				num /= 1000;
				int timeLeft = (int) num;

				main.countdownView.setText(String.valueOf(timeLeft));
				main.countdownView.startAnimation(fadeOut);
				
			}
			
		}.start();
		
		

	}// end startGame
	

	private void initiateGameTimers() {
		// TODO Auto-generated method stub
		running = true; 
		
		sensorManager.registerListener(eventListener, rtnVectorSensor, 50000);

		level = 0;
		eggDelayTime = 4000;

		final Handler handler = new Handler();
		TimerTask levelTimerTask = new TimerTask() {

			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if(running){
							if (level != 0 && eggDelayTime >= 500) {
								eggDelayTimer.cancel();
								eggDelayTime -= 250;
							}
							createEggFallTimer();
							eggDelayTimer.schedule(eggTimerTask, 400, eggDelayTime);
							level += 1;
						}
					}
				});

			}

		};
		masterLvlTimer = new Timer();
		masterLvlTimer.schedule(levelTimerTask, 200, 15000);
		
		main.reset_btn.setEnabled(true);
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
						if(running){
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

	protected void startEggFall(int position) {

		final int pos = position;

		final ImageView eggView = main.createEgg(pos);
		
		eggAnimation = AnimationUtils.loadAnimation(main,
				R.anim.eggdrop);

		eggView.startAnimation(eggAnimation);
		
		animListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				if(running){
					eggView.setVisibility(View.GONE);
					checkIfScored(pos);
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		};
		eggAnimation.setAnimationListener(animListener);

	}// end startEggFall()

	protected void checkIfScored(int position) {
		
		boolean caught = false;

		switch (position) {
		case 0:

			if(xBasketPosition < main.convertToPixel(-90)){
				caught = true;
			}
			
			break;
		case 1:
			if(xBasketPosition < main.convertToPixel(30) 
						&& xBasketPosition > main.convertToPixel(-30)){
				caught = true;
			}
			break;
		case 2:
			if(xBasketPosition > main.convertToPixel(90)){
				caught = true;
			}
			break;
		default:
			break;
		}
		
		if(caught){
			scoreCount += 1;
			main.updateScore(scoreCount);
			
		}else{
			main.showBrokenEgg(position);
			numberOfLives--;
			
			if(numberOfLives <= 0){
				main.gameOver();
			}else{
				main.updateLives(numberOfLives);
			}
		}
		
	}//end check if scored

	public void moveBasket(String direction, int incrementValue) {

		final int unitInc = main.convertToPixel(incrementValue);
		RelativeLayout.LayoutParams basketLayout = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		basketLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		basketLayout.width = main.convertToPixel(120);
	

		if (direction.equals("right")) {
			if (xBasketPosition <= main.convertToPixel(rightBound)) {
				if (xBasketPosition == 0 || xBasketPosition > 0) {
					// 128dp is set to compensate on how big the nest is 
					// see layout parameter rule!
					rightMargin = main.convertToPixel(-60);
					leftMargin += unitInc;
					basketLayout.addRule(RelativeLayout.RIGHT_OF,
							R.id.centerRef);
					basketLayout.setMargins(leftMargin, 0, 0, 0);

				} else {
					rightMargin -= unitInc;
					basketLayout.addRule(RelativeLayout.LEFT_OF,
							R.id.centerRef);
					basketLayout.setMargins(0, 0, rightMargin, 0);
				}

				xBasketPosition += unitInc;
				basketView.setLayoutParams(basketLayout);
			}
		} else {

			if (xBasketPosition >= main.convertToPixel(leftBound)) {

				if (xBasketPosition == 0 || xBasketPosition > 0) {
					leftMargin -= unitInc;
					basketLayout.addRule(RelativeLayout.RIGHT_OF,
							R.id.centerRef);
					basketLayout.setMargins(leftMargin, 0, 0, 0);
				} else {
					// 128dp is set to compensate on how big the chicken center is 
					// see layout parameter rule!
					leftMargin = main.convertToPixel(-60);
					rightMargin += unitInc;
					basketLayout.addRule(RelativeLayout.LEFT_OF,
							R.id.centerRef);
					basketLayout.setMargins(0, 0, rightMargin, 0);
				}
				xBasketPosition -= unitInc;
				basketView.setLayoutParams(basketLayout);
			}
		}

	}// end of moveBasket()

	public int getBasketPosition() {
		return xBasketPosition;
	}

	public void resetGame() {
		
		numberOfLives = 3;
		main.updateLives(numberOfLives);
		scoreCount = 0;
		main.updateScore(scoreCount);
		
		startGame();
	}

	public void stopGame() {
		
		running = false;
		
		masterLvlTimer.cancel();
		eggDelayTimer.cancel();
		
		masterLvlTimer.purge();
		eggDelayTimer.purge();
		
		eggAnimation.cancel();
		main.eggView.clearAnimation();
		
		sensorManager.unregisterListener(eventListener);
	}
	
}
