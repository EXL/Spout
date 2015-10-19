#include <jni.h>
#include <android/log.h>

#include "ru_exlmoto_spout_SpoutNativeLibProxy.h" // JNI header

extern "C" {
#include "piece.h"
#include "SpoutNativeLibProxy.h"

unsigned char keysState[] = {
		0,
		0, // KEY_LEFT
		0, // KEY_RIGHT
		0, // KEY_UP
		0, // KEY_DOWN
		0, // KEY_FIRE
		0, // KEY_QUIT
		0, // KEY_PAUSE
		0  // KEY_UNKNOWN
};
} // end extern "C"

static int appRunning = 0;

// Init
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeInit
  (JNIEnv *env, jclass c) {
	if (appRunning == 0) {
		LOGI("Spout_Native_Init...");

		initSpoutGLES();

		appRunning = 1;
	}
}

// Deinit
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDeinit
  (JNIEnv *env, jclass c) {
	if (appRunning == 1) {
		LOGI("Spout_Native_Deinit...");

		deinitSpoutGLES();

		appRunning = 0;
	}
}

// Surface Changed
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeSurfaceChanged
  (JNIEnv *env, jclass c, jint width, jint height) {
	resizeSpoutGLES(width, height);
}

// Draw
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDraw
  (JNIEnv *env, jclass c) {
	stepSpoutGLES();

	// pceLCDTrans();
}

// KeyDown
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeKeyDown
  (JNIEnv *env, jclass c, jint keyCode) {
	keysState[keyCode] = 1;
}

// KeyUp
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeKeyUp
  (JNIEnv *env, jclass c, jint keyCode) {
	keysState[keyCode] = 0;
}

// Push Score
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativePushScore
  (JNIEnv *env, jclass c, jint scoreHeight, jint scoreScore) {

}

// Get Score
JNIEXPORT
jintArray JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeGetScore
  (JNIEnv *env, jclass c) {
//	int a[4] = {0, 1, 2, 3 };
//	return a;
}
