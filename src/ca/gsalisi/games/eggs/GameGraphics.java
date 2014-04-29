package ca.gsalisi.games.eggs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class GameGraphics {
	
	private MainActivity main;
	private RelativeLayout rLayout;
	private ImageView chickenViewLeft;
	private ImageView chickenViewCenter;
	private ImageView chickenViewRight;
	private ImageView basketView;
	private ImageView eggBrokenLeft;
	private ImageView eggBrokenCenter;
	private ImageView eggBrokenRight;
	private TextView livesView;
	private TextView levelView;
	private Typeface typeface;
	
	protected ImageView eggView;
	protected MyScrollView hScroll;
	protected TextView scoreView;
	protected TextView countdownView;
	
	
	public GameGraphics(MainActivity mainActivity) {
		this.main = mainActivity;

	}

	protected void initializeGameGraphics() {
		
		typeface = Typeface.createFromAsset(main.getAssets(),
		        "fonts/roostheavy.ttf");
		rLayout = (RelativeLayout) main.findViewById(R.id.rLayout);
		
		// ---- create chicken references --- //
		chickenViewLeft = (ImageView) main.findViewById(R.id.chickenLeft);
		chickenViewCenter = (ImageView) main.findViewById(R.id.chickenCenter);
		chickenViewRight = (ImageView) main.findViewById(R.id.chickenRight);
			
		//---- Initialize broken eggs ----//
		//needs to create a function to make the code better and shorter!!
		
		eggBrokenLeft = (ImageView) main.findViewById(R.id.eggBrokenLeft);
		eggBrokenCenter = (ImageView) main.findViewById(R.id.eggBrokenCenter);
		eggBrokenRight = (ImageView) main.findViewById(R.id.eggBrokenRight);

		eggBrokenLeft.setVisibility(View.INVISIBLE);
		eggBrokenCenter.setVisibility(View.INVISIBLE);
		eggBrokenRight.setVisibility(View.INVISIBLE);
		
		// ----------------  create basket view -------------------//

		hScroll = (MyScrollView) main.findViewById(R.id.hScrollView);
		basketView = (ImageView) main.findViewById(R.id.basketView);
	
		basketView.getLayoutParams().width = getDeviceWidth()*2 - convertToPixel(170);
			
		//-------------- create view references -----------------//
		
		TextView livesLabel = (TextView) main.findViewById(R.id.lives_label);
		livesLabel.setTypeface(typeface);
		
		livesView = (TextView) main.findViewById(R.id.lives_view);
		livesView.setTypeface(typeface);
		
		countdownView = (TextView) main.findViewById(R.id.countdown_view);
		countdownView.setTypeface(typeface);
		
		scoreView = (TextView) main.findViewById(R.id.score_view);
		scoreView.setTypeface(typeface);
		
		levelView = (TextView) main.findViewById(R.id.level_view);
		levelView.setTypeface(typeface);
		
	
	}// end initializeGraphics()
	
	public ImageView createEgg(int position, String color) {
		
		RelativeLayout.LayoutParams layoutParams = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_TOP);
		

		switch (position) {//0 for left; 1 for center; 2 for right
		case 0:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.setMargins(convertToPixel(25), convertToPixel(60), 0, 0);
			layoutParams.height = convertToPixel(35);
			

			break;
		case 1:
		
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			layoutParams.setMargins(0, convertToPixel(60), 0, 0);
			layoutParams.height = convertToPixel(35);
			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, convertToPixel(60), convertToPixel(25), 0);
			layoutParams.height = convertToPixel(35);
			break;
		default:
			break;
		}

		eggView = new ImageView(main);
		if(color.equals("white")){
			eggView.setImageResource(R.drawable.egg_white);
			//playSoundEffect(0, 50);
		}else if(color.equals("gold")){
			eggView.setImageResource(R.drawable.egg_gold);
			
		}else{
			eggView.setImageResource(R.drawable.egg_bad);
		}
		eggView.setLayoutParams(layoutParams);

		rLayout.addView(eggView);
		bringChickensToFront();
		bringBasketToFront();
		
		return eggView;
		
	}
	
	public void showBrokenEgg(int position, int scoreInc) {
		
		//reveal broken egg view and then fade it out
		Animation eggFade = AnimationUtils.loadAnimation(main,
				R.anim.fadeout);
		if(scoreInc == 3){
			eggBrokenLeft.setImageResource(R.drawable.egg_gold_broken);
		}else{
			eggBrokenLeft.setImageResource(R.drawable.egg_bad_broken);
		}
		switch (position) {
		case 0:
			eggBrokenLeft.setVisibility(View.VISIBLE);
			eggBrokenLeft.startAnimation(eggFade);
			break;
		case 1:
			eggBrokenCenter.setVisibility(View.VISIBLE);
			eggBrokenCenter.startAnimation(eggFade);
			break;
		case 2:
			eggBrokenRight.setVisibility(View.VISIBLE);
			eggBrokenRight.startAnimation(eggFade);
			break;
		default:
			break;
		}
		
	}// end of showBrokenEgg()
	
	void bringChickensToFront() {

		chickenViewLeft.bringToFront();
		chickenViewCenter.bringToFront();
		chickenViewRight.bringToFront();

	}//end of chickensToFront()
	
	private void bringBasketToFront() {
		// TODO Auto-generated method stub
		hScroll.bringToFront();
	}

	public int getWidthReference() {
		
		return hScroll.getChildAt(0).getMeasuredWidth()-
		        main.getWindowManager().getDefaultDisplay().getWidth();
	}

	protected LayoutParams getMyLayoutParams(int rule) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(rule);
		return layoutParams;
	}

	//check if basket is in the right position
	//if it is then we add score, if not we subtract from lives
	
	
	
	
	public void shakeChicken(int pos) {
		Animation anim = AnimationUtils.loadAnimation(main, R.anim.shake);
		
		switch (pos) {
		case 0:
			chickenViewLeft.startAnimation(anim);
			break;
		case 1:
			chickenViewCenter.startAnimation(anim);
			break;
		case 2:
			chickenViewRight.startAnimation(anim);
			break;
		default:
			break;
		}
	}
	

	// updates lives text view
	public void updateLives(int numberOfLives) {
		livesView.setText(String.valueOf(numberOfLives));
		
	}
	// updates score text view
	public void updateScore(int score) {
		String countStr = "Score: " + String.valueOf(score);
		scoreView.setText(countStr);
	}
	// converts dp to pixels.
	public int convertToPixel(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				main.getResources().getDisplayMetrics());
	}
	public int convertToDp(int px){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
				main.getResources().getDisplayMetrics());
	}
	public int getDeviceWidth(){
		return (int) main.getResources().getDisplayMetrics().widthPixels;  // displayMetrics.density;
	}
	public int getDeviceHeight(){
		return (int) main.getResources().getDisplayMetrics().heightPixels;
	}


	public void updateLevel(int level) {
		levelView.setText("Level "+String.valueOf(level));
		
	}

	public View getBasketScrollView() {

		return hScroll;
	}


}
