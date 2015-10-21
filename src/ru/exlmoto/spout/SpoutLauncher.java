package ru.exlmoto.spout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SpoutLauncher extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spout_launcher);

		Button button = (Button)findViewById(R.id.buttonRunSpout);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), SpoutActivity.class);

				CheckBox checkBoxVar = (CheckBox)findViewById(R.id.checkBoxFilter);
				intent.putExtra("filter", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxScreenButtons);
				intent.putExtra("buttons", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxAspect);
				intent.putExtra("aspect", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxColor);
				intent.putExtra("color", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxTail);
				intent.putExtra("tail", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxSound);
				intent.putExtra("sound", checkBoxVar.isChecked());

				checkBoxVar = (CheckBox)findViewById(R.id.checkBoxVibro);
				intent.putExtra("vibro", checkBoxVar.isChecked());

				EditText editTextVar = (EditText)findViewById(R.id.editTextX);
				intent.putExtra("offset_x", editTextVar.toString());

				editTextVar = (EditText)findViewById(R.id.editTextY);
				intent.putExtra("offset_y", editTextVar.toString());

				startActivity(intent);
			}
		});
	}
}
