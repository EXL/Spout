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

package ru.exlmoto.spout;

import java.lang.Integer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

public class SpoutLauncher extends Activity {

	private static final int originalWidth = 128;
	private static final int originalHeight = 88;
	private static int displayW;
	private static int displayH;

	private static int offX_saved = 0;
	private static int offY_saved = 0;
	private static int max_offX = 0;
	private static int max_offY = 0;

	private static final int OFFSET_ERROR = 0;

	public static final int SENSOR_TYPE_JOY = 0;
	public static final int SENSOR_TYPE_KEY = 1;
	public static final int SENSOR_TYPE_NON = 2;

	// DEFAULT SETTINGS CLASS
	public static class SpoutSettings {
		public static boolean s_Filter = false;
		public static boolean s_CubeDemo = false;
		public static int s_SensorType = SENSOR_TYPE_JOY;
		public static boolean s_Sensor = false;
		public static int s_OffsetX = 25;
		public static int s_OffsetY = 25;
		public static boolean s_AspectRatio = false;
		public static boolean s_Fullscreen = false;
		public static boolean s_Color = false;
		public static boolean s_Tail = false;
		public static boolean s_Sound = false;
		public static boolean s_Vibro = true;

		public static int s_scoreHeight = 0;
		public static int s_scoreScore = 0;
	}
	// END DEFAULT SETTINGS CLASS

	private CheckBox checkBoxFullscreen;
	private CheckBox checkBoxAspect;

	private RadioButton radioSensorJoy;
	private RadioButton radioSensorKey;
	private RadioButton radioSensorNone;

	private CheckBox checkBox3DCube;
	private CheckBox checkBoxAccel;

	private EditText editTextOffsetX;
	private EditText editTextOffsetY;

	private AlertDialog aboutDialog = null;

	private SharedPreferences settings = null;

	private int getAspectSideScaler(int original, int display) {
		int i = 1;
		int o = original;
		for (; display > o; ++i, o = original * i);
		return (i - 1);
	}

	private int min(int a, int b) {
		return (a > b) ? b : a;
	}

	private void getAspectRatioOffsets(int originalW, int originalH,
			int displayW, int displayH) {
		int wS = getAspectSideScaler(originalW, displayW);
		int hS = getAspectSideScaler(originalH, displayH);
		int scaler = min(wS, hS);

		int scaledWidth = originalWidth * scaler;
		int scaledHeight = originalHeight * scaler;

		SpoutSettings.s_OffsetX = (displayW - scaledWidth) / 2;
		SpoutSettings.s_OffsetY = (displayH - scaledHeight) / 2;
	}

	private boolean testOffsets(int offsetX, int offsetY, int displayWidth, int displayHeight) {
		int result = 0;

		max_offX = (displayWidth / 2) - (originalWidth / 2);
		max_offY = (displayHeight / 2) - (originalHeight / 2);

		if (offsetX >  max_offX) {
			result++;
		}
		if (offsetY > max_offY) {
			result++;
		}

		return (result == 0);
	}

	private void setOffsetTextViewsState(boolean enable) {
		editTextOffsetX.setEnabled(enable);
		editTextOffsetY.setEnabled(enable);
	}

	private void readSettings() {
		SpoutSettings.s_AspectRatio = settings.getBoolean("s_AspectRatio", SpoutSettings.s_AspectRatio);
		SpoutSettings.s_Color = settings.getBoolean("s_Color", SpoutSettings.s_Color);
		SpoutSettings.s_CubeDemo = settings.getBoolean("s_CubeDemo", SpoutSettings.s_CubeDemo);
		SpoutSettings.s_Filter = settings.getBoolean("s_Filter", SpoutSettings.s_Filter);
		SpoutSettings.s_Fullscreen = settings.getBoolean("s_Fullscreen", SpoutSettings.s_Fullscreen);
		SpoutSettings.s_OffsetX = settings.getInt("s_OffsetX", SpoutSettings.s_OffsetX);
		SpoutSettings.s_OffsetY = settings.getInt("s_OffsetY", SpoutSettings.s_OffsetY);
		SpoutSettings.s_Sensor = settings.getBoolean("s_Sensor", SpoutSettings.s_Sensor);
		SpoutSettings.s_Sound = settings.getBoolean("s_Sound", SpoutSettings.s_Sound);
		SpoutSettings.s_Tail = settings.getBoolean("s_Tail", SpoutSettings.s_Tail);
		SpoutSettings.s_Vibro = settings.getBoolean("s_Vibro", SpoutSettings.s_Vibro);
		SpoutSettings.s_SensorType = settings.getInt("s_SensorType", SpoutSettings.s_SensorType);

		SpoutSettings.s_scoreHeight = settings.getInt("s_scoreHeight", SpoutSettings.s_scoreHeight);
		SpoutSettings.s_scoreScore = settings.getInt("s_scoreScore", SpoutSettings.s_scoreScore);
	}

