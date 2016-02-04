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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FilteredArtists extends Activity {


	SharedPreferences prefs;
	String prefName ="MyPref";
	String query,query_del;
	InputStream is=null,is1=null;
	String result=null,result1=null;
	String line=null,line1=null;
	ArrayList<ArtistDataModel> listArray=null;
	int id,all,recent;
	String name;
	int i,status;
	ImageButton ib1,ib2,ib3;
	ImageView iv2;
	TextView t1;
	int DELAY;
	public ProgressDialog pdlg;
	Context ctx;
	int x;    
	ArtistListAdapter listAdapter;
	ListView listView;
	String user_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filtered_artists);
		
		if (!isTablet(this))
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(FilteredArtists.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		listView = (ListView) findViewById(R.id.listView1);

    	TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		user_id=telephonyManager.getDeviceId();
		
    	do_it_now();
    	
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	
            	ArtistDataModel artist =(ArtistDataModel) adapterView.getItemAtPosition(i);
                
            	Intent intent = new Intent(FilteredArtists.this, ArtistSongs.class);
            	
            	Bundle mBundle = new Bundle();
            	mBundle.putSerializable("artist_obj", artist);
            	
            	intent.putExtras(mBundle);
                startActivity(intent);
            }
        });

	}
	
	public void do_it_now()
	{
		
		showProgressBar("Fetching Data and Images...");
		listArray = new ArrayList<ArtistDataModel>();
    	ctx=this;
    	
	    listAdapter = new ArtistListAdapter(listArray,ctx);
	    listView.setAdapter(listAdapter);
	    
	    new AsyncCaller().execute();
	}
	
	public void del_view()
	{
		new AsyncCaller1().execute();
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
		super.onBackPressed();
		Intent intent=new Intent(FilteredArtists.this,AddFilters.class);
		finish();
		startActivity(intent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.filtered_artists, menu);
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
        	status=search();
			return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
                	    	    
            if(status==0)
            {
            	pdlg.dismiss();
            	del_view();
            	Toast.makeText(getApplicationContext(), "Sorry, currently no records match your search criteria.", Toast.LENGTH_LONG).show();
            	Toast.makeText(getApplicationContext(), "Please try searching again with different filters.", Toast.LENGTH_LONG).show();
            	Intent intent=new Intent(FilteredArtists.this,AddFilters.class);
            	finish();
            	startActivity(intent);
            }
            else
            {
        	    listAdapter.notifyDataSetChanged();
        		int DELAY = 2000;
        	    Handler handler = new Handler();
        	    handler.postDelayed(new Runnable() {            
        	        @Override
        	        public void run() {
        	        	pdlg.dismiss();
        	        	del_view();
        	    	}
        	    }, DELAY);
            }
        }
    }
    
    public int search()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    	
    	query="select * from Database_Project_DB2.Artists where artistId in (select * from "+user_id+"ourstars) limit 5";
    	nameValuePairs.add(new BasicNameValuePair("query",query));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://ec2-52-10-3-1.us-west-2.compute.amazonaws.com/database_project.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	    	HttpEntity entity = response.getEntity();

	        is = entity.getContent();
	        //Log.e("pass 1", limit+"connection success ");
    	}
        catch(Exception e)
    	{
        	Log.e("Fail 1", e.toString());
	    	Toast.makeText(getApplicationContext(), "Invalid IP Address",
			Toast.LENGTH_LONG).show();
	    	return 0;
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
	        //Log.e("pass 2", limit+"connection success ");
        }
        catch(Exception e)
    	{
        	Log.e("Fail 2", e.toString());
        	return 0;
    	}     
       
        try
    	{
        	//Log.e("Result", result);
        	if(result.indexOf("null")>-1)
        	{
    	        Log.e("Tag","3333333333333333333");
        		return 0;
        	}
        	JSONArray json_array = new JSONArray(result);
        	WifiManager wm1 = (WifiManager) getSystemService(WIFI_SERVICE);
        	String ip1 = Formatter.formatIpAddress(wm1.getConnectionInfo().getIpAddress());
        	for(int z=0;z<json_array.length();z++)
        	{
        		JSONObject json_object=json_array.getJSONObject(z);
	        	if(json_object.getString("artistName")=="null" || json_object.getString("artistName").indexOf("??")!=-1 || json_object.getString("artistName")==null)
	        	{
	        		name="(**Unknown Name**)";
	        	}
	        	else
	        	{
	        		name=json_object.getString("artistName");	        		
	        	}
	        	id=json_object.getInt("artistId");
	        	all=json_object.getInt("artistPopularityAll");
	        	recent=json_object.getInt("artistPopularityRecent");
	        	
	        	try{
		        	HttpClient httpclient1 = new DefaultHttpClient();
			        HttpGet httppost1 = new HttpGet("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+name.replace(" ", "")+"&userip="+ip1);
			        HttpResponse response1 = httpclient1.execute(httppost1); 
			    	HttpEntity entity1 = response1.getEntity();
			    	is1 = entity1.getContent();
			    	BufferedReader reader1 = new BufferedReader
			    			(new InputStreamReader(is1,"iso-8859-1"),8);
			                StringBuilder sb1 = new StringBuilder();
			                while ((line1 = reader1.readLine()) != null)
			                {
			           		    sb1.append(line1 + "\n");
			               	}
			                is1.close();
			                result1 = sb1.toString();
			                //Log.e("Result1", result1);
			                JSONObject json_object1=new JSONObject(result1);
			                JSONObject json_object2=json_object1.getJSONObject("responseData");
			                JSONArray json_array1=json_object2.getJSONArray("results");
			                JSONObject json_object3=json_array1.getJSONObject(0);
			                String url=json_object3.getString("unescapedUrl");
			                //Log.e("URL", url);
				            listArray.add(new ArtistDataModel(id, name, all, recent,url,z+1));
	        	}
	        	catch(Exception e)
	        	{
	            	Log.e("Id doesn't exist 001.", e.toString());
		            listArray.add(new ArtistDataModel(id, name, all, recent,"",z+1));	        		
	        	}
        	}

        	return 1;
    	}
        catch(Exception e)
    	{
        	Log.e("Id doesn't exist.", e.toString());
        	return 0;
    	}
    }
	    
	public synchronized void  showProgressBar(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(pdlg == null){
                        pdlg = new ProgressDialog(FilteredArtists.this); 
                        pdlg.setCancelable(false); 
                        pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
                    }
                }catch (Exception e) {}

                try{
                    if(!pdlg.isShowing()) pdlg.show();
                    pdlg.setMessage(message); 
                }catch (Exception e) {}
                
            }
        });
    }
	
	private class AsyncCaller1 extends AsyncTask<Void, Void, Void>
    {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here        	
        	delete();
			return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    
    public void delete()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    	
    	query_del="drop view "+user_id+"ourstars";
    	nameValuePairs.add(new BasicNameValuePair("query",query_del));    	
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
    	}
    }
    
}
