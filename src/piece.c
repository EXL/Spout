#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <math.h>

#include <SDL/SDL.h>
#include <SDL/SDL_opengl.h>

#include "piece.h"
#include "spout.h"
#include "font.h"

#define GP2X_BUTTON_UP              (0)
#define GP2X_BUTTON_DOWN            (4)
#define GP2X_BUTTON_LEFT            (2)
#define GP2X_BUTTON_RIGHT           (6)
#define GP2X_BUTTON_UPLEFT          (1)
#define GP2X_BUTTON_UPRIGHT         (7)
#define GP2X_BUTTON_DOWNLEFT        (3)
#define GP2X_BUTTON_DOWNRIGHT       (5)
#define GP2X_BUTTON_CLICK           (18)
#define GP2X_BUTTON_A               (12)
#define GP2X_BUTTON_B               (13)
#define GP2X_BUTTON_X               (14)
#define GP2X_BUTTON_Y               (15)
#define GP2X_BUTTON_L               (10)
#define GP2X_BUTTON_R               (11)
#define GP2X_BUTTON_START           (8)
#define GP2X_BUTTON_SELECT          (9)
#define GP2X_BUTTON_VOLUP           (16)
#define GP2X_BUTTON_VOLDOWN         (17)


unsigned char joykeys[256];

SDL_Surface *video;

static GLuint global_texture = 0;

unsigned char *vBuffer = NULL;

unsigned char pixelData[SDL_WIDTH * SDL_HEIGHT];
unsigned short testP[SDL_WIDTH * SDL_HEIGHT];

static GLfloat texcoord[4];

void pceLCDDispStop ()
{
}

void pceLCDDispStart ()
{
}

void SDL_GL_Enter2DMode()
{
	SDL_Surface *screen = SDL_GetVideoSurface();

	/* Note, there may be other things you need to change,
	   depending on how you have your OpenGL state set up.
	*/
	//glPushAttrib(GL_ENABLE_BIT);
	//glDisable(GL_DEPTH_TEST);
	//glDisable(GL_CULL_FACE);
	glEnable(GL_TEXTURE_2D);

	/* This allows alpha blending of 2D textures with the scene */
	//glEnable(GL_BLEND);
	//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	//glViewport(0, 0, screen->w, screen->h);

	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();

	glOrtho(0.0, (GLdouble)screen->w, (GLdouble)screen->h, 0.0, 0.0, 1.0);

	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
	glLoadIdentity();

	//glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL);
}

void SDL_GL_Leave2DMode()
{
	glMatrixMode(GL_MODELVIEW);
	glPopMatrix();

	glMatrixMode(GL_PROJECTION);
	glPopMatrix();

	glPopAttrib();
}

GLuint SDL_GL_LoadTexture_fromPixelData(int w, int h, GLfloat *texcoord, void *pixels)
{
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

void initSDL () {
    int i;
    if (SDL_Init (SDL_INIT_JOYSTICK | SDL_INIT_VIDEO) < 0) {
        fprintf (stderr, "Couldn't initialize SDL: %s\n", SDL_GetError ());
        exit (1);
    }

    SDL_JoystickOpen(0);
    atexit (SDL_Quit);

    video = SDL_SetVideoMode (SDL_WIDTH, SDL_HEIGHT, 16, SDL_OPENGL | SDL_SWSURFACE);
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

    SDL_ShowCursor (0);

    if (!global_texture) {
        global_texture = SDL_GL_LoadTexture_fromPixelData(SDL_WIDTH, SDL_HEIGHT, texcoord, video->pixels);
    }

    for (i=0;i<256;i++)
        joykeys[i] = 0;
}

int rgb(unsigned char r, unsigned char g, unsigned char b) {
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
    const int zoom = 2;

    const int offsetx = SDL_WIDTH/2 - 128*zoom/2;
    const int offsety = SDL_HEIGHT/2 - 88*zoom/2;

    bi = pixelData;

    w = SDL_WIDTH;
    h = SDL_HEIGHT;

    for (y = 0; y < (88*zoom); y++) {
        vbi = vBuffer +  (y/zoom) * 128;  //the actual line on the pce internal buffer (128x88)
        bline = bi + SDL_WIDTH * (y + offsety);
        bline += offsetx;
        for (x = 0; x < (128*zoom); x++) {
            *bline++ = *(vbi + x/zoom);
        }
    }

    // Convert buffer to RGB565
    int rz;
    for (rz = 0; rz < SDL_HEIGHT * SDL_WIDTH; ++rz) {
        switch (pixelData[rz]) {
        case 0x1:
            testP[rz] = 0xAD55;
            break;
        case 0x2:
            testP[rz] = 0x52AA; //
            break;
        case 0x3:
            testP[rz] = 0x0000; // Black 00000 000000 00000
            break;
        case 0x0:
            testP[rz] = 0xFFFF; // White 11111 111111 11111
            break;
        }
    }


    SDL_GL_Enter2DMode();

    GLfloat texMinX = texcoord[0];
    GLfloat texMinY = texcoord[1];
    GLfloat texMaxX = texcoord[2];
    GLfloat texMaxY = texcoord[3];

    int x_coord = 0;
    int y_coord = 0;

    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, w, h, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, testP);
    glBegin(GL_TRIANGLE_STRIP);
    glTexCoord2f(texMinX, texMinY); glVertex2i(x_coord,   y_coord  );
    glTexCoord2f(texMaxX, texMinY); glVertex2i(x_coord+w, y_coord  );
    glTexCoord2f(texMinX, texMaxY); glVertex2i(x_coord,   y_coord+h);
    glTexCoord2f(texMaxX, texMaxY); glVertex2i(x_coord+w, y_coord+h);
    glEnd();

    SDL_GL_Leave2DMode();

    SDL_GL_SwapBuffers();
}

