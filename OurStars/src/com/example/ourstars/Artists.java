package com.example.ourstars;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

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


import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Artists extends Activity {

	SharedPreferences prefs;
	String prefName ="MyPref";
	int limit,limit_local,use,use1,use2;
	String query;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artists);
		
		if (!isTablet(this))
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(Artists.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		t1=(TextView)findViewById(R.id.textView2);
	    listView = (ListView) findViewById(R.id.listView1);
		
    	do_it_now();

	    ib1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				prefs=getSharedPreferences(prefName, MODE_PRIVATE);
			    SharedPreferences.Editor editor=prefs.edit();
			    editor.remove("Limit");
			    editor.putInt("Limit",limit-5);
			    editor.commit();
			    do_it_now();
		} 
		});
		
		ib2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				prefs=getSharedPreferences(prefName, MODE_PRIVATE);
			    SharedPreferences.Editor editor=prefs.edit();
			    editor.remove("Limit");
			    editor.putInt("Limit",limit+5);
			    editor.commit();
			    do_it_now();
			} 
		});

		ib3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Intent intent=new Intent(Artists.this,AddFilters.class);
				startActivity(intent);
			} 
		});
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	
            	ArtistDataModel artist =(ArtistDataModel) adapterView.getItemAtPosition(i);
                
            	Intent intent = new Intent(Artists.this, ArtistSongs.class);
            	
            	Bundle mBundle = new Bundle();
            	mBundle.putSerializable("artist_obj", artist);
            	
            	intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
		
	}
	
	public void do_it_now()
	{
		
		ib1=(ImageButton)findViewById(R.id.imageButton1);
		ib2=(ImageButton)findViewById(R.id.imageButton2);
		ib3=(ImageButton)findViewById(R.id.imageButton3);
    	iv2=(ImageView)findViewById(R.id.imageView2);
    	
		showProgressBar("Fetching Data and Images...");
		listArray = new ArrayList<ArtistDataModel>();
    	ctx=this;
		
        prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		limit=prefs.getInt("Limit",0);
		use=prefs.getInt("Database",1);
		use1=prefs.getInt("Artist_Choice",1);
		use2=prefs.getInt("Song_Choice",1);
    	
		limit_local=limit;

	    listAdapter = new ArtistListAdapter(listArray,ctx);
	    listView.setAdapter(listAdapter);
	    
	    t1.setText("< "+(limit+1)+" - "+(limit+5)+" >");
	    
    	Handler handler = new Handler(); 
        for(x = 0; x<5;x++)
        {
        	handler.postDelayed(new Runna(), 100*x); 
        }
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
		
	        	Intent intent=new Intent(Artists.this,ChooseYear.class);
            	finish();
            	startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artists, menu);
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

    	    listAdapter.notifyDataSetChanged();
    	    
    	    if(status==0)
            {
            	ib2.setVisibility(View.INVISIBLE);
            }
            else
            {
            	ib2.setVisibility(View.VISIBLE);
            }
            if(limit==0)
            {
            	ib1.setVisibility(View.INVISIBLE);
            }
            else
            {
            	ib1.setVisibility(View.VISIBLE);
            }
            
            if(listArray.size()==5)
            {
            int DELAY = 500;
    	    Handler handler1 = new Handler();
    	    handler1.postDelayed(new Runnable() {            
    	        @Override
    	        public void run() {
                	pdlg.dismiss();    	        	
    	    	}
    	    }, DELAY);
            }
            
        }
    }
    
    public int search()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    	
    	int from=limit_local;
    	limit_local=limit_local+1;
    	int to=1;

    	if(use==2)
    	{
    		Log.e("Alert", "Using alternate database.");
    		if(use1==1&&use2==1)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from one_one_2) limit "+from+","+to;
    		}
    		if(use1==1&&use2==2)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from one_two_2) limit "+from+","+to;
    		}
    		if(use1==1&&use2==3)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from one_three_2) limit "+from+","+to;
    		}
    		if(use1==2&&use2==1)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from two_one_2) limit "+from+","+to;
    		}
    		if(use1==2&&use2==2)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from two_two_2) limit "+from+","+to;
    		}
    		if(use1==2&&use2==3)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from two_three_2) limit "+from+","+to;
    		}
    		if(use1==3&&use2==1)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from three_one_2) limit "+from+","+to;
    		}
    		if(use1==3&&use2==2)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from three_two_2) limit "+from+","+to;
    		}
    		if(use1==3&&use2==3)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from three_three_2) limit "+from+","+to;
    		}
    	}
    	else
    	{
    		Log.e("Alert", "Using original database.");
    		if(use1==1&&use2==1)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from one_one) limit "+from+","+to;
    		}
    		if(use1==1&&use2==2)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from one_two) limit "+from+","+to;
    		}
    		if(use1==1&&use2==3)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from one_three) limit "+from+","+to;
    		}
    		if(use1==2&&use2==1)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from two_one) limit "+from+","+to;
    		}
    		if(use1==2&&use2==2)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from two_two) limit "+from+","+to;
    		}
    		if(use1==2&&use2==3)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from two_three) limit "+from+","+to;
    		}
    		if(use1==3&&use2==1)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from three_one) limit "+from+","+to;
    		}
    		if(use1==3&&use2==2)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from three_two) limit "+from+","+to;
    		}
    		if(use1==3&&use2==3)
    		{
    			query="select * from Database_Project_DB2.Artists where artistId in (select * from three_three) limit "+from+","+to;
    		}
    	}
    	
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
        	JSONArray json_array = new JSONArray(result);
	        	JSONObject json_object=json_array.getJSONObject(0);
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
	        	
	        	WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
	        	String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
	        	try{
		        	HttpClient httpclient1 = new DefaultHttpClient();
			        HttpGet httppost1 = new HttpGet("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+name.replace(" ", "")+"&userip="+ip);
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
				            listArray.add(new ArtistDataModel(id, name, all, recent,url,limit_local));
	        	}
	        	catch(Exception e)
	        	{
	            	Log.e("Id doesn't exist 001.", e.toString());
		            listArray.add(new ArtistDataModel(id, name, all, recent,"",limit_local));	        		
	        	}


	        	if(limit_local>=50)
	        	{
	        		return 0;
	        	}
	        	else
	        	{
	        		return 1;
	        	}
    	}
        catch(Exception e)
    	{
        	Log.e("Id doesn't exist.", e.toString());
        	return 0;
    	}
    }

    class Runna implements Runnable { 
    	public Runna() {
    	    // TODO Auto-generated constructor stub
    	}

    	    public void run() {  
    	// some code
    	    	new AsyncCaller().execute();
    	}
    	}
    
	public synchronized void  showProgressBar(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(pdlg == null){
                        pdlg = new ProgressDialog(Artists.this); 
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
}