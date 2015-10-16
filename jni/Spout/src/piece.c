#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <math.h>

#ifndef ANDROID_NDK
#include <SDL/SDL.h>
#include <SDL/SDL_opengl.h>
#else
#include <GLES/gl.h>
#include <GLES/glext.h>

#include "../../SpoutNativeLibProxy.h" // For Keys
#endif // ANDROID_NDK

#include "piece.h"
#include "spout.h"
#include "font.h"

#ifndef ANDROID_NDK
SDL_Surface *video;
#else
// TODO: ????
unsigned int *allDisplayPixelMap;
#endif // ANDROID_NDK

static GLuint global_texture = 0;
static GLfloat texcoord[4];

unsigned char *vBuffer = NULL;
unsigned char pixelData[TEXTURE_WIDTH * TEXTURE_HEIGHT];
unsigned short testP[TEXTURE_WIDTH * TEXTURE_HEIGHT];

unsigned char *keys;

// 128 256 384 512
// 88  176 264 352

int display_w = 640;
int display_h = 480;
int x_off = 20;
int y_off = 20;
int fullscreen = 0;

int interval = 0;
int exec = 1;

int font_posX = 0, font_posY = 0, font_width = 4, font_height = 6;
unsigned char font_fgcolor = 3, font_bgcolor = 0, font_bgclear = 0;
const char *font_adr = (const char *)FONT6;

/***********************************************/

void pceLCDDispStop () { }

void pceLCDDispStart () { }

void SDL_GL_Enter2DMode () {
#ifndef ANDROID_NDK
	SDL_Surface *screen = SDL_GetVideoSurface();

	/* Note, there may be other things you need to change,
	   depending on how you have your OpenGL state set up.
	*/
	glPushAttrib(GL_ENABLE_BIT);
#endif // ANDROID_NDK
	glDisable(GL_DEPTH_TEST);
	glDisable(GL_CULL_FACE);
	glEnable(GL_TEXTURE_2D);

	/* This allows alpha blending of 2D textures with the scene */
	//glEnable(GL_BLEND);
	//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	//glViewport(0, 0, screen->w, screen->h);

	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();

#ifndef ANDROID_NDK
	glOrtho(0.0, (GLdouble)screen->w, (GLdouble)screen->h, 0.0, 0.0, 1.0);
#else
	glOrthof(0.0, display_w, display_h, 0.0, 0.0, 1.0);
#endif // ANDROID_NDK
	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();

	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
}

void SDL_GL_Leave2DMode () {
	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();

	glMatrixMode(GL_PROJECTION);
	glPopMatrix();
#ifndef ANDROID_NDK
	glPopAttrib();
#endif // ANDROID_NDK
}

GLuint SDL_GL_LoadTexture_fromPixelData (int w, int h, GLfloat *texcoord, void *pixels) {
    GLuint texture;

    texcoord[0] = 0.0f;         /* Min X */
    texcoord[1] = 0.0f;         /* Min Y */
    texcoord[2] = 1.0f;         /* Max X */
    texcoord[3] = 1.0f;         /* Max Y */

    glGenTextures(1, &texture);
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);

    return texture;
}

