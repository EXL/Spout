package ru.exlmoto.spout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ru.exlmoto.spout.SpoutLauncher.SpoutSettings;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SpoutNativeSurface extends GLSurfaceView implements android.opengl.GLSurfaceView.Renderer {

	private static final int FPS_RATE = 60;

	public static final int KEY_LEFT        =    0x01;
	public static final int KEY_RIGHT       =    0x02;
	private static final int KEY_UP         =    0x03;
	private static final int KEY_DOWN       =    0x04;
	public static final int KEY_FIRE        =    0x05;
	private static final int KEY_QUIT       =    0x06;
	private static final int KEY_PAUSE      =    0x07;
	private static final int KEY_UNKNOWN    =    0x08;

	private long m_lastFrame = 0;

	private boolean left_key_pressed = false;
	private boolean right_key_pressed = false;
	private boolean fire_key_pressed = false;
	private boolean fire_hold_key_pressed = false;

	public SpoutNativeSurface(Context context) {
		super(context);
		setRenderer(this);

		// We wants events
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		// We wants keep on screen
		setKeepScreenOn(true);
	}

	@Override
	public void onPause() {
		SpoutActivity.toDebug("== GL_SURFACE ON PAUSE ==");
		super.onPause();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		SpoutActivity.toDebug("== GL_SURFACE CREATED ==");
		SpoutNativeLibProxy.SpoutInitilizeGlobalJavaEnvPointer();

		SpoutNativeLibProxy.SpoutNativePushScore(SpoutLauncher.SpoutSettings.s_scoreHeight,
				SpoutLauncher.SpoutSettings.s_scoreScore);

		SpoutNativeLibProxy.SpoutSetSound(SpoutLauncher.SpoutSettings.s_Sound);

		SpoutNativeLibProxy.SpoutNativeInit();

		SpoutNativeLibProxy.SpoutFilter(SpoutLauncher.SpoutSettings.s_Filter);
		SpoutNativeLibProxy.SpoutDisplayOffsetX(SpoutLauncher.SpoutSettings.s_OffsetX);
		SpoutNativeLibProxy.SpoutDisplayOffsetY(SpoutLauncher.SpoutSettings.s_OffsetY);
	}

	public void onClose() {
		SpoutActivity.toDebug("== GL_SURFACE CLOSE ==");
		SpoutNativeLibProxy.SpoutNativeDeinit();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		long currentFrame = SystemClock.uptimeMillis();
		long diff = currentFrame - m_lastFrame;
		m_lastFrame = currentFrame;
		SpoutNativeLibProxy.SpoutNativeDraw();

		// Vibration
		if (SpoutLauncher.SpoutSettings.s_Vibro) {
			if (SpoutNativeLibProxy.SpoutVibrate()) {
				SpoutActivity.doVibrate(50);
			}
		}

//		// Sound
//		// Now From JNI
//		if (SpoutLauncher.SpoutSettings.s_Sound) {
//			if (SpoutNativeLibProxy.SpoutVibrate()) {
//				SpoutActivity.playSound(SpoutSounds.s_gameover);
//			}
//		}

		try {
			long sleepfor = (1000 / FPS_RATE) - diff;
			if (sleepfor > 0) {
//				SpoutActivity.toDebug("Sleep now: " + sleepfor);
				Thread.sleep(sleepfor);
			}
		} catch (InterruptedException ex) { }
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		SpoutNativeLibProxy.SpoutNativeSurfaceChanged(width, height);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_A:
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_LEFT);
			break;
		}
		case KeyEvent.KEYCODE_D:
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_RIGHT);
			break;
		}
		case KeyEvent.KEYCODE_W:
		case KeyEvent.KEYCODE_DPAD_UP: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_UP);
			break;
		}
		case KeyEvent.KEYCODE_S:
		case KeyEvent.KEYCODE_DPAD_DOWN: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_DOWN);
			break;
		}
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_SPACE:
		case KeyEvent.KEYCODE_E:
		case KeyEvent.KEYCODE_Z:
		case KeyEvent.KEYCODE_X:
		case KeyEvent.KEYCODE_C:
		case KeyEvent.KEYCODE_DPAD_CENTER: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_FIRE);
			break;
		}
		case KeyEvent.KEYCODE_V: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_QUIT);
			break;
		}
		case KeyEvent.KEYCODE_P: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_PAUSE);
			break;
		}
		default: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_UNKNOWN);
			break;
		}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_A:
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_LEFT);
			break;
		}
		case KeyEvent.KEYCODE_D:
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_RIGHT);
			break;
		}
		case KeyEvent.KEYCODE_W:
		case KeyEvent.KEYCODE_DPAD_UP: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_UP);
			break;
		}
		case KeyEvent.KEYCODE_S:
		case KeyEvent.KEYCODE_DPAD_DOWN: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_DOWN);
			break;
		}
		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_SPACE:
		case KeyEvent.KEYCODE_E:
		case KeyEvent.KEYCODE_Z:
		case KeyEvent.KEYCODE_X:
		case KeyEvent.KEYCODE_C:
		case KeyEvent.KEYCODE_DPAD_CENTER: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_FIRE);
			break;
		}
		case KeyEvent.KEYCODE_V: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_QUIT);
			break;
		}
		case KeyEvent.KEYCODE_P: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_PAUSE);
			break;
		}
		default: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_UNKNOWN);
			break;
		}
		}

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (SpoutSettings.s_DisableButtons) {
			float x = event.getX();
			float y = event.getY();

			float half = getHeight() / 2.0f;
			boolean firstHalf = (y <= half);

			float chunk = getWidth() / 4.0f;
			boolean first = (x <= chunk);
			boolean second = ((x > chunk) && (x <= chunk * 3));
			boolean third = ((x > chunk * 3) && (x <= chunk * 4));

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (first) {
					SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_LEFT);
					left_key_pressed = true;
				} else if (second) {
					if (!firstHalf) {
						if (fire_hold_key_pressed) {
							SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_FIRE);
							try {
								long sleepfor = 50;
								SpoutActivity.toDebug("Sleep now hack: " + sleepfor);
								Thread.sleep(sleepfor);
								SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_FIRE);
							} catch (InterruptedException ex) { }
							fire_hold_key_pressed = false;
						} else {
							SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_FIRE);
							fire_key_pressed = true;
						}
					} else {
						fire_hold_key_pressed = !fire_hold_key_pressed;
						if (fire_hold_key_pressed) {
							SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_FIRE);
						} else {
							SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_FIRE);
						}
					}
				} else if (third) {
					SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_RIGHT);
					right_key_pressed = true;
				}
			}
			else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (left_key_pressed) {
					SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_LEFT);
					left_key_pressed = false;
				} else if (fire_key_pressed) {
					SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_FIRE);
					fire_key_pressed = false;
					fire_hold_key_pressed = false;
				} else if (right_key_pressed) {
					SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_RIGHT);
					right_key_pressed = false;
				}
			}
		}
		return true;
	}
}
