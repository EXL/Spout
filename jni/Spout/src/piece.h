#ifndef PIECE_H
#define PIECE_H

#ifndef ANDROID_NDK
#include <SDL/SDL.h>
#endif // ANDROID_NDK

#ifdef _WIN32
#   pragma comment(lib, "SDL.lib")
#   pragma comment(lib, "SDLmain.lib")
#else
#	include <unistd.h>
#endif
#include <string.h>
#include "config.h"

#define TEXTURE_WIDTH (128)
#define TEXTURE_HEIGHT (88)

#define PAD_RI 0x01
#define PAD_LF 0x02
#define PAD_DN 0x04
#define PAD_UP 0x08
#define PAD_B  0x10
#define PAD_A  0x20
#define PAD_D  0x40
#define PAD_C  0x80

#define TRG_RI 0x0100
#define TRG_LF 0x0200
#define TRG_DN 0x0400
#define TRG_UP 0x0800
#define TRG_B  0x1000
#define TRG_A  0x2000
#define TRG_D  0x4000
#define TRG_C  0x8000

#define CPU_SPEED_NORMAL 0

#define FILEACC int
#define FOMD_RD 0
#define FOMD_WR 1
#define pceFileCreate

#ifndef ANDROID_NDK
#define pceTimerGetCount SDL_GetTicks
#else
// TODO: check this
#define pceTimerGetCount time
#endif // ANDROID_NDK

#define PP_MODE_SINGLE 0
#define PP_MODE_REPEAT 1

void pceFontPrintf(const char *fmt, ...);
void pceFontSetTxColor (int color);
void pceFontSetBkColor (int color);
void pceFontSetPos (int x, int y);
void pceFontSetType (int type);

void pceLCDDispStop ();
void pceLCDDispStart ();
unsigned char *pceLCDSetBuffer (unsigned char *pbuff);
void pceLCDTrans ();

void pceAppSetProcPeriod (int period);
void pceAppReqExit ();

int pceFileOpen (FILEACC * pfa, const char *fname, int mode);
void pceFileReadSct(void *ptr, int len);
void pceFileWriteSct(const void *ptr, int len);
int pceFileClose (FILEACC * pfa);

int pcePadGet ();

void initSpoutGLES();
void deinitSpoutGLES();
void reshapeSpoutGLES(int w, int h);
void stepSpoutGLES();
#endif // PIECE_H
