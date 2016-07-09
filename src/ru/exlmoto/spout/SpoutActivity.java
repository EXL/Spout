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

import ru.exlmoto.spout.SpoutLauncher.SpoutSettings;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class SpoutActivity extends Activity implements SensorEventListener {

	private SpoutNativeSurface m_spoutNativeSurface;

	private static Vibrator m_vibrator;

	private static final String APP_TAG = "Spout_App";

	private static boolean holdPushed = false;

	private boolean nowLeft = false;
	private static final int touchDelay = 50;

	public static SoundPool soundPool = null;
	public static class SpoutSounds {
		public static int s_gameover;
		public static int s_fire;
		public static int s_button;
		public static int s_hold;
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
		if (!SpoutSettings.s_DisableButtons) {
			float densityPixels = getResources().getDisplayMetrics().density;
			toDebug("PixelDensity: " + densityPixels);

			int padding = (int)(50 * densityPixels);
			toDebug("Padding: " + padding);

			// LAYOUTS
			LinearLayout.LayoutParams parametersBf =
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
			int leftF = (int)(20 * densityPixels);
			int topF = 0;
			int rightF = (int)(20 * densityPixels);
			int bottomF = (int)(10 * densityPixels);
			parametersBf.setMargins(leftF, topF, rightF, bottomF);

			LinearLayout.LayoutParams parametersbLR =
					new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
			int leftLR = 0;
			int topLR = 0;
			int rightLR = 0;
			int bottomLR = (int)(10 * densityPixels);
			parametersbLR.setMargins(leftLR, topLR, rightLR, bottomLR);

			// HOLD FIRE BUTTON
			final Button buttonFireHold = new Button(this);
			if (SpoutSettings.s_ShowButtons) {
				buttonFireHold.setBackgroundColor(Color.argb(100, 229, 82, 90));
			}
			buttonFireHold.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						holdPushed = !holdPushed;

						if (holdPushed) {
							SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_FIRE);
							if (SpoutSettings.s_ShowButtons) {
								v.setBackgroundColor(Color.argb(100, 142, 207, 106));
							}
						} else {
							SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_FIRE);
							if (SpoutSettings.s_ShowButtons) {
								v.setBackgroundColor(Color.argb(100, 229, 82, 90));
							}
						}

						v.setPressed(holdPushed);

						if (SpoutSettings.s_Vibro) {
							doVibrate(15);
						}

						if (SpoutSettings.s_Sound) {
							playSound(SpoutSounds.s_hold);
						}

						break;
					case MotionEvent.ACTION_UP:
						//v.performClick();
						break;
					default:
						break;
					}
					return false;
				}

			});

			buttonFireHold.setText(getString(R.string.HoldText));
			if (!SpoutSettings.s_ShowButtons) {
				buttonFireHold.setBackgroundColor(Color.argb(0, 255, 255, 255));
				buttonFireHold.setTextColor(Color.argb(75, 212, 207, 199));
			}
			buttonFireHold.setPadding(padding, padding, padding, padding);
			buttonFireHold.setLayoutParams(parametersBf);

			// FIRE BUTTON
			Button buttonFire = new Button(this);
			buttonFire.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (holdPushed) {
							if (SpoutSettings.s_ShowButtons) {
								buttonFireHold.setBackgroundColor(Color.argb(100, 229, 82, 90));
							}
							SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_FIRE);
							try {
								long sleepfor = 50;
								SpoutActivity.toDebug("Sleep now hack: " + sleepfor);
								Thread.sleep(sleepfor);
								SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_FIRE);
							} catch (InterruptedException ex) { }
							holdPushed = false;
						}
						SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_FIRE);

						if (SpoutSettings.s_Vibro) {
							doVibrate(15);
						}

						if (SpoutSettings.s_Sound) {
							playSound(SpoutSounds.s_fire);
						}

						break;
					case MotionEvent.ACTION_UP:
						SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_FIRE);
						//v.performClick();
						break;
					default:
						break;
					}
					return false;
				}

			});
			buttonFire.setText(getText(R.string.FireText));
			if (!SpoutSettings.s_ShowButtons) {
				buttonFire.setBackgroundColor(Color.argb(0, 255, 255, 255));
				buttonFire.setTextColor(Color.argb(75, 212, 207, 199));
			}
			buttonFire.setPadding(padding, padding, padding, padding);
			buttonFire.setLayoutParams(parametersBf);

			// LEFT BUTTON
			Button buttonLeft = new Button(this);
			buttonLeft.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_LEFT);

						if (SpoutSettings.s_Vibro) {
							doVibrate(15);
						}

						if (SpoutSettings.s_Sound) {
							playSound(SpoutSounds.s_button);
						}

						break;
					case MotionEvent.ACTION_UP:
						SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_LEFT);
						//v.performClick();
						break;
					default:
						break;
					}
					return false;
				}

			});
			buttonLeft.setText(getString(R.string.LeftText));
			if (!SpoutSettings.s_ShowButtons) {
				buttonLeft.setBackgroundColor(Color.argb(0, 255, 255, 255));
				buttonLeft.setTextColor(Color.argb(75, 212, 207, 199));
			}
			buttonLeft.setPadding(padding, padding, padding, padding);
			buttonLeft.setLayoutParams(parametersbLR);

			// RIGHT BUTTON
			Button buttonRight = new Button(this);
			buttonRight.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						SpoutNativeLibProxy.SpoutNativeKeyDown(SpoutNativeSurface.KEY_RIGHT);

						if (SpoutSettings.s_Vibro) {
							doVibrate(15);
						}

						if (SpoutSettings.s_Sound) {
							playSound(SpoutSounds.s_button);
						}

						break;
					case MotionEvent.ACTION_UP:
						SpoutNativeLibProxy.SpoutNativeKeyUp(SpoutNativeSurface.KEY_RIGHT);
						//v.performClick();
						break;
					default:
						break;
					}
					return false;
				}

			});
			buttonRight.setText(getString(R.string.RightText));
			if (!SpoutSettings.s_ShowButtons) {
				buttonRight.setBackgroundColor(Color.argb(0, 255, 255, 255));
				buttonRight.setTextColor(Color.argb(75, 212, 207, 199));
			}
			buttonRight.setPadding(padding, padding, padding, padding);

			buttonRight.setLayoutParams(parametersbLR);

			// LAYOUTS SETTINGS
			LinearLayout ll0 = new LinearLayout(this);
			ll0.addView(buttonFireHold);
			ll0.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			addContentView(ll0, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			LinearLayout ll = new LinearLayout(this);

			// Add buttons to layer
			if (!SpoutSettings.s_Sensor) {
				ll.addView(buttonLeft);
			}
			ll.addView(buttonFire);
			if (!SpoutSettings.s_Sensor) {
				ll.addView(buttonRight);
			}
			// End

			ll.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

			addContentView(ll, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
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
			final int delay = duration;

			new Thread(new Runnable() {

				@Override
				public void run() {
					m_vibrator.vibrate(delay);
				}

			}).start();
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
		float y = event.values[1]; // Y-axis

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

		if (SpoutSettings.s_Sound) {
			soundPool.release();
		}

		// Because we want drop all memory of library
		System.exit(0);
	}
}
