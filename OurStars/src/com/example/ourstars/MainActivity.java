package com.example.ourstars;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	SharedPreferences prefs;
	String prefName ="MyPref";
	int use;
	String query;
	InputStream is=null;
	String result=null;
	String line=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (!isTablet(this))
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(MainActivity.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		//prefs=getSharedPreferences(prefName, MODE_PRIVATE);
	    //SharedPreferences.Editor editor=prefs.edit();
	    //editor.putInt("Limit",0);
	    //editor.commit();
		

    	new AsyncCaller().execute();
	    
		int DELAY = 3000;
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {            
	        @Override
	        public void run() {
	        	Intent intent = new Intent(MainActivity.this, ChooseYear.class);
            	finish();
                startActivity(intent);
	    	}
	    }, DELAY);
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	
	private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here        	
        	use=check();
			return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            prefs=getSharedPreferences(prefName, MODE_PRIVATE);
    	    SharedPreferences.Editor editor=prefs.edit();
    	    editor.putInt("Database",use);
    	    Log.e("Using",use+"");
    	    editor.commit();
        }
    }
    
    public int check()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    	
    	query="show open tables from Database_Project_DB2 like 'Albums'";
    	nameValuePairs.add(new BasicNameValuePair("query",query));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://ec2-52-10-3-1.us-west-2.compute.amazonaws.com/database_project.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	    	HttpEntity entity = response.getEntity();

	        is = entity.getContent();
	        //Log.e("pass 1", "connection success ");
    	}
        catch(Exception e)
    	{
        	Log.e("Fail 1", e.toString());
	    	Toast.makeText(getApplicationContext(), "Invalid IP Address",
			Toast.LENGTH_LONG).show();
	    	return 1;
    	}     
        
        try
        {
         	BufferedReader reader = new BufferedReader
			(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
       		    sb.append(line + "\n");
           	}
            is.close();
            result = sb.toString();
	        //Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
    	{
        	Log.e("Fail 2", e.toString());
        	return 1;
    	}     
       
        try
    	{
        	JSONArray json_array = new JSONArray(result);  
	       	JSONObject json_object=json_array.getJSONObject(0);
	       	int a=json_object.getInt("In_use");
	       	
	       	if(a==1)
	       		return 2;
	       	else
	       		return 1;
    	}
        catch(Exception e)
    	{
        	Log.e("Id doesn't exist.", e.toString());
        	return 1;
    	}
    }
}
