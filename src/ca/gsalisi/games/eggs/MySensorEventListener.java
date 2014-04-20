//package ca.gsalisi.games.eggs;
//
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//
//public class MySensorEventListener implements SensorEventListener {
//
//	MainActivity main;
//	float smoothedValue;
//	double previousValue;
//	double correctedSmoothedValue;
//	boolean first;
//	int basketPosition;
//
//	public MySensorEventListener(MainActivity mainActivity) {
//		main = mainActivity;
//		smoothedValue = 0;
//		previousValue = 0;
//		first = true;
//	}
//
//	@Override
//	public void onAccuracyChanged(Sensor arg0, int arg1) {
//		// do nothing
//	}
//
//	@Override
//	public void onSensorChanged(SensorEvent event) {
//
//		if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
//			
//			basketPosition = main.getBasketPosition();
//			
//			if (first) {
//				smoothedValue = event.values[1];
//				filter(event.values[1],10);
//				previousValue = smoothedValue*10;
//				first = false;
//			} else {
////				if(basketPosition == 0){
////					smoothedValue = event.values[1];
////				}
//				// correct value
//				float value = event.values[1];
//				filter(value, 20);
//				correctedSmoothedValue = smoothedValue * 100 ;
//			
//				// check if this value is incremented or decremented by a
//				// certain value
//				// move basket accordingly
//
//				if (correctedSmoothedValue > previousValue + .7) {
//					main.moveBasket("right", 15);
////					previousValue = correctedSmoothedValue;
//				} else if (correctedSmoothedValue > previousValue + .5) {
//					main.moveBasket("right", 10);
////					previousValue = correctedSmoothedValue;
//				} else if (correctedSmoothedValue > previousValue + .2) {
//					main.moveBasket("right", 5);
////					previousValue = correctedSmoothedValue;
//				} else if (correctedSmoothedValue > previousValue + .1) {
//					main.moveBasket("right", 2);
////					previousValue = correctedSmoothedValue;
//				}
//
//				if (correctedSmoothedValue < previousValue - .7) {
//					main.moveBasket("left", 15);
////					previousValue = correctedSmoothedValue;
//				} else if (correctedSmoothedValue < previousValue - .5) {
//					main.moveBasket("left", 10);
////					previousValue = correctedSmoothedValue;
//				} else if (correctedSmoothedValue < previousValue - .2) {
//					main.moveBasket("left", 5);
////					previousValue = correctedSmoothedValue;
//				} else if (correctedSmoothedValue < previousValue - .1) {
//					main.moveBasket("left", 2);
////					previousValue = correctedSmoothedValue;
//				}
//				
//				if(correctedSmoothedValue < previousValue - .08 || 
//						correctedSmoothedValue > previousValue + .08){
//					previousValue = correctedSmoothedValue;
//				}
//
//			}
//		}
//	}
//
//	protected void filter(float value, float smoothing) {
//		//if( (value - smoothedValue < upperBound && smoothedValue - value  ))
//		double testValue = (value - smoothedValue) / smoothing; 
//		boolean pass1 = !( testValue > 0 && basketPosition >= 270);
//		boolean pass2 = !( testValue < 0 && basketPosition <= -270);
//		
//		if(pass1 && pass2){
//			smoothedValue += (value - smoothedValue) / smoothing;
//		}
//	}
//}
