package ru.exlmoto.spout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class SpoutNativeSurface extends GLSurfaceView implements android.opengl.GLSurfaceView.Renderer {

	private static final int FPS_RATE = 60;

	private final int KEY_LEFT	=	0x01;
	private final int KEY_RIGHT	=	0x02;
	private final int KEY_UP	=	0x03;
	private final int KEY_DOWN	=	0x04;
	private final int KEY_FIRE	=	0x05;
	private final int KEY_QUIT	=	0x06;
	private final int KEY_PAUSE	=	0x07;
	private final int KEY_UNKNOWN =	0x08;

	private long m_lastFrame = 0;

	public SpoutNativeSurface(Context context) {
		super(context);
		setRenderer(this);

		// We wants events
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		SpoutNativeLibProxy.SpoutNativePushScore(SpoutLauncher.SpoutSettings.s_scoreHeight,
				SpoutLauncher.SpoutSettings.s_scoreScore);

		SpoutNativeLibProxy.SpoutNativeInit();

		SpoutNativeLibProxy.SpoutFilter(SpoutLauncher.SpoutSettings.s_Filter);
		SpoutNativeLibProxy.SpoutDisplayOffsetX(SpoutLauncher.SpoutSettings.s_OffsetX);
		SpoutNativeLibProxy.SpoutDisplayOffsetY(SpoutLauncher.SpoutSettings.s_OffsetY);
	}

	public void onClose() {
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
				SpoutActivity.doVibrate();
			}
		}

		try {
			long sleepfor = (1000 / FPS_RATE) - diff;
			if (sleepfor > 0) {
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
		float x = event.getX();
//		float y = event.getY();

		float chunk = getWidth() / 4.0f;
		boolean first = x <= chunk;
		boolean second = (x > chunk) && (x <= chunk * 3);
		boolean third = ((x > chunk * 3) && (x <= chunk * 4));

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (first) {
				SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_LEFT);
			} else if (second) {
				SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_FIRE);
			} else if (third) {
				SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_RIGHT);
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (first) {
				SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_LEFT);
			} else if (second) {
				SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_FIRE);
			} else if (third) {
				SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_RIGHT);
			}
		}

		return true;
	}
}
