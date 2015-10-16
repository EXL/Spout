package ru.exlmoto.spout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;

public class SpoutNativeSurface extends GLSurfaceView implements android.opengl.GLSurfaceView.Renderer {

	private final int KEY_LEFT	=	0x1;
	private final int KEY_RIGHT	=	0x2;
	private final int KEY_UP	=	0x3;
	private final int KEY_DOWN	=	0x4;
	private final int KEY_FIRE	=	0x6;
	private final int KEY_QUIT	=	0x7;
	private final int KEY_PAUSE	=	0x8;

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
		SpoutNativeLibProxy.SpoutNativeInit();
	}

	public void onClose() {
		SpoutNativeLibProxy.SpoutNativeDeinit();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		SpoutNativeLibProxy.SpoutNativeDraw();
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
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_UP);
			break;
		}
		case KeyEvent.KEYCODE_D:
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_RIGHT);
			break;
		}
		case KeyEvent.KEYCODE_W:
		case KeyEvent.KEYCODE_DPAD_UP: {
			SpoutNativeLibProxy.SpoutNativeKeyDown(KEY_LEFT);
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
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_A:
		case KeyEvent.KEYCODE_DPAD_LEFT: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_UP);
			break;
		}
		case KeyEvent.KEYCODE_D:
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_RIGHT);
			break;
		}
		case KeyEvent.KEYCODE_W:
		case KeyEvent.KEYCODE_DPAD_UP: {
			SpoutNativeLibProxy.SpoutNativeKeyUp(KEY_LEFT);
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
		}

		return super.onKeyUp(keyCode, event);
	}
}
