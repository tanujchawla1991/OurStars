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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ArtistSongs extends Activity{
	
	SharedPreferences prefs;
	String prefName ="MyPref";
	int use,use1;
	String query;
	InputStream is=null,is1=null;
	String result=null,result1=null;
	String line=null,line1=null;
	ArrayList<SongDataModel> listArray=null;
	int song_release,song_viewcount,song_rank=0;
	String song_id,song_name,song_url,song_duration,song_language,song_country;
	public ProgressDialog pdlg;
	Context ctx;
	SongListAdapter listAdapter;
	ListView listView;
	
	ArtistDataModel artist;
	
	String artist_name,artist_url;
	int artist_id,artist_recent,artist_all,artist_rank;

	 protected ImageLoader loader = ImageLoader.getInstance();
     DisplayImageOptions op = new DisplayImageOptions.Builder()
     .showStubImage(R.drawable.default_profile)
     .showImageForEmptyUri(R.drawable.default_profile)
     .showImageOnFail(R.drawable.default_profile)
     .cacheInMemory()
     .cacheOnDisc()
     .imageScaleType(ImageScaleType.EXACTLY)
     .displayer(new RoundedBitmapDisplayer(20))
     .build();
	     
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_songs);
		
		if (!isTablet(this))
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(ArtistSongs.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		showProgressBar("Fetching Songs...");
		listArray = new ArrayList<SongDataModel>();
    	ctx=this;
		
        prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		use=prefs.getInt("Database",1);
		use1=prefs.getInt("Artist_Choice",1);
		
	    listAdapter = new SongListAdapter(listArray,ctx);
	    listView = (ListView) findViewById(R.id.listView1);
	    listView.setAdapter(listAdapter);
		
		artist = (ArtistDataModel)getIntent().getSerializableExtra("artist_obj");
		
    	artist_name = artist.getName();
    	artist_url = artist.getUrl();
    	artist_id = artist.getId();
    	artist_recent = artist.getRecent();
    	artist_all = artist.getAll();
    	artist_rank = artist.getRank();
    	
    	ImageView imageView1=(ImageView)findViewById(R.id.imageView1);
    	if(artist_name.equalsIgnoreCase("(**Unknown Name**)"))
        {
        	imageView1.setImageResource(R.drawable.unknown);
        }
        else
        {
        	new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY);
	        loader.displayImage(artist_url, imageView1, op, null);
        }
    	
        TextView textView1=(TextView)findViewById(R.id.textView1);
        textView1.setText(artist_rank+"");
        
        TextView textView2=(TextView)findViewById(R.id.textView2);
        textView2.setText(artist_name);
        
        TextView textView3=(TextView)findViewById(R.id.textView3);
        textView3.setText("Recent Views: "+artist_recent);
        
        TextView textView4=(TextView)findViewById(R.id.textView4);
        textView4.setText("Total Views: "+artist_all);
        
    	new AsyncCaller().execute();
    	
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            	
            	SongDataModel song =(SongDataModel) adapterView.getItemAtPosition(i);
            	
            	Intent intent = new Intent(ArtistSongs.this, Player.class);
            	
            	Bundle mBundle = new Bundle();
            	mBundle.putSerializable("song_obj", song);
            	mBundle.putSerializable("artist_obj", artist);
            	
            	intent.putExtras(mBundle);
            	
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
        	searchsong();
			return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

    	    listAdapter.notifyDataSetChanged();
    	    
            pdlg.dismiss();    	        	
            
        }
    }
    
    public void searchsong()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	if(use==2)
    	{
    		Log.e("Alert", "Using alternate database.");
    		if(use1==1)
    		{
        		query="select * from Database_Project_DB2.one_year_2 where artistId='"+artist_id+"' order by viewCount desc";    			
    		}
    		if(use1==2)
    		{
        		query="select * from Database_Project_DB2.two_year_2 where artistId='"+artist_id+"' order by viewCount desc";    			
    		}
    		if(use1==3)
    		{
        		query="select * from Database_Project_DB2.five_year_2 where artistId='"+artist_id+"' order by viewCount desc";    			
    		}
    	}
    	else
    	{
    		Log.e("Alert", "Using original database.");
    		if(use1==1)
    		{
        		query="select * from Database_Project_DB2.one_year where artistId='"+artist_id+"' order by viewCount desc";    			
    		}
    		if(use1==2)
    		{
        		query="select * from Database_Project_DB2.two_year where artistId='"+artist_id+"' order by viewCount desc";    			
    		}
    		if(use1==3)
    		{
        		query="select * from Database_Project_DB2.five_year where artistId='"+artist_id+"' order by viewCount desc";    			
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
	        //Log.e("pass 1", "connection success ");
    	}
        catch(Exception e)
    	{
        	Log.e("Fail 1", e.toString());
	    	Toast.makeText(getApplicationContext(), "Invalid IP Address",
			Toast.LENGTH_LONG).show();
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
       
        try
    	{
        	//Log.e("Result", result);
        	JSONArray json_array = new JSONArray(result);
        	for(int a=0;a<json_array.length();a++)
        	{
	        	JSONObject json_object=json_array.getJSONObject(a);
	        	if(json_object.getString("songName")=="null" || json_object.getString("songName").indexOf("??")!=-1 || json_object.getString("songName")==null)
	        	{
	        		song_name=json_object.getString("youtubeName");
	        		if(json_object.getString("youtubeName")=="null" || json_object.getString("youtubeName").indexOf("??")!=-1 || json_object.getString("youtubeName")==null)
	        		{
	        			song_name="(**Unknown Name**)";
	        		}
	        	}
	        	else
	        	{
		        	song_name=json_object.getString("songName");	        		
	        	}
	        	
	        	song_duration=json_object.getString("duration");
	        	song_url=json_object.getString("url");
	        	song_id=json_object.getString("youtubeId");
	        	song_language=json_object.getString("songLanguage");
	        	song_country=json_object.getString("songCountry");
	        	song_release=json_object.getInt("releaseDate");
	        	song_viewcount=json_object.getInt("viewCount");
	        	song_rank=song_rank+1;
	        	listArray.add(new SongDataModel(song_id,song_name,song_viewcount,song_release,song_duration,song_url,song_rank,song_language,song_country));	        	
        	}
	        }
	        catch(Exception e)
	        {
	            	Log.e("Id doesn't exist 001.", e.toString());	        		
	        }

}

    
	public synchronized void  showProgressBar(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(pdlg == null){
                        pdlg = new ProgressDialog(ArtistSongs.this); 
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