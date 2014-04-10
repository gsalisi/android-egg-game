package ca.gsalisi.games.eggs;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MySensorEventListener implements SensorEventListener {

	MainActivity main;
	float smoothedValue;
	double lowerBound;
	double upperBound;
	double previousValue;
	double correctedSmoothedValue;
	boolean first;
	int basketPosition;

	public MySensorEventListener(MainActivity mainActivity) {
		main = mainActivity;
		smoothedValue = 0;
		previousValue = 0;
		first = true;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// do nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			
			basketPosition = main.getBasketPosition();
			
			if (first) {
				initializeBounds(event);
				first = false;
			} else {
				if(basketPosition == 0){
					initializeBounds(event);
				}
				// correct value
				float value = event.values[1];
				filter(value, 10);
				correctedSmoothedValue = smoothedValue * 100 ;

				// check if this value is incremented or decremented by a
				// certain value
				// move basket accordingly

				if (correctedSmoothedValue > previousValue + .7) {
					main.moveBasket("right", 30);
//					previousValue = correctedSmoothedValue;
				} else if (correctedSmoothedValue > previousValue + .5) {
					main.moveBasket("right", 25);
//					previousValue = correctedSmoothedValue;
				} else if (correctedSmoothedValue > previousValue + .2) {
					main.moveBasket("right", 15);
//					previousValue = correctedSmoothedValue;
				} else if (correctedSmoothedValue > previousValue + .08) {
					main.moveBasket("right", 8);
//					previousValue = correctedSmoothedValue;
				}

				if (correctedSmoothedValue < previousValue - .7) {
					main.moveBasket("left", 30);
//					previousValue = correctedSmoothedValue;
				} else if (correctedSmoothedValue < previousValue - .5) {
					main.moveBasket("left", 25);
//					previousValue = correctedSmoothedValue;
				} else if (correctedSmoothedValue < previousValue - .2) {
					main.moveBasket("left", 15);
//					previousValue = correctedSmoothedValue;
				} else if (correctedSmoothedValue < previousValue - .08) {
					main.moveBasket("left", 8);
//					previousValue = correctedSmoothedValue;
				}
				
				if(correctedSmoothedValue < previousValue - .08 || 
						correctedSmoothedValue > previousValue + .08){
					previousValue = correctedSmoothedValue;
				}

			}
			main.zView.setText(String.valueOf(smoothedValue));

		}
	}

	private void initializeBounds(SensorEvent event) {
		smoothedValue = event.values[1];
		upperBound = smoothedValue + 0.10;
		lowerBound = smoothedValue - 0.10;
		main.yView.setText(String.valueOf(smoothedValue));
	}

	protected void filter(float value, float smoothing) {
		//if( (value - smoothedValue < upperBound && smoothedValue - value  ))
		double testValue = (value - smoothedValue) / smoothing; 
		boolean pass1 = !( testValue > 0 && basketPosition >= 280);
		boolean pass2 = !( testValue < 0 && basketPosition <= -280);
		
		if(pass1 && pass2){
			smoothedValue += (value - smoothedValue) / smoothing;
		}
	}
}
