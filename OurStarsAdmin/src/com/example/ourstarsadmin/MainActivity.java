package com.example.ourstarsadmin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(MainActivity.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		int DELAY = 3000;
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {            
	        @Override
	        public void run() {
	        	Intent intent = new Intent(MainActivity.this, SettingsPage.class);
            	finish();
                startActivity(intent);
	    	}
	    }, DELAY);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
