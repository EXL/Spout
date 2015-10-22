package ru.exlmoto.spout;

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

	private static final int VIBRATION_DURATION = 50;

	public static void doVibrate() {
		m_vibrator.vibrate(VIBRATION_DURATION);
	}

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
//		this.onDestroy();
		System.exit(0);
	}
}
