#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <math.h>
#include <unistd.h>

#include <SDL2/SDL.h>

#include "piece.h"
#include "font.h"

SDL_Surface *video;
SDL_Surface *layer;
SDL_Renderer *render;
SDL_Texture *texture;
SDL_Rect layerRect;

unsigned char *vBuffer = NULL;

void pceLCDDispStop()
{
}

void pceLCDDispStart()
{
}

void initSDL() {
	SDL_PixelFormat *pfrm;

	SDL_Window *window = SDL_CreateWindow(
		"Spout",
		SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
		SDL_WIDTH * 3, SDL_HEIGHT * 3,
		SDL_WINDOW_SHOWN | SDL_WINDOW_RESIZABLE
	);
	if (window == NULL) {
		SDL_Log("SDL_CreateWindow failed: %s", SDL_GetError());
		exit(EXIT_FAILURE);
	}

	atexit(SDL_Quit);

	render = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED);
	if (render == NULL) {
		SDL_Log("SDL_CreateRenderer failed: %s", SDL_GetError());
		exit(EXIT_FAILURE);
	}

	video = SDL_CreateRGBSurface(0, SDL_WIDTH, SDL_HEIGHT, 32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);
	if (video == NULL) {
		SDL_Log("SDL_CreateRGBSurface failed: %s", SDL_GetError());
		exit(EXIT_FAILURE);
	}

	layer = SDL_CreateRGBSurface(0, SDL_WIDTH, SDL_HEIGHT, 8, 0x00, 0x00, 0x00, 0x00);
	if (layer == NULL) {
		SDL_Log("SDL_CreateRGBSurface failed: %s", SDL_GetError());
		exit(EXIT_FAILURE);
	}

	layerRect.x = 0;
	layerRect.y = 0;
	layerRect.w = SDL_WIDTH;
	layerRect.h = SDL_HEIGHT;

	{
		static SDL_Color pltTbl[4] = {
			{255, 255, 255},
			{170, 170, 170},
			{85, 85, 85},
			{0, 0, 0}
		};
		SDL_SetPaletteColors(layer->format->palette, pltTbl, 0, sizeof(pltTbl));
	}

	texture = SDL_CreateTextureFromSurface(render, layer);
	if (texture == NULL) {
		SDL_Log("SDL_CreateTextureFromSurface failed: %s", SDL_GetError());
		exit(EXIT_FAILURE);
	}
}

void pceLCDTrans() {
	int x, y;
	unsigned char *vbi, *bi;

	bi = layer->pixels;
	for(y = 0; y < SDL_HEIGHT; y ++) {
		vbi = vBuffer + (y / ZOOM) * 128;
		for(x = 0; x < SDL_WIDTH; x ++) {
			*bi ++ = *(vbi + x / ZOOM);
		}
		bi += layer->pitch - SDL_WIDTH;
	}

	SDL_BlitSurface(layer, NULL, video, &layerRect);
	SDL_UpdateTexture(texture, &layerRect, video->pixels, video->pitch);
	SDL_RenderCopy(render, texture, NULL, NULL);
	SDL_RenderPresent(render);
}

const unsigned char *keys;

int pcePadGet() {
	static int pad = 0;
	int i = 0, op = pad & 0x00ff;

	int k[] = {
		SDL_SCANCODE_UP,	SDL_SCANCODE_DOWN,	SDL_SCANCODE_LEFT,	SDL_SCANCODE_RIGHT,
		SDL_SCANCODE_KP_8,	SDL_SCANCODE_KP_2,	SDL_SCANCODE_KP_4,	SDL_SCANCODE_KP_6,
		SDL_SCANCODE_X,		SDL_SCANCODE_Z,		SDL_SCANCODE_SPACE,	SDL_SCANCODE_RETURN,
		SDL_SCANCODE_ESCAPE,	SDL_SCANCODE_LSHIFT,	SDL_SCANCODE_RSHIFT
	};

	int p[] = {
		PAD_UP,			PAD_DN,			PAD_LF,			PAD_RI,
		PAD_UP,			PAD_DN,			PAD_LF,			PAD_RI,
		PAD_A,			PAD_B,			PAD_A,			PAD_B,
		PAD_C,			PAD_D,			PAD_D,
		-1
	};

	pad = 0;

	do {
		if(keys[k[i]] == SDL_PRESSED) {
			pad |= p[i];
		}
		i ++;
	} while(p[i] >= 0);

	pad |= (pad & (~op)) << 8;

	return pad;
}

