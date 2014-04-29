package ca.gsalisi.games.eggs;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private EggGame eggGame;
	private GameGraphics gameGraphics;
	private SoundHandler soundHandler;
	private TextView bestScoreView;
	private SharedPreferences pref;
	private Dialog overDialog;
	private boolean gameOverBool;
	private int bestScore;
	public boolean soundsOn;
	protected ImageButton reset_btn;
	public Typeface typeface;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getGamePreferences();
		setGameTypeface();
		
		gameGraphics = new GameGraphics(this);
		soundHandler = new SoundHandler(this, soundsOn);
		gameGraphics.initializeGameGraphics();
		soundHandler.initializeSoundFx();
		
		gameOverBool = false;
		reset_btn = (ImageButton) findViewById(R.id.btn_reset);
		bestScoreView = (TextView) findViewById(R.id.best_view);
		bestScoreView.setTypeface(typeface);
		updateBest();

		eggGame = new EggGame(this, gameGraphics, soundHandler);
		eggGame.startGame();
		Log.d("OnCreate", "started game");
		
		//reset button
		reset_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				eggGame.stopGame();
				eggGame.resetGame();
				gameOverBool = false;
			}
		});

	}// end OnCreate

	private void getGamePreferences() {
	
		//get existing high score
		pref = this.getSharedPreferences("ca.gsalisi.eggs", Context.MODE_PRIVATE);
		bestScore = pref.getInt("best", 0);
		soundsOn = pref.getBoolean("soundfx", true);
				
	}
	
	private void setGameTypeface() {
		
		typeface = Typeface.createFromAsset(this.getAssets(),
		        "fonts/roostheavy.ttf");
	}


	//cancel timers when the window is closed or when 
	//back button or home button is pressed to prevent
	//eggs falling even when out of game
	@Override
	protected void onStop() {
		super.onStop();
		if(!gameOverBool){
			try{
				soundHandler.getSoundPool().autoPause();
				soundHandler.getSoundPool().release();
				eggGame.stopGame();
				Log.d("On Stop", "Called cancel timers");
			} catch(Exception e) {
				Log.d("On Stop", "exception caught");
			}
		}else{
			overDialog.dismiss();
		}
		finish();
		
		
	}//end onStop()
	

	
	//show game over dialog
	public void gameOver() {
		
		gameOverBool = true;
		
		if(eggGame.scoreCount > bestScore){//save high score if it is beaten
			bestScore = eggGame.scoreCount;
			pref = this.getSharedPreferences("ca.gsalisi.eggs", Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putInt("best", bestScore);
			editor.commit();
			updateBest();
		}
		
		eggGame.stopGame();
		
		//creates dialog
		overDialog = new Dialog(MainActivity.this, R.style.CustomDialog);
		overDialog.setContentView(R.layout.game_over);
		overDialog.setCanceledOnTouchOutside(false);
	
		TextView tv = new TextView(this);
		tv.setText("Game Over!");
		tv.setTypeface(typeface);
		
		overDialog.setTitle("Game Over!");
		
		TextView scoreOver = (TextView) overDialog.findViewById(R.id.score_view2);
		TextView bestOver = (TextView) overDialog.findViewById(R.id.best_view2);
		
		scoreOver.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		bestOver.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
		
		scoreOver.setTypeface(typeface);
		bestOver.setTypeface(typeface);
		
		scoreOver.setText(gameGraphics.getScoreView().getText().toString());
		bestOver.setText(bestScoreView.getText().toString());
		
		ImageButton restartbtn = (ImageButton) overDialog.findViewById(R.id.btn_gameover);
		restartbtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				overDialog.dismiss();
				eggGame.resetGame();
				gameOverBool = false;
			}
		});
		overDialog.show();
		
		overDialog.setOnCancelListener(new Dialog.OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				eggGame.resetGame();
				gameOverBool = false;
			}
		});
		
	}//end of gameOver()
	
	//updates best score view
	protected void updateBest() {
		bestScoreView.setText("Best: " + bestScore);
	}



	

}