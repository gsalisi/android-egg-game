package ca.gsalisi.games.eggs;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
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

	protected ImageView eggViewLeft;
	protected ImageView eggViewCenter;
	protected ImageView eggViewRight;
	protected ImageView chickenViewLeft;
	protected ImageView chickenViewCenter;
	protected ImageView chickenViewRight;
	protected ImageView basketView;

	protected TextView scoreView;
	protected int level;
	protected int scoreCount;
	protected int xBasketPosition;
	protected int rightMargin;
	protected int leftMargin;
	protected int eggDelayTime;
	private int rightBound = 186;
	private int leftBound = -186;

	public EggGame(MainActivity mainActivity) {
		// constructor
		main = mainActivity;
		sensorManager = main.sensorManager;
		eventListener = main.eventListener;
		rtnVectorSensor = main.rtnVectorSensor;
		chickenViewLeft = main.chickenViewLeft;
		chickenViewCenter = main.chickenViewCenter;
		chickenViewRight = main.chickenViewRight;
		basketView = main.basketView;
		scoreView = main.scoreView;
		scoreCount = 0;
		rightMargin = main.convertToPixel(128) * (-1); // how much margin it
														// needs from center to
														// left
		leftMargin = main.convertToPixel(128) * (-1); // how much margin it
														// needs from center to
														// right
		xBasketPosition = 0;

	}

	protected void startGame() {
		// creates a delay for every egg drop

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
						if (level != 0 && eggDelayTime >= 500) {
							eggDelayTimer.cancel();
							eggDelayTime -= 250;
						}
						createEggFallTimer();
						eggDelayTimer.schedule(eggTimerTask, 1000, eggDelayTime);
						level += 1;
					}
				});

			}

		};
		masterLvlTimer = new Timer();
		masterLvlTimer.schedule(levelTimerTask, 200, 15000);

	}// end startGame

	protected void createEggFallTimer() {

		eggDelayTimer = new Timer();
		final Handler handler = new Handler();
		eggTimerTask = new TimerTask() {

			@Override
			public void run() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						int position = generateRandomPosition();
						startEggFall(position);
					}
				});

			}

		};

	}// end startEggFallTimer

	protected void pauseGame() {

		masterLvlTimer.cancel();
		eggDelayTimer.cancel();

		sensorManager.unregisterListener(eventListener);

	}// end pauseGame

	// Generates a random position for the egg fall
	protected int generateRandomPosition() {
		Random rand = new Random();
		return rand.nextInt(3); // 0 for left; 1 for center; 2 for right

	}// end generateRandomPosition

	protected void startEggFall(int position) {

		final int pos = position;

		final ImageView eggView = main.createEgg(pos);

		Animation eggAnimation = AnimationUtils.loadAnimation(main,
				R.anim.eggdrop);

		eggView.startAnimation(eggAnimation);
		
		animListener = new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
				main.scoreCount += 1;
				String countStr = "Score: " + String.valueOf(main.scoreCount);
				main.scoreView.setText(countStr);
				eggView.setVisibility(View.GONE);

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

	public void moveBasket(String direction, int incrementValue) {

		final int unitInc = main.convertToPixel(incrementValue);
		RelativeLayout.LayoutParams basketLayout = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		basketLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		basketLayout.height = 120;
		main.xView.setText(String.valueOf(xBasketPosition));
		main.xView.setBackgroundColor(Color.WHITE);

		if (direction.equals("right")) {
			if (xBasketPosition <= main.convertToPixel(rightBound)) {
				if (xBasketPosition == 0 || xBasketPosition > 0) {
					rightMargin = main.convertToPixel(128) * (-1);
					leftMargin += unitInc;
					basketLayout.addRule(RelativeLayout.RIGHT_OF,
							R.id.chickenCenter);
					basketLayout.setMargins(leftMargin, 0, 0, 20);

				} else {
					rightMargin -= unitInc;
					basketLayout.addRule(RelativeLayout.LEFT_OF,
							R.id.chickenCenter);
					basketLayout.setMargins(0, 0, rightMargin, 20);
				}

				xBasketPosition += unitInc;
				basketView.setLayoutParams(basketLayout);
			}
		} else {

			if (xBasketPosition >= main.convertToPixel(leftBound)) {

				if (xBasketPosition == 0 || xBasketPosition > 0) {
					leftMargin -= unitInc;
					basketLayout.addRule(RelativeLayout.RIGHT_OF,
							R.id.chickenCenter);
					basketLayout.setMargins(leftMargin, 0, 0, 20);
				} else {
					leftMargin = main.convertToPixel(128) * (-1);
					rightMargin += unitInc;
					basketLayout.addRule(RelativeLayout.LEFT_OF,
							R.id.chickenCenter);
					basketLayout.setMargins(0, 0, rightMargin, 20);
				}
				xBasketPosition -= unitInc;
				basketView.setLayoutParams(basketLayout);
			}
		}

	}// end of moveBasket()

	public int getBasketPosition() {
		return xBasketPosition;
	}
	
}
