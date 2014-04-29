package ca.gsalisi.games.eggs;

import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
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
	
	private ImageView eggView;
	private MyScrollView hScroll;
	private TextView scoreView;
	private TextView countdownView;
	public TextView livesLabel;
	
	
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
		
		int devWidthDp = getDeviceWidth();
		
		if( devWidthDp <  convertToPx(400) ){
			basketView.getLayoutParams().width = getDeviceWidth()*2 - convertToPx(170);
		}else{
			basketView.getLayoutParams().width = convertToPx(800) - convertToPx(170);
		}
			
		//-------------- create view references -----------------//
		
		livesLabel = (TextView) main.findViewById(R.id.lives_label);
		livesLabel.setTypeface(typeface);
		
		livesView = (TextView) main.findViewById(R.id.lives_view);
		livesView.setTypeface(typeface);
		
		countdownView = (TextView) main.findViewById(R.id.countdown_view);
		getCountdownView().setTypeface(typeface);
		
		scoreView = (TextView) main.findViewById(R.id.score_view);
		getScoreView().setTypeface(typeface);
		
		levelView = (TextView) main.findViewById(R.id.level_view);
		levelView.setTypeface(typeface);
		
	
	}// end initializeGraphics()
	
	public ImageView createEgg(int position, String color) {
		
		RelativeLayout.LayoutParams layoutParams = getMyLayoutParams(RelativeLayout.ALIGN_PARENT_TOP);
		

		switch (position) {//0 for left; 1 for center; 2 for right
		case 0:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutParams.setMargins(convertToPx(25), convertToPx(60), 0, 0);
			layoutParams.height = convertToPx(35);
			

			break;
		case 1:
		
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL,
					RelativeLayout.TRUE);
			layoutParams.setMargins(0, convertToPx(60), 0, 0);
			layoutParams.height = convertToPx(35);
			break;
		case 2:
			layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutParams.setMargins(0, convertToPx(60), convertToPx(25), 0);
			layoutParams.height = convertToPx(35);
			break;
		default:
			break;
		}

		eggView = new ImageView(main);
		if(color.equals("white")){
			getEggView().setImageResource(R.drawable.egg_white);
			//playSoundEffect(0, 50);
		}else if(color.equals("gold")){
			getEggView().setImageResource(R.drawable.egg_gold);
			
		}else{
			getEggView().setImageResource(R.drawable.egg_bad);
		}
		getEggView().setLayoutParams(layoutParams);

		rLayout.addView(getEggView());
		bringChickensToFront();
		bringBasketToFront();
		
		return getEggView();
		
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
		
		return convertToDp(basketView.getLayoutParams().width/2) - 55;
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
		getScoreView().setText(countStr);
	}
	// converts dp to pixels.
	public int convertToPx(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				main.getResources().getDisplayMetrics());
	}
	public int convertToDp(int px){
		
		float scale = main.getResources().getDisplayMetrics().density;
		int dp = (int) (px / scale + 0.5f);
		return dp; 
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

	public HorizontalScrollView getBasketScrollView() {
		return hScroll;
	}

	public ImageView getEggView() {
		return eggView;
	}

	public TextView getScoreView() {
		return scoreView;
	}


	public TextView getCountdownView() {
		return countdownView;
	}


}
