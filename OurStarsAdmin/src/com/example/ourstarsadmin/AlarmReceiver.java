package com.example.ourstarsadmin;

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	String query0="lock tables one_year WRITE, one_year_week WRITE, one_year_month WRITE, one_year_genres WRITE, two_year WRITE, two_year_week WRITE, two_year_month WRITE, two_year_genres WRITE, five_year WRITE, five_year_week WRITE, five_year_month WRITE, five_year_genres WRITE, Songs READ, Songs as s READ, CrawlHistory Read, Genres Read, Albums Write";
	
	String query1="delete from one_year";
	String query2="insert into one_year select *, viewCount/DATEDIFF(curdate(),youtubeDate) as delta from Songs where year(youtubeDate)>year(date_sub(curdate(), interval 2 year)) and artistId not in (select artistId from Songs s where s.youtubeDate<date_sub(curdate(), interval 1 year))";
	String query3="delete from two_year";
	String query4="insert into two_year select *, viewCount/DATEDIFF(curdate(),youtubeDate) as delta from Songs where year(youtubeDate)>year(date_sub(curdate(), interval 2 year)) and artistId not in (select artistId from Songs s where s.youtubeDate<date_sub(curdate(), interval 2 year))";
	String query5="delete from five_year";
	String query6="insert into five_year select *, viewCount/DATEDIFF(curdate(),youtubeDate) as delta from Songs where year(youtubeDate)>year(date_sub(curdate(), interval 2 year)) and artistId not in (select artistId from Songs s where s.youtubeDate<date_sub(curdate(), interval 5 year))";
	String query7="delete from one_year_week";
	String query8="insert into one_year_week select songId, max(views)-min(views) from CrawlHistory where date>date_sub('2015-03-15', interval 7 day) and songId in (select youtubeId from one_year) group by songId order by max(views)-min(views) desc";
	String query9="delete from one_year_month";
	String query10="insert into one_year_month select songId, max(views)-min(views) from CrawlHistory where date>date_sub('2015-03-15', interval 1 month) and songId in (select youtubeId from one_year) group by songId order by max(views)-min(views) desc";
	String query11="delete from two_year_week";
	String query12="insert into two_year_week select songId, max(views)-min(views) from CrawlHistory where date>date_sub('2015-03-15', interval 7 day) and songId in (select youtubeId from two_year) group by songId order by max(views)-min(views) desc";
	String query13="delete from two_year_month";
	String query14="insert into two_year_month select songId, max(views)-min(views) from CrawlHistory where date>date_sub('2015-03-15', interval 1 month) and songId in (select youtubeId from two_year) group by songId order by max(views)-min(views) desc";
	String query15="delete from five_year_week";
	String query16="insert into five_year_week select songId, max(views)-min(views) from CrawlHistory where date>date_sub('2015-03-15', interval 7 day) and songId in (select youtubeId from five_year) group by songId order by max(views)-min(views) desc";
	String query17="delete from five_year_month";
	String query18="insert into five_year_month select songId, max(views)-min(views) from CrawlHistory where date>date_sub('2015-03-15', interval 1 month) and songId in (select youtubeId from five_year) group by songId order by max(views)-min(views) desc";
	String query19="delete from one_year_genres";
	String query20="insert into one_year_genres select * from Genres where songId in(select youtubeId from one_year)";
	String query21="delete from two_year_genres";
	String query22="insert into two_year_genres select * from Genres where songId in(select youtubeId from two_year)";
	String query23="delete from five_year_genres";
	String query24="insert into five_year_genres select * from Genres where songId in(select youtubeId from five_year)";

	String query25="UNLOCK TABLES";
	
	InputStream is=null;
	String result=null;
	String line=null;
	
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// For our recurring task, we'll just display a message
		Toast.makeText(arg0, "Updating...", Toast.LENGTH_SHORT).show();
		
		new AsyncCaller().execute();
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
        	refresh();	
			return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("Done", "Done");

        }
    }
    
    public void refresh()
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("query",query0));
    	nameValuePairs.add(new BasicNameValuePair("query1",query1));
    	nameValuePairs.add(new BasicNameValuePair("query2",query2));
    	nameValuePairs.add(new BasicNameValuePair("query3",query3));
    	nameValuePairs.add(new BasicNameValuePair("query4",query4));
    	nameValuePairs.add(new BasicNameValuePair("query5",query5));
    	nameValuePairs.add(new BasicNameValuePair("query6",query6));
    	nameValuePairs.add(new BasicNameValuePair("query7",query7));
    	nameValuePairs.add(new BasicNameValuePair("query8",query8));
    	nameValuePairs.add(new BasicNameValuePair("query9",query9));
    	nameValuePairs.add(new BasicNameValuePair("query10",query10));
    	nameValuePairs.add(new BasicNameValuePair("query11",query11));
    	nameValuePairs.add(new BasicNameValuePair("query12",query12));
    	nameValuePairs.add(new BasicNameValuePair("query13",query13));
    	nameValuePairs.add(new BasicNameValuePair("query14",query14));
    	nameValuePairs.add(new BasicNameValuePair("query15",query15));
    	nameValuePairs.add(new BasicNameValuePair("query16",query16));
    	nameValuePairs.add(new BasicNameValuePair("query17",query17));
    	nameValuePairs.add(new BasicNameValuePair("query18",query18));
    	nameValuePairs.add(new BasicNameValuePair("query19",query19));
    	nameValuePairs.add(new BasicNameValuePair("query20",query20));
    	nameValuePairs.add(new BasicNameValuePair("query21",query21));
    	nameValuePairs.add(new BasicNameValuePair("query22",query22));
    	nameValuePairs.add(new BasicNameValuePair("query23",query23));
    	nameValuePairs.add(new BasicNameValuePair("query24",query24));
    	nameValuePairs.add(new BasicNameValuePair("query25",query25));
    	
    	try
    	{
    		HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost("http://ec2-52-10-3-1.us-west-2.compute.amazonaws.com/database_project_admin_new.php");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	    	HttpEntity entity = response.getEntity();

	        is = entity.getContent();
	        Log.e("pass 1", "connection success ");
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
	        Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
    	{
        	Log.e("Fail 2", e.toString());
    	}     
       
        try
    	{
        	Log.e("Result", result);
        	
    	}
        catch(Exception e)
    	{
        	Log.e("Id doesn't exist.", e.toString());
    	}
    }
    
}