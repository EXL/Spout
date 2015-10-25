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

	public native static boolean SpoutVibrate();

	public native static int SpoutGetScoreScores();
	public native static int SpoutGetScoreHeight();

	public native static void SpoutDisplayOffsetX(int offset_x);
	public native static void SpoutDisplayOffsetY(int offset_y);

	public native static void SpoutInitilizeGlobalJavaEnvPointer();
}
