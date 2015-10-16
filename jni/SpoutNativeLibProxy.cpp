#include <jni.h>
#include <android/log.h>

#include "ru_exlmoto_spout_SpoutNativeLibProxy.h" // JNI header
#include "SpoutNativeLibProxy.h"                  // Keys

extern "C" {
#include "piece.h"

void a_printf(char const * const format, ...) {
	va_list ap;
	va_start(ap, format);
	__android_log_print(ANDROID_LOG_WARN, "Spout_App", format, ap);
}
} // end extern "C"

static int appRunning = 0;

// Init
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeInit
  (JNIEnv *env, jclass c) {
	if (appRunning == 0) {
		a_printf("Spout_Native_Init...");

		// TODO: here all GL and other inits
		initSpoutGLES();

		appRunning = 1;
	}
}

// Deinit
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDeinit
  (JNIEnv *env, jclass c) {
	if (appRunning == 1) {
		a_printf("Spout_Native_Deinit...");

		// TODO: deinits code here
		deinitSpoutGLES();

		appRunning = 0;
	}
}

// Surface Changed
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeSurfaceChanged
  (JNIEnv *env, jclass c, jint width, jint height) {
	reshapeSpoutGLES(width, height);
}

// Draw
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDraw
  (JNIEnv *env, jclass c) {
	// TODO: display code here
}

// KeyDown
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeKeyDown
  (JNIEnv *env, jclass c, jint keyCode) {

}

// KeyUp
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeKeyUp
  (JNIEnv *env, jclass c, jint keyCode) {

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