#ifndef ANDROID_NDK
void initSDL () {
    if (SDL_Init (SDL_INIT_VIDEO) < 0) {
        fprintf (stderr, "Couldn't initialize SDL: %s\n", SDL_GetError ());
        exit (1);
    }

    atexit (SDL_Quit);

    Uint32 flags = SDL_OPENGL | SDL_SWSURFACE;
    if (fullscreen) {
        flags |= SDL_FULLSCREEN;
    }

    video = SDL_SetVideoMode (display_w, display_h, 16, flags);
    if (video == NULL) {
        fprintf (stderr, "Couldn't set video mode: %s\n", SDL_GetError ());
        exit (1);
    }

    /* OpenGL Init */
    SDL_GL_SetAttribute( SDL_GL_RED_SIZE, 3 );
    SDL_GL_SetAttribute( SDL_GL_GREEN_SIZE, 3 );
    SDL_GL_SetAttribute( SDL_GL_BLUE_SIZE, 2 );
    SDL_GL_SetAttribute( SDL_GL_DEPTH_SIZE, 16 );
    SDL_GL_SetAttribute( SDL_GL_DOUBLEBUFFER, 1 );
    SDL_GL_SetAttribute( SDL_GL_SWAP_CONTROL, 0 ); // ?

	glClearColor( 255.0, 255.0, 255.0, 1.0 );
	glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    SDL_ShowCursor (0);

    if (!global_texture) {
        global_texture = SDL_GL_LoadTexture_fromPixelData(TEXTURE_WIDTH, TEXTURE_HEIGHT, texcoord, video->pixels);
    }
}
#else
void initSpoutGLES() {

	pceAppInit ();

	// TODO: ???
	allDisplayPixelMap = (int *)malloc(TEXTURE_WIDTH * TEXTURE_HEIGHT);

	if (!global_texture) {
		global_texture = SDL_GL_LoadTexture_fromPixelData(TEXTURE_WIDTH, TEXTURE_HEIGHT, texcoord, allDisplayPixelMap);
	}

	glClearColor( 255.0, 255.0, 255.0, 1.0 );
	glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	reshapeSpoutGLES(display_w, display_h);
}

void deinitSpoutGLES() {
	pceAppExit ();

	if (global_texture) {
		glDeleteTextures(1, &global_texture);
		global_texture = 0;
	}

	free(allDisplayPixelMap);
}

void reshapeSpoutGLES(int w, int h) {
	glViewport(0, 0, (GLint) w, (GLint) h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}
#endif // !ANDROID_NDK

int rgb (unsigned char r, unsigned char g, unsigned char b) {
    unsigned char red = r >> 3;
    unsigned char green = g >> 2;
    unsigned char blue = b >> 3;

    int result = (red << (5 + 6)) | (green << 5) | blue;

    printf("red: %x\n", red);
    printf("green: %x\n", green);
    printf("blue: %x\n", blue);
    printf("result: %x\n", result);

    return result;
}

void pceLCDTrans () {
    static int w, h;
    int x, y;
    unsigned char *vbi, *bi;
    unsigned char *bline;
    const int zoom = 1;

    const int offsetx = TEXTURE_WIDTH/2 - 128*zoom/2;
    const int offsety = TEXTURE_HEIGHT/2 - 88*zoom/2;

    bi = pixelData;

    w = TEXTURE_WIDTH;
    h = TEXTURE_HEIGHT;

    for (y = 0; y < (88*zoom); y++) {
        vbi = vBuffer +  (y/zoom) * 128;  //the actual line on the pce internal buffer (128x88)
        bline = bi + TEXTURE_WIDTH * (y + offsety);
        bline += offsetx;
        for (x = 0; x < (128*zoom); x++) {
            *bline++ = *(vbi + x/zoom);
        }
    }

    // Convert buffer to RGB565
    int rz;
    for (rz = 0; rz < TEXTURE_HEIGHT * TEXTURE_WIDTH; ++rz) {
        switch (pixelData[rz]) {
        case 0x1:
            testP[rz] = 0xAD55; // Gray #1
            break;
        case 0x2:
            testP[rz] = 0x52AA; // Gray #2
            break;
        case 0x3:
            testP[rz] = 0x0000; // Black 00000 000000 00000
            break;
        case 0x0:
            testP[rz] = 0xFFFF; // White 11111 111111 11111
            break;
        }
    }

    glClearColor( 255.0, 255.0, 255.0, 1.0 );
    glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    SDL_GL_Enter2DMode();

    GLfloat texMinX = texcoord[0];
    GLfloat texMinY = texcoord[1];
    GLfloat texMaxX = texcoord[2];
    GLfloat texMaxY = texcoord[3];

    int x_coord = 0;
    int y_coord = 0;

    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, testP);

#ifndef ANDROID_NDK
    glBegin(GL_TRIANGLE_STRIP);
    glTexCoord2f(texMinX, texMinY); glVertex2i(x_coord + x_off,                   y_coord + y_off );
    glTexCoord2f(texMaxX, texMinY); glVertex2i(x_coord + display_w - x_off * 2,   y_coord + y_off );
    glTexCoord2f(texMinX, texMaxY); glVertex2i(x_coord + x_off,                   y_coord + display_h - y_off * 2);
    glTexCoord2f(texMaxX, texMaxY); glVertex2i(x_coord + display_w - x_off * 2,   y_coord + display_h - y_off * 2);
    glEnd();
#else
    GLfloat texC[] = {
        texMinX, texMinY,
        texMaxX, texMinY,
        texMinX, texMaxY,
        texMaxX, texMaxY
    };

    GLfloat vtxC[] = {
        x_coord + x_off, y_coord + y_off, 0,
        x_coord + display_w - x_off * 2, y_coord + y_off, 0,
        x_coord + x_off, y_coord + display_h - y_off * 2, 0,
        x_coord + display_w - x_off * 2, y_coord + display_h - y_off * 2, 0
    };

    glEnableClientState(GL_VERTEX_ARRAY);
    glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    glVertexPointer(3, GL_FLOAT, 0, vtxC);
    glTexCoordPointer(2, GL_FLOAT, 0, texC);
    glDrawArrays(GL_TRIANGLE_FAN, 0, 4);

    glDisableClientState(GL_VERTEX_ARRAY);
    glDisableClientState(GL_TEXTURE_COORD_ARRAY);
#endif // ANDROID_NDK

    SDL_GL_Leave2DMode();
#ifndef ANDROID_NDK
    SDL_GL_SwapBuffers();
#else
    //TODO: swapbuffers ???
    glEnable(GL_TEXTURE_2D);
    glActiveTexture(GL_TEXTURE0);
    glClientActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, global_texture);
    glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    glEnable(GL_BLEND);
    glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnableClientState(GL_COLOR_ARRAY);

