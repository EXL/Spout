package ru.exlmoto.spout;

import ru.exlmoto.spout.SpoutLauncher.SpoutSettings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class SpoutActivity extends Activity {

	private SpoutNativeSurface m_spoutNativeSurface;

	private static Vibrator m_vibrator;

	private static final String APP_TAG = "Spout_App";

	private static final int VIBRATION_DURATION = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		/* We like to be fullscreen */
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		m_vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		m_spoutNativeSurface = new SpoutNativeSurface(this);
		setContentView(m_spoutNativeSurface);
	}

	// JNI-method
	public static void setScores(int scoresH, int scoresS) {
		SpoutActivity.toDebug("--- From JNI!, a1: " + scoresH + " a2: " + scoresS);

		SpoutSettings.s_scoreHeight = scoresH;
		SpoutSettings.s_scoreScore = scoresS;
	}

	public static void toDebug(String s) {
		Log.d(APP_TAG, s);
	}

	public static void doVibrate() {
		m_vibrator.vibrate(VIBRATION_DURATION);
	}

	private void writeScoresToSharedPreferences() {
		toDebug("write scores... " + SpoutSettings.s_scoreHeight + " " + SpoutSettings.s_scoreScore);
		SharedPreferences settings = getSharedPreferences("ru.exlmoto.spout", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("s_scoreHeight", SpoutSettings.s_scoreHeight);
		editor.putInt("s_scoreScore", SpoutSettings.s_scoreScore);
		editor.commit();
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

		writeScoresToSharedPreferences();

		m_spoutNativeSurface.onPause();
		m_spoutNativeSurface.onClose();

		// super.onBackPressed();
		// Because we want drop all memory of library
		System.exit(0);
	}
}
