package ru.exlmoto.spout;

public class SpoutNativeLibProxy {

	// Load native library
	static {
		System.loadLibrary("Spout");
	}

	public native static void SpoutNativeInit();
	public native static void SpoutNativeDeinit();

	public native static void SpoutNativeSurfaceChanged(int width, int height);
	public native static void SpoutNativeDraw();

	public native static void SpoutNativeKeyDown(int keyCode);
	public native static void SpoutNativeKeyUp(int keyCode);

	public native static void SpoutFilter(boolean filterGLES);

	public native static void SpoutNativePushScore(int height, int score);

	// TODO: return what? ByteBuffer?
	public native static int[] SpoutNativeGetScore();
}
