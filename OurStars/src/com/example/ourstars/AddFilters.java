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
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddFilters extends Activity {
	Spinner s1,s2,s3,s4;
	ImageButton ib1,ib2;
	TextView t1,t2,t3,t4;
	
	SharedPreferences prefs;
	String prefName ="MyPref";
	int use,use1,use2;
	
	public ProgressDialog pdlg;
	
	String query1,query2,query3,query4,query_filter="";
	InputStream is=null;
	String result=null;
	String line=null;
	
	String country[];
	String language[];
	String category[];
	String subcategory[]={"All"};
	
	String coun,lang,cat,subcat;
	
	String temp;
	
    ArrayAdapter<String> dataAdapter4=null;
    ArrayAdapter<String> dataAdapter1=null;
    ArrayAdapter<String> dataAdapter2=null;
    ArrayAdapter<String> dataAdapter3=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_filters);
		
		if (!isTablet(this))
		    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		boolean y=isInternetOn();
		if(!y)
		{
		    Toast.makeText(AddFilters.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
		    return;
		}
		
		showProgressBar("Fetching Filters...");
		
		t1=(TextView)findViewById(R.id.textView1);
		t1.setPaintFlags(t1.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
		t2=(TextView)findViewById(R.id.textView2);
		t2.setPaintFlags(t2.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
		t3=(TextView)findViewById(R.id.textView3);
		t3.setPaintFlags(t3.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
		t4=(TextView)findViewById(R.id.textView4);
		t4.setPaintFlags(t4.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);
		
		s1=(Spinner)findViewById(R.id.spinner1);
		s2=(Spinner)findViewById(R.id.spinner2);
		s3=(Spinner)findViewById(R.id.spinner3);
		s4=(Spinner)findViewById(R.id.spinner4);
		
		ib1=(ImageButton)findViewById(R.id.imageButton1);
		ib2=(ImageButton)findViewById(R.id.imageButton2);
		
        prefs=getSharedPreferences(prefName, MODE_PRIVATE);
		use=prefs.getInt("Database",1);
		use1=prefs.getInt("Artist_Choice",1);
		use2=prefs.getInt("Song_Choice",1);
		
		if(use1==1)
		{
			if(use==2)
			{
				query1="select distinct songCountry from one_year_2 order by songCountry";
				query2="select distinct songLanguage from one_year_2 order by songLanguage";
				query3="select distinct category from categories order by category";
			}
			else
			{
				query1="select distinct songCountry from one_year order by songCountry";
				query2="select distinct songLanguage from one_year order by songLanguage";
				query3="select distinct category from categories order by category";
			}	
		}
		
		if(use1==2)
		{
			if(use==2)
			{
				query1="select distinct songCountry from two_year_2 order by songCountry";
				query2="select distinct songLanguage from two_year_2 order by songLanguage";
				query3="select distinct category from categories order by category";
			}
			else
			{
				query1="select distinct songCountry from two_year order by songCountry";
				query2="select distinct songLanguage from two_year order by songLanguage";
				query3="select distinct category from categories order by category";
			}
		}
		
		if(use1==3)
		{
			if(use==2)
			{
				query1="select distinct songCountry from five_year_2 order by songCountry";
				query2="select distinct songLanguage from five_year_2 order by songLanguage";
				query3="select distinct category from categories order by category";
			}
			else
			{
				query1="select distinct songCountry from five_year order by songCountry";
				query2="select distinct songLanguage from five_year order by songLanguage";
				query3="select distinct category from categories order by category";
			}
		}		
		
		get_country();
		
		s3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            	if(adapterView.getItemAtPosition(i).toString()=="All")
            	{
            		s4.setEnabled(false);
            	}
            	else
            	{
            		s4.setEnabled(true);
                	query4="select subcategory from categories where category='"+adapterView.getItemAtPosition(i).toString()+"' order by subcategory";
                	new AsyncCaller().execute(query4);
            	}
            }

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        });
		
	    ib1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				query_filter="";
				
				boolean y=isInternetOn();
				if(!y)
				{
				    Toast.makeText(AddFilters.this, "No Internet Access. Please Check Your Data Settings and Reload The App.", Toast.LENGTH_LONG).show();
				    return;
				}
				
	        	TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	    		String id=telephonyManager.getDeviceId();
	    		//1-3
	    		if(use1==1&&use2==3)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songCountry='"+s1.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songCountry='"+s1.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(delta) desc";
	    		}
	    		
	    		//1-1
	    		if(use1==1&&use2==1)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_week_2 on one_year_2.youtubeId=one_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_week on one_year.youtubeId=one_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(one_year_week.week_delta) desc";
	    		}

	    		//1-2
	    		if(use1==1&&use2==2)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year_2 inner join one_year_month_2 on one_year_2.youtubeId=one_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year_2.youtubeId in (select songId from one_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from one_year inner join one_year_month on one_year.youtubeId=one_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and one_year.youtubeId in (select songId from one_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(one_year_month.month_delta) desc";
	    		}
	    		
	    		//2-3
	    		if(use1==2&&use2==3)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songCountry='"+s1.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songCountry='"+s1.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(delta) desc";
	    		}

	    		//2-1
	    		if(use1==2&&use2==1)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_week_2 on two_year_2.youtubeId=two_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_week on two_year.youtubeId=two_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(two_year_week.week_delta) desc";
	    		}

	    		//2-2
	    		if(use1==2&&use2==2)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year_2 inner join two_year_month_2 on two_year_2.youtubeId=two_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year_2.youtubeId in (select songId from two_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from two_year inner join two_year_month on two_year.youtubeId=two_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and two_year.youtubeId in (select songId from two_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(two_year_month.month_delta) desc";
	    		}
	    		
	    		//3-3
	    		if(use1==3&&use2==3)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songCountry='"+s1.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songCountry='"+s1.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songCountry='"+s1.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(delta) desc";
	    		}

	    		//3-1
	    		if(use1==3&&use2==1)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_week_2 on five_year_2.youtubeId=five_year_week_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_week on five_year.youtubeId=five_year_week.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(five_year_week.week_delta) desc";
	    		}

	    		//3-2
	    		if(use1==3&&use2==2)
	    		{
		    		if(use==2)
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year_2 inner join five_year_month_2 on five_year_2.youtubeId=five_year_month_2.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year_2.youtubeId in (select songId from five_year_genres_2 where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}	
		    		else
		    		{
			    		if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All" && s3.getSelectedItem().toString()=="All")
			    		{
			    			Intent intent=new Intent(AddFilters.this,Artists.class);
							finish();
							startActivity(intent); 
			    		}
			    		else
			    		{
			    			if(s1.getSelectedItem().toString()=="All" && s2.getSelectedItem().toString()=="All")
			    			{
			    				if(s4.getSelectedItem().toString()=="All")
			    				{
				    				query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";	    					
			    				}
			    				else
			    				{
			    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";	    					
			    				}
			    			}
			    			else
			    			{
			    				if(s1.getSelectedItem().toString()!="All" && s2.getSelectedItem().toString()!="All")
			    	    		{
			    					if(s3.getSelectedItem().toString()=="All")
			    					{
				    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
			    					}
			    					else
			    					{
			    						if(s4.getSelectedItem().toString()=="All")
			    						{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
			    						}
			    						else
			    						{
			    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
			    						}
			    					}
			    	    		}
			    	    		else
			    	    		{
			    	    			if(s1.getSelectedItem().toString()!="All")
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songCountry='"+s1.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    			else
			    	    			{
			    	    				if(s3.getSelectedItem().toString()=="All")
				    					{
					    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"'";	    						
				    					}
				    					else
				    					{
				    						if(s4.getSelectedItem().toString()=="All")
				    						{
						    					query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s3.getSelectedItem().toString()+"')";
				    						}
				    						else
				    						{
				    							query_filter=query_filter+"create view "+id+"ourstars as select artistId from five_year inner join five_year_month on five_year.youtubeId=five_year_month.songId where songLanguage='"+s2.getSelectedItem().toString()+"' and five_year.youtubeId in (select songId from five_year_genres where name='"+s4.getSelectedItem().toString()+"')";			
				    						}
				    					}
			    	    			}
			    	    		}
			    			}
			    		}
		    		}
		    		query_filter=query_filter+" group by artistId order by max(five_year_month.month_delta) desc";
	    		}
	    		
	       		Log.e("Final Query",query_filter);
	       		query_filter=query_filter.replace("\n", "");
	       		query_filter=query_filter.replace("\r", "");
	    		new AsyncCaller().execute(query_filter);	    			
	    	}
		});
	    
	    ib2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(AddFilters.this,Artists.class);
				finish();
				startActivity(intent);
		} 
		});
		
	}
	
	public void get_country()
	{
		new AsyncCaller().execute(query1);
	}
	
	public void get_language()
	{
		dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, country);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setPrompt("Select Country");
		s1.setAdapter(dataAdapter1);
		new AsyncCaller().execute(query2);
	}
	
	public void get_genres()
	{
		dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, language);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s2.setPrompt("Select Language");
		s2.setAdapter(dataAdapter2);
		new AsyncCaller().execute(query3);
	}
	
	public void set_genres()
	{
		dataAdapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, category);
		dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s3.setPrompt("Select Genre");
		s3.setAdapter(dataAdapter3);
		dataAdapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subcategory);
		dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s4.setPrompt("Select Genre Subcategory");
		s4.setAdapter(dataAdapter4);
		pdlg.dismiss();
	}
	
	public void set_genres_subcategory()
	{
		dataAdapter4 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subcategory);
		dataAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s4.setPrompt("Select Genre Subcategory");
		s4.setAdapter(dataAdapter4);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_filters, menu);
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
	
    private class AsyncCaller extends AsyncTask<String, Void, Void>
    {
		//ProgressDialog pdLoading = new ProgressDialog(AddFilters.this);
        
		@Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
    		//pdLoading.setMessage("\tFetching Filters...");
            //pdLoading.show();
        }
        @Override
        protected Void doInBackground(String... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
        	temp=find(params[0]);	
			return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            if(temp==query1)
            {
            	get_language();
            }
            if(temp==query2)
            {
            	get_genres();
            }
            if(temp==query3)
            {
            	set_genres();
            }
            if(temp==query4)
            {
            	set_genres_subcategory();
            }
            if(temp==query_filter)
            {
				prefs=getSharedPreferences(prefName, MODE_PRIVATE);
			    SharedPreferences.Editor editor=prefs.edit();
			    editor.putString("Filter",query_filter);
			    editor.commit();
			    Intent intent= new Intent(AddFilters.this,FilteredArtists.class);
			    finish();
			    startActivity(intent);
            }
            //pdLoading.dismiss();       
        }
    }
    
    public String find(String q)
    {
    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	nameValuePairs.add(new BasicNameValuePair("query",q));
    	
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
        	return "";
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
        	return "";
    	}     
       
        try
    	{
        	//Log.e("Result", result);
        	if(q==query1)
        	{
            	JSONArray json_array = new JSONArray(result);
            	country=new String[json_array.length()+1];
            	country[0]=("All");
            	for(int i=0;i<json_array.length();i++)
            	{
                	JSONObject json_object=json_array.getJSONObject(i);
                	if(json_object.getString("songCountry")=="null")
                	{
                    	country[i+1]="(**Unknown**)";
                	}
                	else
                	{
                    	country[i+1]=json_object.getString("songCountry");                		
                	}
            	}
        	}
        	if(q==query2)
        	{
            	JSONArray json_array = new JSONArray(result);
            	language=new String[json_array.length()+1];
            	language[0]="All";
            	for(int i=0;i<json_array.length();i++)
            	{
                	JSONObject json_object=json_array.getJSONObject(i);
                	if(json_object.getString("songLanguage")=="null")
                	{
                    	language[i+1]="(**Unknown**)";
                	}
                	else
                	{
                    	language[i+1]=json_object.getString("songLanguage");                		
                	}
            	}
        	}
        	if(q==query3)
        	{
            	JSONArray json_array = new JSONArray(result);
            	category=new String[json_array.length()+1];
            	category[0]=("All");
            	for(int i=0;i<json_array.length();i++)
            	{
                	JSONObject json_object=json_array.getJSONObject(i);
                	if(json_object.getString("category")=="null")
                	{
                    	category[i+1]="(**Unknown**)";
                	}
                	else
                	{
                    	category[i+1]=json_object.getString("category");                		
                	}
            	}
        	}
        	if(q==query4)
        	{
            	JSONArray json_array = new JSONArray(result);
            	if(query4.indexOf("Unknown genre")>-1)
            	{
                	subcategory=new String[json_array.length()];
                	subcategory[0]=("All");
            	}
            	else
            	{
            		subcategory=new String[json_array.length()+1];
            		subcategory[0]=("All");
            		for(int i=0;i<json_array.length();i++)
                	{
                    	JSONObject json_object=json_array.getJSONObject(i);
                        subcategory[i+1]=json_object.getString("subcategory");                		
                	}            		
            	}
        	}
        	return q;
        	
    	}
        catch(Exception e)
    	{
        	Log.e("Id doesn't exist.", e.toString());
        	return "";
    	}
    }
    
	public synchronized void  showProgressBar(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(pdlg == null){
                        pdlg = new ProgressDialog(AddFilters.this); 
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