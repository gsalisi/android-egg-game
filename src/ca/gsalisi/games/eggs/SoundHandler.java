package ca.gsalisi.games.eggs;

import android.media.AudioManager;
import android.media.SoundPool;

public class SoundHandler {
	
	private SoundPool soundPool;
	private int[] soundIds = new int[7];
	private boolean soundsOn;
	private int DEFAULT_VOLUME = 50;
	private MainActivity main;
	
	public SoundHandler(MainActivity main, boolean soundsOn) {
		this.main = main;
		this.soundsOn = soundsOn;
	}

	public void initializeSoundFx() {

		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);

		soundIds[0] = soundPool.load(main, R.raw.score_neg, 1);
		soundIds[1] = soundPool.load(main, R.raw.laying_hen_gold, 1);
		soundIds[2] = soundPool.load(main, R.raw.egg_dropped, 1);
		soundIds[3] = soundPool.load(main, R.raw.score, 1);
		soundIds[4] = soundPool.load(main, R.raw.score_2, 1);
		soundIds[5] = soundPool.load(main, R.raw.countdown_sec, 1);
		soundIds[6] = soundPool.load(main, R.raw.countdown_sec_go, 1);
		
	}
	
	void playSoundEffect(int id, int vol) {
		
		if(soundsOn){
			if(id == 2){
				soundPool.play(soundIds[id], vol, vol, 1, 0, (float) 0.7);
			}else{
				soundPool.play(soundIds[id], vol, vol, 1, 0, 1);
			}
		}
	}

	public SoundPool getSoundPool() {
		
		return soundPool;
	}


}