#endif // !ANDROID_NDK
}

int pcePadGet () {
    static int pad = 0;
    int i = 0, op = pad & 0x00ff;

#ifndef ANDROID_NDK
    int k[] = {
        SDLK_PAGEUP, SDLK_PAGEDOWN, SDLK_LEFT, SDLK_RIGHT,
        SDLK_KP4, SDLK_KP6,
    #ifdef TARGET_PANDORA
        SDLK_PAGEUP, SDLK_PAGEDOWN, SDLK_HOME, SDLK_END,
    #else
        SDLK_x, SDLK_z, SDLK_SPACE, SDLK_RETURN,
    #endif
        SDLK_ESCAPE, SDLK_LSHIFT, SDLK_RSHIFT,
        SDLK_PLUS, SDLK_MINUS
    };
#else
    int k[] = {
        KEY_UP, KEY_DOWN, KEY_LEFT, KEY_RIGHT,
        KEY_LEFT, KEY_RIGHT,
        KEY_FIRE, KEY_FIRE, KEY_FIRE, KEY_FIRE,
		KEY_PAUSE, KEY_QUIT, KEY_UNKNOWN,
		KEY_FIRE, KEY_FIRE
    };
#endif // !ANDROID_NDK

    int p[] = {
        PAD_UP, PAD_DN, PAD_LF, PAD_RI,
        PAD_LF, PAD_RI,
        PAD_A, PAD_B, PAD_A, PAD_B,
        PAD_C, PAD_D, PAD_D, -1
    };

    pad = 0;

    do {
#ifndef ANDROID_NDK
        if (keys[k[i]] == SDL_PRESSED) {
#else
        if (keys[k[i]] == 1) { // TODO: check this
#endif // !ANDROID_NDK
            pad |= p[i];
        }
        i++;
    } while (p[i] >= 0);

    pad |= (pad & (~op)) << 8;

    return pad;
}

void pceAppSetProcPeriod (int period) {
    interval = period;
}

void pceAppReqExit (/*int c*/) {
    exec = 0;
}

unsigned char * pceLCDSetBuffer (unsigned char *pbuff)
{
    if (pbuff) {
        vBuffer = pbuff;
    }
    return vBuffer;
}

void pceFontSetType (int type)
{
    const int width[] = { 5, 8, 4};
    const int height[] = { 10, 16, 6};
    const char *adr[] = { (const char *)FONT6, (const char *)FONT16, (const char *)FONT6 };

    type &= 3;
    font_width = width[type];
    font_height = height[type];
    font_adr = adr[type];
}

void pceFontSetTxColor (int color)
{
    font_fgcolor = (unsigned char) color;
}

void pceFontSetBkColor (int color)
{
    if (color >= 0) {
        font_bgcolor = (unsigned char) color;
        font_bgclear = 0;
    } else {
        font_bgclear = 1;
    }
}

void pceFontSetPos (int x, int y)
{
    font_posX = x;
    font_posY = y;
}

void pceFontPrintf (const char *fmt, ...)
{
    unsigned char *adr = vBuffer + font_posX + font_posY * 128;
    unsigned char *pC;
    char c[1024];
    va_list argp;

    va_start (argp, fmt);
    vsprintf (c, fmt, argp);
    va_end (argp);

    pC = (unsigned char*)c;
    while (*pC) {
        int i, x, y;
        const unsigned char *sAdr;
        if (*pC >= 0x20 && *pC < 0x80) {
            i = *pC - 0x20;
        } else {
            i = 0;
        }
        sAdr = (const unsigned char *)font_adr + (i & 15) + (i >> 4) * 16 * 16;
        for (y = 0; y < font_height; y++) {
            unsigned char c = *sAdr;
            for (x = 0; x < font_width; x++) {
                if (c & 0x80) {
                    *adr = font_fgcolor;
                } else if (font_bgclear == 0) {
                    *adr = font_bgcolor;
                }
                adr++;
                c <<= 1;
            }
            adr += 128 - font_width;
            sAdr += 16;
        }
        adr -= 128 * font_height - font_width;
        pC++;
    }
}

#ifndef ANDROID_NDK
int pceFileOpen (FILEACC * pfa, const char *fname, int mode)
{
    fprintf(stderr, "Openning file: %s\n", fname);

    if (mode == FOMD_RD) {
        *pfa = open (fname, O_RDONLY);
    } else if (mode == FOMD_WR) {
        *pfa = open (fname, O_CREAT | O_RDWR | O_TRUNC, S_IREAD | S_IWRITE);
    }

    if (*pfa >= 0) {
        return 0;
    } else {
        return 1;
    }
}
#endif // !ANDROID NDK

void pceFileReadSct (void *ptr, /*int sct,*/ int len)
{
    fprintf(stderr, "Getting score, length: %d\n", len);

    int *toScrore = (int *)(ptr);
    toScrore[0] = 55;
    toScrore[1] = 77;
}

void pceFileWriteSct (const void *ptr, /*int sct,*/ int len)
{
    const int *hiScore = (const int *)(ptr);
    fprintf(stderr, "Pushing score: %d and %d, Len: %d\n", hiScore[0], hiScore[1], len);
}

int pceFileClose (FILEACC * pfa)
{
    close (*pfa);
    return 0;
}

#ifndef ANDROID_NDK
int main (/*int argc, char *argv[]*/)
{
    SDL_Event event;
    int nextTick, wait;
    int cnt = 0;

    initSDL ();
    pceAppInit ();

    nextTick = SDL_GetTicks () + interval;
    while (exec) {
        keys = SDL_GetKeyState (NULL);

        while (SDL_PollEvent(&event)) { }

        wait = nextTick - SDL_GetTicks ();
        if (wait > 0) {
            SDL_Delay (wait);
        }

        pceAppProc ();

        nextTick += interval;
        cnt++;

        if ((keys[SDLK_ESCAPE] == SDL_PRESSED && (keys[SDLK_LSHIFT] == SDL_PRESSED || keys[SDLK_RSHIFT] == SDL_PRESSED)) || event.type == SDL_QUIT) {
            exec = 0;
        }
    }

    pceAppExit ();

    if (global_texture) {
        glDeleteTextures(1, &global_texture);
        global_texture = 0;
    }

    return 0;
}
#endif // !ANDROID_NDK
