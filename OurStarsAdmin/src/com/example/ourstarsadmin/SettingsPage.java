package com.example.ourstarsadmin;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingsPage extends Activity {
	
	ImageButton ib1,ib2;
	private PendingIntent pendingIntent,pendingIntent1;
	private AlarmManager manager,manager1;
	int diff1,diff2;
	int hour,hour_now,minute,minute_now;
	long diff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_page);
		
		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(SettingsPage.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
        // Retrieve a PendingIntent that will perform a broadcast
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
		Intent alarmIntent1 = new Intent(this, AlarmReceiver1.class);
		pendingIntent1 = PendingIntent.getBroadcast(this, 1, alarmIntent1, 0);
		
		manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		manager1 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		ib1=(ImageButton)findViewById(R.id.imageButton1);
		ib2=(ImageButton)findViewById(R.id.imageButton2);
		
		ib1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Show_Time_Dialog();

			} 
		});
		
		ib2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				cancelAlarm();
			} 
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void startAlarm(long time,long time1,int interval) {
		manager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval*60*60*1000, pendingIntent);
		manager1.setRepeating(AlarmManager.RTC_WAKEUP, time1, interval*60*60*1000, pendingIntent1);
		Toast.makeText(this, "Periodic Updates Started Successfully.", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Starting after "+diff2+" hour and "+diff1+" minute from now.\nRepeating every "+interval+" hour.", Toast.LENGTH_LONG).show();
	}
	
	public void cancelAlarm() {
			manager.cancel(pendingIntent);
			manager1.cancel(pendingIntent1);
			//manager2.cancel(pendingIntent2);
			//manager3.cancel(pendingIntent3);
	        Toast.makeText(this, "Periodic Updates Stopped Successfully.", Toast.LENGTH_LONG).show();
	}	
	
	public void Show_Interval_Dialog(long tims) {
		final IntervalDialog id = new IntervalDialog(SettingsPage.this);
		id.show();
		final long time=tims;
		ImageButton ok = (ImageButton) id.findViewById(R.id.imageButton1);
		ImageButton cancel = (ImageButton) id.findViewById(R.id.imageButton2);
		ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	NumberPicker n1=(NumberPicker)id.findViewById(R.id.numberPicker1);
            	int interval=n1.getValue();
            	long time1=time+(30*60*1000);
            	Log.e("Time",time+" "+time1+" "+interval);
            	startAlarm(time,time1,interval);
            	
				id.dismiss();
            }
        });
		cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	id.dismiss();
            }
        });
		
	}
	
	public void Show_Time_Dialog() {
		final TimeDialog td = new TimeDialog(SettingsPage.this);
		td.show();
    	
		ImageButton done = (ImageButton) td.findViewById(R.id.imageButton1);
		ImageButton cancel = (ImageButton) td.findViewById(R.id.imageButton2);
    	done.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	TimePicker t1=(TimePicker)td.findViewById(R.id.timePicker1);
            	hour=t1.getCurrentHour();
            	minute=t1.getCurrentMinute();
            	
            	Calendar now = Calendar.getInstance();
        		hour_now = now.get(Calendar.HOUR_OF_DAY);
        		minute_now = now.get(Calendar.MINUTE);
    	        Log.e("Time",hour+" "+minute);
        		if(hour<hour_now && minute>minute_now)
        		{
        			diff2=(24-hour_now)+hour;
        			diff1=minute-minute_now;
        		}
        		else if(hour<hour_now && minute<minute_now)
        		{
        			diff2=(24-hour_now)+hour-1;
        			diff1=minute-minute_now+60;
        		}
        		else if(hour-hour_now==1 && minute<minute_now)
        		{
        			diff2=0;
        			diff1=minute-minute_now+60;
        		}
        		else if(hour-hour_now>1 && minute<minute_now)
        		{
        			diff2=hour-hour_now-1;
        			diff1=minute-minute_now+60;
        		}
        		else
        		{
        			diff2=hour-hour_now;
        			diff1=minute-minute_now;
        		}
        		
        		
        		diff=System.currentTimeMillis()+diff2*60*60*1000+diff1*60*1000;
            	Show_Interval_Dialog(diff);
            	Log.e("Time",diff2+" "+diff1);
				td.dismiss();
            }
        });
		cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	td.dismiss();
            }
        });
		
	}
	
	public final boolean isInternetOn()
	{    
        getBaseContext();
		ConnectivityManager connec =(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
         
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                 connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                 connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED )
        {
                return true;
                 
        } else if ( 
              connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
              connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  )
        {
               
                return false;
        }
        return false;
    }
	
}