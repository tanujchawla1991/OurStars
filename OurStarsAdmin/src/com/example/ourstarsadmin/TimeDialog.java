package com.example.ourstarsadmin;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TimePicker;

public class TimeDialog extends Dialog{
	public Activity c;
	TimePicker t1;
	ImageButton b1,b2;
	
	public TimeDialog(Activity a) {
		super(a);
		// TODO Auto-generated constructor stub
		this.c=a;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_time);
		t1 = (TimePicker)findViewById(R.id.timePicker1);
		b1 = (ImageButton) findViewById(R.id.imageButton1);
		b2 = (ImageButton) findViewById(R.id.imageButton2);
		
		t1.setIs24HourView(true);
		
		Calendar now = Calendar.getInstance();
		int hour_now = now.get(Calendar.HOUR_OF_DAY);
		int minute_now = now.get(Calendar.MINUTE);
		
		t1.setCurrentHour(hour_now);
		t1.setCurrentMinute(minute_now);
	}
	
}
