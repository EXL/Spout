package ru.exlmoto.spout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SpoutActivity extends Activity {

	private SpoutNativeSurface m_spoutNativeSurface;

	private static final String APP_TAG = "Spout_App";

	private int scoreHeight = 45;
	private int scoreScore = 65;

	private static boolean applyFilter = false;

	private void fillSettings() {
		Bundle extras = getIntent().getExtras();

		setApplyFilter(extras.getBoolean("filter"));
	}

	public static boolean getApplyFilter() {
		return applyFilter;
	}

	private void setApplyFilter(boolean applyFilter) {
		SpoutActivity.applyFilter = applyFilter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* We like to be fullscreen */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

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

	private void setScoresToSpoutNative() {
		SpoutNativeLibProxy.SpoutNativePushScore(scoreHeight, scoreScore);
	}

	private void getScoresFromSpoutNative() {
		int[] ret = SpoutNativeLibProxy.SpoutNativeGetScore();
		scoreHeight = ret[0];
		scoreScore = ret[1];
	}
}
