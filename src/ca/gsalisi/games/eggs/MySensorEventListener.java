package ca.gsalisi.games.eggs;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class MySensorEventListener implements SensorEventListener {

	MainActivity main;
	float smoothedValue;
	double previousValue;
	double rtnFiltered;
	boolean first;

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

			if (first) {
				smoothedValue = event.values[1];
				first = false;
			} else {

				// correct value
				float valueCorrected = event.values[1];

				rtnFiltered = lowPassFilter(valueCorrected, 10) * 100;

				// check if this value is incremented or decremented by a
				// certain value
				// move basket accordingly

				if (rtnFiltered > previousValue + .7) {
					main.moveBasket("right", 30);
					previousValue = rtnFiltered;
				} else if (rtnFiltered > previousValue + .5) {
					main.moveBasket("right", 25);
					previousValue = rtnFiltered;
				} else if (rtnFiltered > previousValue + .2) {
					main.moveBasket("right", 15);
					previousValue = rtnFiltered;
				} else if (rtnFiltered > previousValue + .08) {
					main.moveBasket("right", 10);
					previousValue = rtnFiltered;
				}

				if (rtnFiltered > previousValue + .7) {
					main.moveBasket("left", 30);
					previousValue = rtnFiltered;
				} else if (rtnFiltered < previousValue - .5) {
					main.moveBasket("left", 25);
					previousValue = rtnFiltered;
				} else if (rtnFiltered < previousValue - .2) {
					main.moveBasket("left", 15);
					previousValue = rtnFiltered;
				} else if (rtnFiltered < previousValue - .08) {
					main.moveBasket("left", 10);
					previousValue = rtnFiltered;
				}

			}

			main.yView.setText(String.valueOf(previousValue));

		}
	}

	protected double lowPassFilter(float value, float smoothing) {

		smoothedValue += (value - smoothedValue) / smoothing;

		return smoothedValue;
	}
}
