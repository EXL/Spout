#include <jni.h>
#include <android/log.h>

#include "ru_exlmoto_spout_SpoutNativeLibProxy.h" // JNI header

enum KeyCodes {
	KEY_LEFT	=	0x01,
	KEY_RIGHT	=	0x02,
	KEY_UP		=	0x03,
	KEY_DOWN	=	0x04,
	KEY_FIRE	=	0x05,
	KEY_QUIT	=	0x06,
	KEY_PAUSE	=	0x07
};

static int appRunning = 0;

extern "C"
void a_printf(char const * const format, ...) {
	va_list ap;
	va_start(ap, format);
	__android_log_print(ANDROID_LOG_WARN, "Spout_App", format, ap);
}

// Init
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeInit
  (JNIEnv *env, jclass c) {

}

// Deinit
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDeinit
  (JNIEnv *env, jclass c) {

}

// Surface Changed
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeSurfaceChanged
  (JNIEnv *env, jclass c, jint width, jint height) {

}

// Draw
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDraw
  (JNIEnv *env, jclass c) {

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
	jintArray a[4] = {0, 1, 2, 3 };
	return a;
}
