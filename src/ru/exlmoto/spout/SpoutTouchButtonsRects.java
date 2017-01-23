/************************************************************************************
** The MIT License (MIT)
**
** Copyright (c) 2015-2016 Serg "EXL" Koles
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

package ru.exlmoto.spout;

import java.util.ArrayList;
import java.util.List;

import ru.exlmoto.spout.SpoutActivity.SpoutSounds;

public class SpoutTouchButtonsRects {

	private List<SpoutButton> initializedButtons = null;

	private class SpoutButton {

		final private int VIBRO_OFFSET = 20;

		private float m_x0;
		private float m_y0;
		private float m_x1;
		private float m_y1;

		private int m_buttonCode;
		private boolean m_buttonPushed = false;

		// -1 for no touches on button
		private int m_buttonTouchId = -1;

		// Useful for DEBUG
		private String m_buttonName;

		public SpoutButton(String buttonName,
				float x, float y, float width, float height,
				int keyCode) {
			m_buttonName = buttonName;
			m_x0 = x;
			m_y0 = y;
			m_x1 = x + width;
			m_y1 = y + height;
			m_buttonCode = keyCode;
		}

		public boolean checkButtonRect(float touchX, float touchY) {
			return (touchX > m_x0 &&
					touchX < m_x1 &&
					touchY > m_y0 &&
					touchY < m_y1);
		}

		public void press() {
			m_buttonPushed = true;

			SpoutActivity.playSound(SpoutSounds.s_button);
			SpoutActivity.doVibrate(SpoutActivity.touchDelay - VIBRO_OFFSET);

			SpoutNativeLibProxy.SpoutNativeKeyDown(m_buttonCode);
		}

		public void release() {
			m_buttonPushed = false;

			m_buttonTouchId = -1;

			SpoutNativeLibProxy.SpoutNativeKeyUp(m_buttonCode);
		}

		@SuppressWarnings("unused")
		public String getName() {
			return m_buttonName;
		}

		public void setTouchId(int touchId) {
			m_buttonTouchId = touchId;
		}

		public int getTouchId() {
			return m_buttonTouchId;
		}

		public boolean getState() {
			return m_buttonPushed;
		}
	}

	public SpoutTouchButtonsRects() {
		initButtonsRects();
	}

	public void checkTouchButtons(float touchX, float touchY, int touchId) {
		for (SpoutButton button : initializedButtons) {
			if (button.checkButtonRect(touchX, touchY)) {
				button.setTouchId(touchId);
			}
		}
	}

	public void pressSingleTouchButtons() {
		for (SpoutButton button : initializedButtons) {
			if (button.getTouchId() == 0) {
				button.press();
			}
		}
	}

	public void pressMultiTouchButtons() {
		for (SpoutButton button : initializedButtons) {
			if (button.getTouchId() > 0 && !button.getState()) {
				button.press();
			}
		}
	}

	public void releaseMultiTouchButtons(int touchId) {
		for (SpoutButton button : initializedButtons) {
			if (button.getTouchId() == touchId) {
				button.release();
			}
		}
	}

	public void releaseAllButtons() {
		for (SpoutButton button : initializedButtons) {
			button.release();
			button.setTouchId(-1);
		}
	}

	private void initButtonsRects() {
		/************************************************************************************
		**     +------------------------------------------------+
		**     |    overlay (overlay_width x overlay_height)    |
		**     |                                                |
		**     |                                                |
		**     |    btn_x, btn_y ->  +--------+                 |
		**     |                     | button |                 |
		**     |                     |        |                 |
		**     |                     |        |                 |
		**     |                     +--------+ <- btn_w, btn_h |
		**     |                                                |
		**     +------------------------------------------------+
		**
		**     btn_x and btn_y is coordinates of start point of button on an overlay
		**     btn_w and btn_h is coordinates of end point of button on an overlay
		**
		**     float x = btn_x / overlay_width;
		**     float y = btn_y / overlay_height;
		**     float width = btn_w / overlay_width;
		**     float height = btn_h / overlay_height;
		**
		**     Example for 854x480 overlay:
		**     float x = 125.0 / 854.0;
		**     float y = 455.0 / 480.0;
		**     float width = 200.0 / 854.0;
		**     float height = 475.0 / 480.0;
		************************************************************************************/

		initializedButtons = new ArrayList<SpoutTouchButtonsRects.SpoutButton>();
		initializedButtons.add(new SpoutButton("Left", 0.0351f, 0.6437f, 0.1651f, 0.2937f, SpoutNativeSurface.KEY_LEFT));
		initializedButtons.add(new SpoutButton("Right", 0.2353f, 0.6437f, 0.1651f, 0.2937f, SpoutNativeSurface.KEY_RIGHT));
		initializedButtons.add(new SpoutButton("Fire", 0.7997f, 0.6437f, 0.1651f, 0.2937f, SpoutNativeSurface.KEY_FIRE));
	}
}
