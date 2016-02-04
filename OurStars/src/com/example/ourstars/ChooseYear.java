package com.example.ourstars;

import java.util.regex.Pattern;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class ChooseYear extends Activity {
	ImageButton ib1;
	NumberPicker np1,np2;
	int i=0,j=0;
	SharedPreferences prefs;
	String prefName ="MyPref";
	
	String possibleEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_year);
		
		if (!isTablet(this))
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(ChooseYear.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		Pattern emailPattern = Patterns.EMAIL_ADDRESS;
		Account[] accounts = AccountManager.get(getApplicationContext()).getAccounts();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		        possibleEmail = account.name;
		    }
		}
    	
		prefs=getSharedPreferences(prefName, MODE_PRIVATE);
	    SharedPreferences.Editor editor=prefs.edit();
	    editor.putInt("Limit",0);
	    editor.commit();
		
		ib1=(ImageButton)findViewById(R.id.imageButton1);
		np1=(NumberPicker)findViewById(R.id.numberPicker1);
		np2=(NumberPicker)findViewById(R.id.numberPicker2);
		
		String[] artist=new String[3];
		artist[0]="Past One Year";
		artist[1]="Past Two Years";
		artist[2]="Past Five Years";
		np1.setMaxValue(artist.length-1);
		np1.setMinValue(0);
		np1.setDisplayedValues(artist);
		np1.setValue(1);
		
		String[] song=new String[3];
		song[0]="Past One Week";
		song[1]="Past One Month";
		song[2]="Past One Year";
		np2.setMaxValue(song.length-1);
		np2.setMinValue(0);
		np2.setDisplayedValues(song);
		np2.setValue(1);
		
		ib1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(np1.getValue()==0)
				{
					i=1;
				}
				else
				{
					if(np1.getValue()==1)
					{
						i=2;
					}
					else
					{
						i=3;
					}
				}
				
				if(np2.getValue()==0)
				{
					j=1;
				}
				else
				{
					if(np2.getValue()==1)
					{
						j=2;
					}
					else
					{
						j=3;
					}
				}
				
				prefs=getSharedPreferences(prefName, MODE_PRIVATE);
			    SharedPreferences.Editor editor=prefs.edit();
			    editor.putInt("Artist_Choice",i);
			    editor.putInt("Song_Choice",j);
			    editor.commit();									    
			    Intent intent= new Intent(ChooseYear.this,Artists.class);
			    finish();
			    startActivity(intent);
			} 
		});

	}
	

	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
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


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Sending(possibleEmail);
		finish();
	}
	
	public void Sending(String email_user)
	{
		String u="Hi,\n \nHopefully you found what you have been looking for.\n \nPlease drop us a feedback at ourstarsdb@gmail.com .\n \nThank you.\n \nIt was pleasure serving you.\n \nAlways at your service,\nOutStars";
		String v=email_user;
	    try
	    {  
	        SendOrderEmail l=new SendOrderEmail();
	        l.execute(u,v);
	    }
	    catch (Exception x)
	    {  
	        Log.e("SendMail", x.getMessage(), x);
	    }
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_year, menu);
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
}
