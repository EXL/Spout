package ru.exlmoto.spout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class SpoutLauncher extends Activity {

	private static final int originalWidth = 128;
	private static final int originalHeight = 88;

	private static int offX = 0;
	private static int offY = 0;
	private static int offX_saved = 0;
	private static int offY_saved = 0;

	private CheckBox checkBoxFullscreen;
	private CheckBox checkBoxAspect;

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

		offX = (displayW - scaledWidth) / 2;
		offY = (displayH - scaledHeight) / 2;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spout_launcher);

		final EditText editTextOffsetX = (EditText)findViewById(R.id.editTextX);
		editTextOffsetX.setText("25");
		offX_saved = 25;

		final EditText editTextOffsetY = (EditText)findViewById(R.id.editTextY);
		editTextOffsetY.setText("25");
		offY_saved = 25;

		checkBoxFullscreen = (CheckBox)findViewById(R.id.checkBoxFullscreen);
		checkBoxFullscreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {

				editTextOffsetX.setEnabled(!status);
				editTextOffsetY.setEnabled(!status);
				checkBoxAspect.setEnabled(!status);

				if (status) {
					offX = 0;
					offY = 0;
				} else {
					offX = offX_saved;
					offY = offY_saved;
				}

				editTextOffsetX.setText(String.valueOf(offX));
				editTextOffsetY.setText(String.valueOf(offY));
			}

		});

		checkBoxAspect = (CheckBox)findViewById(R.id.checkBoxAspect);
		checkBoxAspect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton button, boolean status) {

				editTextOffsetX.setEnabled(!status);
				editTextOffsetY.setEnabled(!status);
				checkBoxFullscreen.setEnabled(!status);

				if (status) {
					getAspectRatioOffsets(originalWidth, originalHeight,
							getWindowManager().getDefaultDisplay().getWidth(),
							getWindowManager().getDefaultDisplay().getHeight());
				} else {
					offX = offX_saved;
					offY = offY_saved;
				}

				editTextOffsetX.setText(String.valueOf(offX));
				editTextOffsetY.setText(String.valueOf(offY));
			}

		});

		Button button = (Button)findViewById(R.id.buttonRunSpout);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), SpoutActivity.class);

				CheckBox checkBoxVar = (CheckBox)findViewById(R.id.checkBoxFilter);
				intent.putExtra("filter", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxScreenButtons);
				intent.putExtra("buttons", checkBoxVar.isChecked());

//				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxAspect);
//				intent.putExtra("aspect", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxColor);
				intent.putExtra("color", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxTail);
				intent.putExtra("tail", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxSound);
				intent.putExtra("sound", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxVibro);
				intent.putExtra("vibro", checkBoxVar.isChecked());

				EditText editTextVar = (EditText)findViewById(R.id.editTextX);
				intent.putExtra("offset_x", editTextVar.getText().toString());

				editTextVar = (EditText)findViewById(R.id.editTextY);
				intent.putExtra("offset_y", editTextVar.getText().toString());

				startActivity(intent);
			}
		});
	}
}
