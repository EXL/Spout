# This Makefile was created by EXL: 08-Apr-2023

all: linux

linux:
	gcc -O2 spout.c piece.c -o Spout `sdl2-config --cflags --libs`
	strip -s Spout

web:
	emcc -O2 --use-preload-plugins spout.c piece.c -s USE_SDL=2 -o Spout.html
	emstrip -s Spout.wasm

clean:
	-rm -f Spout
	-rm -f Spout.o
	-rm -f Spout.obj
	-rm -f Spout.exe
	-rm -f Spout.data
	-rm -f Spout.html
	-rm -f Spout.wasm
	-rm -f Spout.js
