package com.example.ourstarsadmin;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.NumberPicker;

public class IntervalDialog extends Dialog{
		public Activity c;
		NumberPicker n1;
		ImageButton b1,b2;
		
		public IntervalDialog(Activity a) {
			super(a);
			// TODO Auto-generated constructor stub
			this.c=a;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.select_interval);
			n1=(NumberPicker)findViewById(R.id.numberPicker1);
			b1 = (ImageButton) findViewById(R.id.imageButton1);
			b2 = (ImageButton) findViewById(R.id.imageButton2);
			
			n1.setMinValue(1);
			n1.setValue(1);
			n1.setMaxValue(24);
		}
}
