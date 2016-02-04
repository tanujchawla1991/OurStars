/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend {@link YouTubeBaseActivity}.
 */public class Player extends YouTubeFailureRecoveryActivity {
	 
	 SongDataModel song;
	 ArtistDataModel artist;
	 int status;
	 YouTubePlayerView youTubeView;
	 
	InputStream is=null;
	String result=null;
	String line=null;
	
	TextView t1,t2,t3,t4,t5,t6,t7;


	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_player);
	    
		song = (SongDataModel)getIntent().getSerializableExtra("song_obj");
		artist = (ArtistDataModel)getIntent().getSerializableExtra("artist_obj");
		
		t1=(TextView)findViewById(R.id.textView1);
		t2=(TextView)findViewById(R.id.textView2);
		t3=(TextView)findViewById(R.id.textView3);
		t4=(TextView)findViewById(R.id.textView4);
		t5=(TextView)findViewById(R.id.textView5);
		t6=(TextView)findViewById(R.id.textView6);
		t7=(TextView)findViewById(R.id.textView7);

		if(song.getName().length()>25)
        {
        	t2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
        }
        else
        {
        	t2.setTextSize(TypedValue.COMPLEX_UNIT_DIP,35);
        }
		
		t1.setText(song.getDuration());
		t2.setText(song.getName());
		t3.setText(artist.getName());
		t4.setText("Total Views: "+song.getViewcount());
		t5.setText("Year Of Release: "+song.getRelease());
		t6.setText("Language: "+song.getLanguage());
		t7.setText("Country: "+song.getCountry());

	    youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
	    
	    new AsyncCaller().execute();
	    
	    
	  }
	  
	  public void go_ahead()
	  {
		  if(status==0)
	        {
	        	Toast.makeText(getApplicationContext(), "Sorry, this video cannot be played due to age restrictions.", Toast.LENGTH_LONG).show();
	        	
	        	youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
	        	youTubeView.setEnabled(false);
	        }
	        else
	        {
	        	youTubeView.initialize(DeveloperKey.DEVELOPER_KEY, this);
	        }
	  }

	  @Override
	  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
	      boolean wasRestored) {

		  if (!wasRestored) {
	      player.cueVideo(song.getId());
	    }
		    player.loadVideo(song.getId());
	  }

	  @Override
	  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
	    return (YouTubePlayerView) findViewById(R.id.youtube_view);
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
	        	status=check_age();
				return null;
	        }

	        @Override
	        protected void onPostExecute(Void result) {
	            super.onPostExecute(result);
	            go_ahead();
	        }
	    }
	    
	    public int check_age()
	    {
	    	try
	    	{
	    		HttpClient httpclient = new DefaultHttpClient();
		        HttpGet httppost = new HttpGet("https://gdata.youtube.com/feeds/api/videos/"+song.getId()+"?v=2");
		        HttpResponse response = httpclient.execute(httppost); 
		    	HttpEntity entity = response.getEntity();

		        is = entity.getContent();
		        //Log.e("pass 1", "connection success ");
	    	}
	        catch(Exception e)
	    	{
	        	Log.e("Fail 1", e.toString());
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
		        //Log.e("pass 2", "connection success ");
	        }
	        catch(Exception e)
	    	{
	        	Log.e("Fail 2", e.toString());
	        	return 0;
	    	}
	        
	        if(result.indexOf("media:rating")==-1)
	        {
	        	//Log.e("Result",result);
	        	return 1;
	        }
	        else
	        {
	        	return 0;
	        }
	       
	}

}