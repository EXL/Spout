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

/*
 * SpoutNativeLibProxy.h
 *
 *  Created on: Oct 16, 2015
 *      Author: exl
 */

#ifndef SPOUTNATIVELIBPROXY_H_
#define SPOUTNATIVELIBPROXY_H_

#include <android/log.h>
#include <jni.h>

enum KeyCodes {
	KEY_LEFT	=	0x01,
	KEY_RIGHT	=	0x02,
	KEY_UP		=	0x03,
	KEY_DOWN	=	0x04,
	KEY_FIRE	=	0x05,
	KEY_QUIT	=	0x06,
	KEY_PAUSE	=	0x07,
	KEY_UNKNOWN	=	0x08
};

#define LOG_TAG "Spout_App"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

extern unsigned char keysState[];
extern int interval;

extern int filter;

extern int cube_on;

extern int sound_on;

extern int color_on;
extern int tail_on;

extern int dis_x;
extern int dis_y;

extern int score_height;
extern int score_score;

extern JNIEnv *javaEnviron;

#endif /* SPOUTNATIVELIBPROXY_H_ */
