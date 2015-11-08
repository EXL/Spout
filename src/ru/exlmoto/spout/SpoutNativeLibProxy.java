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

	public native static void SpoutSetSound(boolean sound);

	public native static void SpoutSetColor(boolean color);
	public native static void SpoutSetTail(boolean tail);

	public native static void SpoutSet3DCube(boolean cube);

	public native static int SpoutGetScoreScores();
	public native static int SpoutGetScoreHeight();

	public native static void SpoutDisplayOffsetX(int offset_x);
	public native static void SpoutDisplayOffsetY(int offset_y);

	public native static void SpoutInitilizeGlobalJavaEnvPointer();
}