	private void writeSettings() {
		SpoutActivity.toDebug("== writeSettings() ==");

		fillSettingsByLayout();

		SharedPreferences.Editor editor = settings.edit();

		editor.putBoolean("s_AspectRatio", SpoutSettings.s_AspectRatio);
		editor.putBoolean("s_Color", SpoutSettings.s_Color);
		editor.putBoolean("s_CubeDemo", SpoutSettings.s_CubeDemo);
		editor.putBoolean("s_Filter", SpoutSettings.s_Filter);
		editor.putBoolean("s_Fullscreen", SpoutSettings.s_Fullscreen);

		if (testOffsets(SpoutSettings.s_OffsetX, SpoutSettings.s_OffsetY,
				displayW, displayH)) {
			editor.putInt("s_OffsetX", SpoutSettings.s_OffsetX);
			editor.putInt("s_OffsetY", SpoutSettings.s_OffsetY);
		} else {
			SpoutActivity.toDebug("Error: what's wrong! oX: " + SpoutSettings.s_OffsetX +
					" oY: " + SpoutSettings.s_OffsetY);
		}

		editor.putBoolean("s_Sensor", SpoutSettings.s_Sensor);
		editor.putInt("s_SensorType", SpoutSettings.s_SensorType);
		editor.putBoolean("s_Sound", SpoutSettings.s_Sound);
		editor.putBoolean("s_Tail", SpoutSettings.s_Tail);
		editor.putBoolean("s_Vibro", SpoutSettings.s_Vibro);

		editor.putInt("s_scoreHeight", SpoutSettings.s_scoreHeight);
		editor.putInt("s_scoreScore", SpoutSettings.s_scoreScore);

		editor.commit();
	}

	private void fillSettingsByLayout() {
		String _toParse = editTextOffsetX.getText().toString();
		SpoutSettings.s_OffsetX = _toParse.compareTo("") == 0 ? 0 : Integer.parseInt(_toParse);

		_toParse = editTextOffsetY.getText().toString();
		SpoutSettings.s_OffsetY = _toParse.compareTo("") == 0 ? 0 : Integer.parseInt(_toParse);

		SpoutSettings.s_AspectRatio = checkBoxAspect.isChecked();

		SpoutSettings.s_Fullscreen = checkBoxFullscreen.isChecked();

		CheckBox generalCheckBox = (CheckBox)findViewById(R.id.checkBoxColor);
		SpoutSettings.s_Color = generalCheckBox.isChecked();

		//generalCheckBox = (CheckBox)findViewById(R.id.checkBoxCube);
		SpoutSettings.s_CubeDemo = checkBox3DCube.isChecked();

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxFilter);
		SpoutSettings.s_Filter = generalCheckBox.isChecked();

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxSensor);
		SpoutSettings.s_Sensor = generalCheckBox.isChecked();

		if (radioSensorJoy.isChecked()) {
			SpoutSettings.s_SensorType = SENSOR_TYPE_JOY;
		} else if (radioSensorKey.isChecked()) {
			SpoutSettings.s_SensorType = SENSOR_TYPE_KEY;
		} else if (radioSensorNone.isChecked()) {
			SpoutSettings.s_SensorType = SENSOR_TYPE_NON;
		}

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxSound);
		SpoutSettings.s_Sound = generalCheckBox.isChecked();

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxTail);
		SpoutSettings.s_Tail = generalCheckBox.isChecked();

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxVibro);
		SpoutSettings.s_Vibro = generalCheckBox.isChecked();
	}

	private void fillLayoutBySettings() {
		// Fill offsets
		offX_saved = SpoutSettings.s_OffsetX;
		editTextOffsetX.setText(Integer.toString(SpoutSettings.s_OffsetX));

		offY_saved = SpoutSettings.s_OffsetY;
		editTextOffsetY.setText(Integer.toString(SpoutSettings.s_OffsetY));

		// Fill checkbox's
		checkBoxFullscreen.setChecked(SpoutSettings.s_Fullscreen);

		checkBoxAspect.setChecked(SpoutSettings.s_AspectRatio);

		switch (SpoutSettings.s_SensorType) {
		case SENSOR_TYPE_JOY:
			radioSensorJoy.setChecked(true);
			break;
		case SENSOR_TYPE_KEY:
			radioSensorKey.setChecked(true);
			break;
		case SENSOR_TYPE_NON:
			radioSensorNone.setChecked(true);
			break;
		default:
			break;
		}

		checkBox3DCube.setChecked(SpoutSettings.s_CubeDemo);

		CheckBox generalCheckBox = (CheckBox)findViewById(R.id.checkBoxColor);
		generalCheckBox.setChecked(SpoutSettings.s_Color);

//		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxCube);
//		generalCheckBox.setChecked(SpoutSettings.s_CubeDemo);

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxFilter);
		generalCheckBox.setChecked(SpoutSettings.s_Filter);