unsigned char *keys;

int pcePadGet () {
    static int pad = 0;
    int i = 0, op = pad & 0x00ff;

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

    int p[] = {
        PAD_UP, PAD_DN, PAD_LF, PAD_RI,
        PAD_LF, PAD_RI,
        PAD_A, PAD_B, PAD_A, PAD_B,
        PAD_C, PAD_D, PAD_D, -1
    };


    int gp[] = {
        GP2X_BUTTON_UP,
        GP2X_BUTTON_DOWN,
        GP2X_BUTTON_LEFT, GP2X_BUTTON_UPLEFT, GP2X_BUTTON_DOWNLEFT, GP2X_BUTTON_RIGHT, GP2X_BUTTON_UPRIGHT, GP2X_BUTTON_DOWNRIGHT,
        GP2X_BUTTON_A, GP2X_BUTTON_B, GP2X_BUTTON_X, GP2X_BUTTON_Y, GP2X_BUTTON_START
        -1
    };

    int gpp[] = {
        PAD_UP, PAD_DN, PAD_LF, PAD_LF, PAD_LF, PAD_RI, PAD_RI, PAD_RI, PAD_B, PAD_B, PAD_B, PAD_B, PAD_D
    };

    pad = 0;

    do {
        if (keys[k[i]] == SDL_PRESSED) {
            pad |= p[i];
        }
        i++;
    } while (p[i] >= 0);

    i=0;
    for (i=0;i<13;i++) {
        if (joykeys[gp[i]] == 1) {
            pad |= gpp[i];
        }
    }


    pad |= (pad & (~op)) << 8;

    return pad;
}

int interval = 0;

void pceAppSetProcPeriod (int period) {
    interval = period;
}

int exec = 1;

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

int font_posX = 0, font_posY = 0, font_width = 4, font_height = 6;
unsigned char font_fgcolor = 3, font_bgcolor = 0, font_bgclear = 0;
const char *font_adr = (const char *)FONT6;

void pceFontSetType (int type)
{
    const int width[] = { 5, 8, 4};
    const int height[] = { 10, 16, 6};
    const char *adr[] = { (const char *)FONT6, (const char *)FONT16, (const char *)FONT6};

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

int pceFileOpen (FILEACC * pfa, const char *fname, int mode)
{
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

int pceFileReadSct (FILEACC * pfa, void *ptr, /*int sct,*/ int len)
{
    return read (*pfa, ptr, len);
}

int pceFileWriteSct (FILEACC * pfa, const void *ptr, /*int sct,*/ int len)
{
    return write (*pfa, ptr, len);
}

int pceFileClose (FILEACC * pfa)
{
    close (*pfa);
    return 0;
}



int main (int argc, char *argv[])
{
    SDL_Event event;
    int nextTick, wait;
    int cnt = 0;
    int pzoom;
    char *tail;
    zoom = 1;
    fullscreen = 0;
    if (argc > 1) {
        if (strcmp (argv[1], "f") == 0)
            fullscreen = 1;
        else {
            pzoom = strtol (argv[1], &tail, 0);
            if (pzoom >= 1)
                zoom = pzoom;
        }
    }

    initSDL ();
    pceAppInit ();

    //SDL_WM_SetCaption (PACKAGE_STRING, NULL);

    nextTick = SDL_GetTicks () + interval;
    while (exec) {
        keys = SDL_GetKeyState (NULL);

        while (SDL_PollEvent(&event)) {
            switch (event.type) {
            case SDL_JOYBUTTONDOWN:
                if (event.jbutton.button == GP2X_BUTTON_SELECT)
                    exec = 0;
                joykeys[event.jbutton.button] = 1;
                break;

            case SDL_JOYBUTTONUP:
                joykeys[event.jbutton.button] = 0;
                break;

            case SDL_KEYDOWN:
                if (event.key.keysym.sym == SDLK_ESCAPE)
                    exec = 0;
                break;
                // other event types
            }
        }



        wait = nextTick - SDL_GetTicks ();
        if (wait > 0) {
            SDL_Delay (wait);
        }

        pceAppProc ();
        //      SDL_Flip(video);

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

#ifdef GP2X
    chdir("/usr/gp2x");
    execl("/usr/gp2x/gp2xmenu", "/usr/gp2x/gp2xmenu", NULL);
#endif

    return 0;
}
