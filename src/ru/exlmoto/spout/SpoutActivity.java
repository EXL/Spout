/************************************************************************************
** The MIT License (MIT)
**
** Copyright (c) 2015 Serg "EXL" Koles
**
** Permission is hereby granted, free of charge, to any person obtaining a copy
** of this software and associated documentation files (the "Software"), to deal
** in the Software without restriction, including without limitation the rights
** to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
** copies of the Software, and to permit persons to whom the Software is
** furnished to do so, subject to the following conditions:
**
** The above copyright notice and this permission notice shall be included in all
** copies or substantial portions of the Software.
**
** THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
** IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
** FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
** AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
** LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
** OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
** SOFTWARE.
************************************************************************************/

package ru.exlmoto.spout;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import ru.exlmoto.spout.SpoutLauncher.SpoutSettings;

public class SpoutActivity extends Activity implements SensorEventListener {

	private SpoutNativeSurface m_spoutNativeSurface;
	private SpoutJoystickView m_SpoutJoystickView;

	private static Vibrator m_vibrator;

	private static final String APP_TAG = "Spout_App";

	private boolean nowLeft = false;
	public static final int touchDelay = 50;

	public static SoundPool soundPool = null;
	public static class SpoutSounds {
		public static int s_gameover;
		public static int s_fire;
		public static int s_button;
		public static int s_hold;
	}

	@SuppressWarnings("deprecation")
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

		if (SpoutSettings.s_Sound) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
		}

		// SOUNDS
		if (SpoutSettings.s_Sound) {
			soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			try {
				SpoutSounds.s_button = soundPool.load(getAssets().openFd("s_button.wav"), 1);
				SpoutSounds.s_fire = soundPool.load(getAssets().openFd("s_fire.wav"), 1);
				SpoutSounds.s_gameover = soundPool.load(getAssets().openFd("s_gameover.wav"), 1);
				SpoutSounds.s_hold = soundPool.load(getAssets().openFd("s_hold.wav"), 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// SENSORS
		if (SpoutSettings.s_Sensor) {
			SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			toDebug("== Using accelerometer: " + accelerometer.getName());
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}

		// ALL ONSCREEN BUTTONS
		switch (SpoutLauncher.SpoutSettings.s_SensorType) {
			case SpoutLauncher.SENSOR_TYPE_JOY:
			default:
				m_SpoutJoystickView = new SpoutJoystickView(this);
				addContentView(m_SpoutJoystickView,
						new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
				break;
			case SpoutLauncher.SENSOR_TYPE_KEY:
				LinearLayout ll = new LinearLayout(this);
				ll.setBackgroundDrawable(getResources().getDrawable(R.drawable.overlay_controls));
				addContentView(ll, new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
				break;
			case SpoutLauncher.SENSOR_TYPE_NON:
				break;
		}
	}

	// JNI-method
	public static void playSound(int soundID) {
		if (SpoutSettings.s_Sound && (soundID != 0)) {
			soundPool.play(soundID, 1.0f, 1.0f, 0, 0, 1.0f);
		}
	}

	// JNI-method
	public static void setScores(int scoresH, int scoresS) {
		SpoutActivity.toDebug("--- From JNI!, a1: " + scoresH + " a2: " + scoresS);

		SpoutSettings.s_scoreHeight = scoresH;
		SpoutSettings.s_scoreScore = scoresS;
	}

	// JNI-method
	public static void doVibrate(int duration) {
		if (SpoutSettings.s_Vibro) {
			m_vibrator.vibrate(duration);
		}
	}

	public static void toDebug(String s) {
		Log.d(APP_TAG, s);
	}

	private void writeScoresToSharedPreferences() {
		int scoreS = SpoutNativeLibProxy.SpoutGetScoreScores();
		int scoreH = SpoutNativeLibProxy.SpoutGetScoreHeight();

		toDebug("ScoreS: " + scoreS + " ScoreH: " + scoreH +
				" SetS: " + SpoutSettings.s_scoreScore +
				" SetH: " + SpoutSettings.s_scoreHeight);

		if (scoreS > SpoutSettings.s_scoreScore) {
			SpoutSettings.s_scoreScore = scoreS;
			SpoutSettings.s_scoreHeight = scoreH;
			toDebug("Update scores!");
		}

		SharedPreferences settings = getSharedPreferences("ru.exlmoto.spout", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("s_scoreHeight", SpoutSettings.s_scoreHeight);
		editor.putInt("s_scoreScore", SpoutSettings.s_scoreScore);
		editor.commit();

		toDebug("write scores... " + SpoutSettings.s_scoreHeight + " " + SpoutSettings.s_scoreScore);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing to do here
	}

	private void toLeft(final boolean left) {
		if (!nowLeft) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					nowLeft = true;
					try {
						if (left) {
							SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_RIGHT);
							SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_LEFT);
						} else {
							SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_LEFT);
							SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_RIGHT);
						}

						Thread.sleep(touchDelay);

						if (left) {
							SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_LEFT);
						} else {
							SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_RIGHT);
						}

						nowLeft = false;
					} catch (InterruptedException ex) { }
				}

			}).start();
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0]; // X-axis
		float y = event.values[1]; // Y-axis

		if (x < 6.0f) {
			SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_FIRE);
		} else {
			SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_FIRE);
		}

		if (y > (-3.0f) && y < (3.0f)) {
			SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_LEFT);
			SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_RIGHT);
		} else {
			if (y < 0.0f) {
				toLeft(true);
			}

			if (y > 0.0f) {
				toLeft(false);
			}
		}
	}

	private void startLauncher() {
		Intent intent = this.getPackageManager()
				.getLaunchIntentForPackage("ru.exlmoto.spout");
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		toDebug("Destroying activity...");
		//TODO: call score save method ?
		writeScoresToSharedPreferences();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		toDebug("Back key pressed!, Exiting...");

		if (SpoutSettings.s_Sound) {
			soundPool.release();
		}

		startLauncher();

		m_spoutNativeSurface.onPause();
		m_spoutNativeSurface.onClose();

		// Because we want drop all memory of library
		// System.exit(0);
		// Now exit() call in native deinitSpoutGLES()
		super.onBackPressed();
	}
}