int interval = 0;

void pceAppSetProcPeriod(int period) {
	interval = period;
}

int exec = 1;

void pceAppReqExit(int c) {
	exec = 0;
}

unsigned char *pceLCDSetBuffer(unsigned char *pbuff)
{
	if(pbuff) {
		vBuffer = pbuff;
	}
	return vBuffer;
}

int font_posX = 0, font_posY = 0, font_width = 4, font_height = 6;
unsigned char font_fgcolor = 3, font_bgcolor = 0, font_bgclear = 0;
const char *font_adr = FONT6;

void pceFontSetType(int type)
{
	const int width[] = {5, 8, 4};
	const int height[] = {10, 16, 6};
	const char* adr[] ={FONT6, FONT16, FONT6};

	type &= 3;
	font_width = width[type];
	font_height = height[type];
	font_adr = adr[type];
}

void pceFontSetTxColor(int color)
{
	font_fgcolor = (unsigned char)color;
}

void pceFontSetBkColor(int color)
{
	if(color >= 0) {
		font_bgcolor = (unsigned char)color;
		font_bgclear = 0;
	} else {
		font_bgclear = 1;
	}
}

void pceFontSetPos(int x, int y)
{
	font_posX = x;
	font_posY = y;
}

int pceFontPrintf(const char *fmt, ...)
{
	unsigned char *adr = vBuffer + font_posX + font_posY * 128;
	unsigned char *pC;
	char c[1024];
	va_list argp;

	va_start(argp, fmt);
	vsprintf(c, fmt, argp);
	va_end(argp);

	pC = c;
	while(*pC) {
		int i, x, y;
		const unsigned char *sAdr;
		if(*pC >= 0x20 && *pC < 0x80) {
			i = *pC - 0x20;
		} else {
			i = 0;
		}
		sAdr = font_adr + (i & 15) + (i >> 4) * 16 * 16;
		for(y = 0; y < font_height; y ++) {
			unsigned char c = *sAdr;
			for(x = 0; x < font_width; x ++) {
				if(c & 0x80) {
					*adr = font_fgcolor;
				} else if(font_bgclear == 0) {
					*adr = font_bgcolor;
				}
				adr ++;
				c <<= 1;
			}
			adr += 128 - font_width;
			sAdr += 16;
		}
		adr -= 128 * font_height - font_width;
		pC ++;
	}
	return 0;
}

int pceFileOpen(FILEACC *pfa, const char *fname, int mode)
{
	if(mode == FOMD_RD) {
		*pfa = open(fname, O_RDONLY);
	} else if(mode == FOMD_WR) {
		*pfa = open(fname, O_CREAT | O_RDWR | O_TRUNC, S_IREAD | S_IWRITE);
	}

	if(*pfa >= 0) {
		return 0;
	} else {
		return 1;
	}
}

int pceFileReadSct(FILEACC *pfa, void *ptr, int sct, int len)
{
	return read(*pfa, ptr, len);
}

int pceFileWriteSct(FILEACC *pfa, const void *ptr, int sct, int len)
{
	return write(*pfa, ptr, len);
}

int pceFileClose(FILEACC *pfa)
{
	close(*pfa);
	return 0;
}

int main(int argc, char *argv[])
{
	SDL_Event event;
	long nextTick, wait;
	int cnt = 0;

	initSDL();
	pceAppInit();

	nextTick = SDL_GetTicks() + interval;
	while(exec) {
		SDL_PollEvent(&event);
		keys = SDL_GetKeyboardState(NULL);

		wait = nextTick - SDL_GetTicks();
		if(wait > 0) {
			SDL_Delay(wait);
		}

		pceAppProc(cnt);
	//	SDL_Flip(video);

		nextTick += interval;
		cnt ++;

		if((keys[SDL_SCANCODE_ESCAPE] == SDL_PRESSED && (keys[SDL_SCANCODE_LSHIFT] == SDL_PRESSED || keys[SDL_SCANCODE_RSHIFT] == SDL_PRESSED)) || event.type == SDL_QUIT) {
			exec = 0;
		}
	}

	pceAppExit();
}

