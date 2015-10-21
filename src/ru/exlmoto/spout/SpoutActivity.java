package ru.exlmoto.spout;

import java.lang.Integer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SpoutActivity extends Activity {

	private SpoutNativeSurface m_spoutNativeSurface;

	private static Vibrator m_vibrator;

	private static final String APP_TAG = "Spout_App";

	private static final int VIBRATION_DURATION = 100;

	private static int scoreHeight = 45;
	private static int scoreScore = 65;

	private static boolean applyFilter = false;
	private static int displayOffsetX = 25;
	private static int displayOffsetY = 25;
	private static boolean vibration = false;

	private void fillSettings() {
		Bundle extras = getIntent().getExtras();

		setApplyFilter(extras.getBoolean("filter"));
		// TODO: On Screen Buttons
		setDisplayOffsetX(Integer.parseInt(extras.getString("offset_x")));
		setDisplayOffsetY(Integer.parseInt(extras.getString("offset_y")));

		setVibration(extras.getBoolean("vibro"));
	}

	public static int getScoreScore() {
		return scoreScore;
	}

	public static void setScoreScore(int scoreScore) {
		SpoutActivity.scoreScore = scoreScore;
	}

	public static int getScoreHeight() {
		return scoreHeight;
	}

	public static void setScoreHeight(int scoreHeight) {
		SpoutActivity.scoreHeight = scoreHeight;
	}

	public static int getDisplayOffsetX() {
		return displayOffsetX;
	}

	public static void setDisplayOffsetX(int displayOffsetX) {
		SpoutActivity.displayOffsetX = displayOffsetX;
	}

	public static int getDisplayOffsetY() {
		return displayOffsetY;
	}

	public static void setDisplayOffsetY(int displayOffsetY) {
		SpoutActivity.displayOffsetY = displayOffsetY;
	}

	public static boolean getVibration() {
		return vibration;
	}

	public static void setVibration(boolean vibration) {
		SpoutActivity.vibration = vibration;
	}

	public static boolean getApplyFilter() {
		return applyFilter;
	}

	public static void setApplyFilter(boolean applyFilter) {
		SpoutActivity.applyFilter = applyFilter;
	}

	public static void doVibrate() {
		m_vibrator.vibrate(VIBRATION_DURATION);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* We like to be fullscreen */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		fillSettings();

		m_spoutNativeSurface = new SpoutNativeSurface(this);
		setContentView(m_spoutNativeSurface);
	}

	static public void toDebug(String s) {
		Log.d(APP_TAG, s);
	}

	@Override
	protected void onDestroy() {
		toDebug("Destroying...");
		//TODO: call score save method ?
		//m_spoutNativeSurface.onClose();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		toDebug("Back key pressed!, Exiting...");
		//TODO: call score save method
		m_spoutNativeSurface.onClose();
		System.exit(0);
	}
}