//		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxScreenButtons);
//		generalCheckBox.setChecked(SpoutSettings.s_ShowButtons);

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxSensor);
		generalCheckBox.setChecked(SpoutSettings.s_Sensor);

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxSound);
		generalCheckBox.setChecked(SpoutSettings.s_Sound);

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxTail);
		generalCheckBox.setChecked(SpoutSettings.s_Tail);

		generalCheckBox = (CheckBox)findViewById(R.id.checkBoxVibro);
		generalCheckBox.setChecked(SpoutSettings.s_Vibro);

		if (checkBoxAccel.isChecked()) {
			radioSensorNone.setChecked(true);
			radioSensorJoy.setEnabled(false);
			radioSensorKey.setEnabled(false);
		}

		if (checkBoxFullscreen.isChecked()) {
			setOffsetTextViewsState(false);
			checkBoxAspect.setChecked(false);
			checkBoxAspect.setEnabled(false);
			SpoutSettings.s_AspectRatio = false;
		}

		if (checkBoxAspect.isChecked()) {
			setOffsetTextViewsState(false);
			checkBoxFullscreen.setChecked(false);
			checkBoxFullscreen.setEnabled(false);
			SpoutSettings.s_Fullscreen = false;
		}

		if (checkBox3DCube.isChecked()) {
			offX_saved = SpoutSettings.s_OffsetX;
			editTextOffsetX.setText(Integer.toString(0));
			SpoutSettings.s_OffsetX = 0;
			offY_saved = SpoutSettings.s_OffsetY;
			editTextOffsetY.setText(Integer.toString(0));
			SpoutSettings.s_OffsetX = 0;
			setOffsetTextViewsState(false);

			checkBoxAspect.setChecked(false);
			checkBoxAspect.setEnabled(false);

			checkBoxFullscreen.setChecked(false);
			checkBoxFullscreen.setEnabled(false);
		}
	}

	public SpoutLauncher() {
		SpoutActivity.toDebug("== SpoutLauncher() constructor ==");
	}

	private void initAboutDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setCancelable(false);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_about, null);
		builder.setView(dialogView);
		builder.setTitle(R.string.app_name);
		builder.setPositiveButton(R.string.DialogOkText, null);
		aboutDialog = builder.create();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		displayW = getWindowManager().getDefaultDisplay().getWidth();
		displayH = getWindowManager().getDefaultDisplay().getHeight();

		if (displayH > displayW) { // To Landscape
			int tempVar = displayH;
			displayH = displayW;
			displayW = tempVar;
		}

		settings = getSharedPreferences("ru.exlmoto.spout", MODE_PRIVATE);

		// Check the first run
		if (settings.getBoolean("firstrun", true)) {
			// The first run, fill GUI layout with default values
			settings.edit().putBoolean("firstrun", false).commit();
		} else {
			// Read settings from Shared Preferences
			readSettings();
		}

		initAboutDialog();

		setContentView(R.layout.spout_launcher);

		// Fill layout
		editTextOffsetX = (EditText)findViewById(R.id.editTextX);
		editTextOffsetY = (EditText)findViewById(R.id.editTextY);

		checkBoxFullscreen = (CheckBox)findViewById(R.id.checkBoxFullscreen);
		checkBoxAspect = (CheckBox)findViewById(R.id.checkBoxAspect);

		radioSensorJoy = (RadioButton)findViewById(R.id.radioButtonJoy);
		radioSensorKey = (RadioButton)findViewById(R.id.radioButtonKey);
		radioSensorNone = (RadioButton)findViewById(R.id.radioButtonNone);

		checkBoxAccel = (CheckBox)findViewById(R.id.checkBoxSensor);

		checkBox3DCube = (CheckBox)findViewById(R.id.checkBoxCube);

		fillLayoutBySettings();

		// Set listeners
		checkBoxAccel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					radioSensorNone.setChecked(true);
					radioSensorJoy.setEnabled(false);
					radioSensorKey.setEnabled(false);
				} else {
					radioSensorJoy.setEnabled(true);
					radioSensorKey.setEnabled(true);
				}
			}
		});

		checkBox3DCube.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {

				checkBoxAspect.setChecked(false);
				checkBoxFullscreen.setChecked(false);

				if (status) {
					setOffsetTextViewsState(false);
					checkBoxAspect.setEnabled(false);
					checkBoxFullscreen.setEnabled(false);

					SpoutSettings.s_OffsetX = 0;
					SpoutSettings.s_OffsetY = 0;
				} else {
					setOffsetTextViewsState(true);
					checkBoxAspect.setEnabled(true);
					checkBoxFullscreen.setEnabled(true);

					SpoutSettings.s_OffsetX = offX_saved;
					SpoutSettings.s_OffsetY = offY_saved;
				}

				editTextOffsetX.setText(Integer.toString(SpoutSettings.s_OffsetX));
				editTextOffsetY.setText(Integer.toString(SpoutSettings.s_OffsetY));

				writeSettings();
			}

		});

		checkBoxFullscreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {

				setOffsetTextViewsState(!status);
				checkBoxAspect.setChecked(false);
				checkBoxAspect.setEnabled(!status);

				if (status) {
					SpoutSettings.s_OffsetX = 0;
					SpoutSettings.s_OffsetY = 0;
				} else {
					SpoutSettings.s_OffsetX = offX_saved;
					SpoutSettings.s_OffsetY = offY_saved;
				}

				editTextOffsetX.setText(Integer.toString(SpoutSettings.s_OffsetX));
				editTextOffsetY.setText(Integer.toString(SpoutSettings.s_OffsetY));

				writeSettings();
			}

		});

		checkBoxAspect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {

				setOffsetTextViewsState(!status);
				checkBoxFullscreen.setChecked(false);
				checkBoxFullscreen.setEnabled(!status);

				if (status) {
					getAspectRatioOffsets(originalWidth, originalHeight, displayW, displayH);
				} else {
					SpoutSettings.s_OffsetX = offX_saved;
					SpoutSettings.s_OffsetY = offY_saved;
				}

				editTextOffsetX.setText(Integer.toString(SpoutSettings.s_OffsetX));
				editTextOffsetY.setText(Integer.toString(SpoutSettings.s_OffsetY));

				writeSettings();
			}

		});

		Button button = (Button)findViewById(R.id.buttonRunSpout);
		// button.requestFocus();
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				writeSettings(); // For fill

				if (testOffsets(SpoutSettings.s_OffsetX, SpoutSettings.s_OffsetY, displayW, displayH)) {
					Intent intent = new Intent(v.getContext(), SpoutActivity.class);
					startActivity(intent);
					finish();
				} else {
					showDialog(OFFSET_ERROR);
				}
			}

		});

		Button aboutButton = (Button)findViewById(R.id.buttonAboutSpout);
		aboutButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showMyDialog(aboutDialog);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case OFFSET_ERROR:
			builder.setTitle(getString(R.string.OffsetErrorTitle));
			builder.setMessage(getString(R.string.OffsetErrorText) +
					"\nMax \'x\': " + max_offX + "\nMax \'y\': " + max_offY + "\n" +
					getString(R.string.OffsetErrorText2));
			// builder.setCancelable(false);
			builder.setPositiveButton(getString(R.string.DialogOkText), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			break;
		default:
			break;
		}

		return builder.create();
	}

	@Override
	protected void onDestroy() {
		// Write settings before destroy
		writeSettings();

		super.onDestroy();
	}

	// Prevent dialog dismiss when orientation changes
	// http://stackoverflow.com/a/27311231/2467443
	private static void doKeepDialog(AlertDialog dialog) {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(lp);
	}

	private void showMyDialog(AlertDialog dialog) {
		dialog.show();
		doKeepDialog(dialog);
	}

	/*
	@Override
	public void onBackPressed() {
		// Write settings
		writeSettings();

		// Exit to Android
		System.exit(0);
	}
	*/
}
