#include <jni.h>
#include <android/log.h>
#include <GLES/gl.h>
#include <GLES/glext.h>

#include <ctime>

#include "ru_exlmoto_spout_SpoutNativeLibProxy.h" // JNI header

extern "C" {
#include "piece.h"
#include "SpoutNativeLibProxy.h"

JNIEnv *javaEnviron = NULL;

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

/* Return current time in milliseconds */
static double now_ms(void)
{
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec*1000. + tv.tv_usec/1000.;
}

/* simple stats management */
typedef struct {
    double  renderTime;
    double  frameTime;
} FrameStats;

#define  MAX_FRAME_STATS  200
#define  MAX_PERIOD_MS    1500

typedef struct {
    double  firstTime;
    double  lastTime;
    double  frameTime;

    int         firstFrame;
    int         numFrames;
    FrameStats  frames[ MAX_FRAME_STATS ];
} Stats;

static void
stats_init( Stats*  s )
{
    s->lastTime = now_ms();
    s->firstTime = 0.;
    s->firstFrame = 0;
    s->numFrames  = 0;
}

static void
stats_startFrame( Stats*  s )
{
    s->frameTime = now_ms();
}

static void
stats_endFrame( Stats*  s )
{
    double now = now_ms();
    double renderTime = now - s->frameTime;
    double frameTime  = now - s->lastTime;
    int nn;

    if (now - s->firstTime >= MAX_PERIOD_MS) {
        if (s->numFrames > 0) {
            double minRender, maxRender, avgRender;
            double minFrame, maxFrame, avgFrame;
            int count;

            nn = s->firstFrame;
            minRender = maxRender = avgRender = s->frames[nn].renderTime;
            minFrame  = maxFrame  = avgFrame  = s->frames[nn].frameTime;
            for (count = s->numFrames; count > 0; count-- ) {
                nn += 1;
                if (nn >= MAX_FRAME_STATS)
                    nn -= MAX_FRAME_STATS;
                double render = s->frames[nn].renderTime;
                if (render < minRender) minRender = render;
                if (render > maxRender) maxRender = render;
                double frame = s->frames[nn].frameTime;
                if (frame < minFrame) minFrame = frame;
                if (frame > maxFrame) maxFrame = frame;
                avgRender += render;
                avgFrame  += frame;
            }
            avgRender /= s->numFrames;
            avgFrame  /= s->numFrames;

            LOGI("frame/s (avg,min,max) = (%.1f,%.1f,%.1f) "
                 "render time ms (avg,min,max) = (%.1f,%.1f,%.1f)\n",
                 1000./avgFrame, 1000./maxFrame, 1000./minFrame,
                 avgRender, minRender, maxRender);
        }
        s->numFrames  = 0;
        s->firstFrame = 0;
        s->firstTime  = now;
    }

    nn = s->firstFrame + s->numFrames;
    if (nn >= MAX_FRAME_STATS)
        nn -= MAX_FRAME_STATS;

    s->frames[nn].renderTime = renderTime;
    s->frames[nn].frameTime  = frameTime;

    if (s->numFrames < MAX_FRAME_STATS) {
        s->numFrames += 1;
    } else {
        s->firstFrame += 1;
        if (s->firstFrame >= MAX_FRAME_STATS)
            s->firstFrame -= MAX_FRAME_STATS;
    }

    s->lastTime = now;
}

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
	if (appRunning == 1) {
		resizeSpoutGLES(width, height);
	}
}

// Draw
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativeDraw
  (JNIEnv *env, jclass c) {
	if (appRunning == 1) {
		static Stats       stats;
		static int         init;

		if (!init) {
			stats_init(&stats);
			init = 1;
		}

		stats_startFrame(&stats);

		stepSpoutGLES();

		stats_endFrame(&stats);
	}
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

// Filter
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutFilter
  (JNIEnv *env, jclass c, jboolean applyFilter) {
	if (applyFilter) {
		filter = GL_LINEAR;
	} else {
		filter = GL_NEAREST;
	}
}

// Vibrate
JNIEXPORT
jboolean JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutVibrate
  (JNIEnv *env, jclass c) {
	return (jboolean)vibrate_now;
}

// Sound
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutSetSound
  (JNIEnv *env, jclass c, jboolean sound) {
	if (sound) {
		sound_on = 1;
	} else {
		sound_on = 0;
	}
}

// Color
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutSetColor
  (JNIEnv *env, jclass c, jboolean color) {
	if (color) {
		color_on = 1;
	} else {
		color_on = 0;
	}
}

// Tail
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutSetTail
  (JNIEnv *env, jclass c, jboolean tail) {
	if (tail) {
		tail_on = 1;
	} else {
		tail_on = 0;
	}
}

// Display offset X
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutDisplayOffsetX
  (JNIEnv *env, jclass c, jint offset_x) {
	dis_x = offset_x;
}

// Display offset Y
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutDisplayOffsetY
  (JNIEnv *env, jclass c, jint offset_y) {
	dis_y = offset_y;
}

// Push Score
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutNativePushScore
  (JNIEnv *env, jclass c, jint scoreHeight, jint scoreScore) {
	score_score = scoreScore;
	score_height = scoreHeight;
}

// Get ScoreScores
JNIEXPORT
jint JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutGetScoreScores
  (JNIEnv *env, jclass c) {
	return (jint)getScoreScores();
}

// Get ScoreHeight
JNIEXPORT
jint JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutGetScoreHeight
  (JNIEnv *env, jclass c) {
	return (jint)getScoreHeight();
}

// Initialize Pointer
JNIEXPORT
void JNICALL Java_ru_exlmoto_spout_SpoutNativeLibProxy_SpoutInitilizeGlobalJavaEnvPointer
  (JNIEnv *env, jclass c) {

	LOGI("Initialize pointer...");

	if (!javaEnviron) {
		javaEnviron = env;
	}
}
